package com.sdv291.common.service;

import com.sdv291.common.Holder;
import com.sdv291.common.exception.CommonException;
import com.sdv291.common.process.Process;
import com.sdv291.common.process.ProcessConfig;
import com.sdv291.common.process.Processes;
import com.sdv291.common.stats.Alarm;
import com.sdv291.common.stats.Language;
import com.sdv291.common.stats.Level;
import com.sdv291.common.stats.Locale;
import com.sdv291.common.stats.Stats;
import com.sdv291.common.stats.StatsConfig;
import com.sdv291.common.util.ConverterUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class StatsService {

  static final String URL = "http://stats.sdv291.com/api/v1/stats";

  protected final StatsConfig config;
  private final Process<Holder<CommonException, Alarm>> process;
  private final long ttlCache = TimeUnit.HOURS.toMillis(1);
  private final ConcurrentMap<String, Message> cacheMessages = new ConcurrentHashMap<>();

  public StatsService(StatsConfig config) {
    this.config = config;
    this.process = Processes.newProcess(
      ProcessConfig.newBuilder()
        .setOverloadThreshold(5)
        .setMaxWorkerCount(3)
        .setMaxPackSize(1)
        .build(),
      holders -> {
        Holder<CommonException, Alarm> holder = holders.get(0);
        try {
          CommonException ex = holder.getFilter();

          Map<String, Object> request = new HashMap<>();
          request.put("stackTrace", Base64.getEncoder().encodeToString(ConverterUtils.toString(ex).getBytes()));
          request.put("alarmCode", ex.getCode() + "." + ex.getId());
          request.put("happened", System.currentTimeMillis());
          request.put("projectId", config.getProjectId());
          request.put("stackTraceFormat", "BASE64");
          request.put("options", ex.getOptions());
          request.put("source", ex.getSource());
          request.put("stackTraceTypeId", 1); // 1 it's java stacktrace type

          Stats stats = this.execute(URL, request);
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

  protected Stats execute(String url, Map<String, Object> request) throws IOException {
    HttpURLConnection conn = null;
    try {
      byte[] body = config.getObjectMapper().writeValueAsBytes(request);

      conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      conn.setInstanceFollowRedirects(false);
      conn.setRequestMethod("POST");
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.getOutputStream().write(body);
      return config.getObjectMapper().readValue(conn.getInputStream(), Stats.class);
    } finally {
      if (Objects.nonNull(conn)) {
        conn.disconnect();
      }
    }
  }

  public final Holder<CommonException, Alarm> async(CommonException ex) {
    Holder<CommonException, Alarm> holder = new Holder<>(ex);
    Alarm alarm = get(ex);
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

  public final Alarm get(CommonException ex) {
    return get(String.join(".", ex.getCode(), String.valueOf(ex.getId())));
  }

  public final Alarm get(String code) {
    Message message = cacheMessages.get(code);
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
