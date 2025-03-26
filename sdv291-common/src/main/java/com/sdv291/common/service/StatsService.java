package com.sdv291.common.service;

import com.sdv291.common.Holder;
import com.sdv291.common.process.Process;
import com.sdv291.common.process.Processes;
import com.sdv291.common.stats.Alarm;
import com.sdv291.common.stats.Criteria;
import com.sdv291.common.stats.Language;
import com.sdv291.common.stats.Level;
import com.sdv291.common.stats.Locale;
import com.sdv291.common.stats.Stats;
import com.sdv291.common.util.ConverterUtils;
import com.sdv291.common.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class StatsService {

  protected final com.sdv291.common.stats.Config config;
  private final Process<Holder<Criteria, Alarm>> process;
  private final long ttlCache = TimeUnit.HOURS.toMillis(1);
  private final ConcurrentMap<String, Message> cacheMessages = new ConcurrentHashMap<>();

  public StatsService(com.sdv291.common.stats.Config config) {
    this.config = config;
    this.process = Processes.newProcess(
      com.sdv291.common.process.Config.newBuilder()
        .setOverloadThreshold(5)
        .setMaxWorkerCount(3)
        .setMaxPackSize(1)
        .build(),
      holders -> {
        Holder<Criteria, Alarm> holder = holders.get(0);
        try {
          Criteria criteria = holder.getFilter();

          String url = this.getUrl(criteria);
          Map<String, String> headers = this.getHeaders(criteria);
          Map<String, Object> request = this.getRequest(criteria);

          Stats stats = this.execute(url, request, headers);
          if (Objects.nonNull(stats)) {
            Alarm alarm = stats.getAlarm();
            holder.successful(alarm);
            Message message = cacheMessages.get(alarm.getCode());
            if (Objects.isNull(message) || message.isExpired()) {
              cacheMessages.put(alarm.getCode(), new Message(alarm));
            }
          }
        } catch (Exception ex) {
          holder.failed(ex);
        }
      }
    );
  }

  private String getUrl(Criteria criteria) throws UnsupportedEncodingException {
    Map<String, String> params = new HashMap<>();
    if (criteria.isRegister() && Objects.nonNull(criteria.getError())) {
      params.put("register", "true");
    }
    if (criteria.isProperties()) {
      params.put("properties", "true");
    }

    String url = config.getUrl();
    StringBuilder sb = new StringBuilder(url);
    if (!params.isEmpty()) {
      if (!url.endsWith("?")) {
        sb.append('?');
      }
      for (Map.Entry<String, String> entry : params.entrySet()) {
        sb.append(entry.getKey());
        sb.append('=');
        sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
        sb.append('&');
      }
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }

  private Map<String, String> getHeaders(Criteria criteria) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Accept", "application/json");
    return headers;
  }

  private Map<String, Object> getRequest(Criteria criteria) {
    Map<String, Object> request = new HashMap<>();
    if (Objects.nonNull(criteria.getError())) {
      request.put("stackTrace", Base64.getEncoder().encodeToString(ConverterUtils.toString(criteria.getError()).getBytes()));
      request.put("stackTraceFormat", "BASE64");
      request.put("stackTraceTypeId", 1); // 1 it's java stacktrace type

      // todo maybe change it to
      /* 'stackTrace':{
           'type':1,
           'format':'BASE64',
           'value':'ENCODED_STACK_TRACE'
       } */
    }
    if (StringUtils.notEmpty(criteria.getSource())) {
      request.put("source", criteria.getSource());
    }
    request.put("alarmCode", StringUtils.isEmpty(criteria.getCode())? "default" : criteria.getCode());
    request.put("happened", System.currentTimeMillis());
    request.put("projectId", config.getProjectId());
    request.put("options", criteria.getOptions());
    return request;
  }

  protected Stats execute(String url,
                          Map<String, Object> body,
                          Map<String, String> headers) throws IOException {
    HttpURLConnection conn = null;
    try {
      URI uri = URI.create(url);
      conn = (HttpURLConnection) uri.toURL().openConnection();
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        conn.setRequestProperty(entry.getKey(), entry.getValue());
      }
      conn.setInstanceFollowRedirects(false);
      conn.setRequestMethod("POST");
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      conn.connect();

      conn.getOutputStream().write(config.getObjectMapper().writeValueAsBytes(body));
      int responseCode = conn.getResponseCode();
      if (!Objects.equals(HttpURLConnection.HTTP_OK, responseCode)) {
        return null;
      }
      return config.getObjectMapper().readValue(conn.getInputStream(), Stats.class);
    } finally {
      if (Objects.nonNull(conn)) {
        conn.disconnect();
      }
    }
  }

  public final Holder<Criteria, Alarm> invoke(Criteria criteria) {
    Holder<Criteria, Alarm> holder = new Holder<>(criteria);
    Alarm alarm = this.get(criteria.getCode());
    if (Objects.nonNull(alarm)) {
      holder.successful(alarm);
    }
    try {
      process.offer(holder, 1);
    } catch (InterruptedException e) {
      holder.failed(e);
    }
    return holder;
  }

  public final Alarm get(String code) {
    Message message = cacheMessages.get(code.toLowerCase());
    if (Objects.nonNull(message)) {
      return message.getAlarm();
    }
    return null;
  }

  public final Locale getLocale(Alarm alarm, Language language, Level level) {
    for (com.sdv291.common.stats.Locale locale : alarm.getLocales()) {
      if (Objects.equals(locale.getLanguage(), language) && Objects.equals(locale.getLevel(), level)) {
        return locale;
      }
    }
    return alarm.getLocales().stream()
      .findFirst()
      .orElse(null);
  }

  class Message {

    private final Alarm alarm;
    private final long expired;

    public Message(Alarm alarm) {
      this.alarm = alarm;
      this.expired = System.currentTimeMillis() + ttlCache;
    }

    public Alarm getAlarm() {
      return alarm;
    }

    public boolean isExpired() {
      return expired < System.currentTimeMillis();
    }
  }
}
