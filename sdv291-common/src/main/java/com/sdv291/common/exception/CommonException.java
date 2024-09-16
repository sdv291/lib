package com.sdv291.common.exception;

import com.sdv291.common.service.StatsService;
import com.sdv291.common.stats.Alarm;
import com.sdv291.common.stats.Language;
import com.sdv291.common.stats.Level;
import com.sdv291.common.stats.Locale;
import com.sdv291.common.util.RegexUtils;
import com.sdv291.common.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CommonException extends RuntimeException {

  private static final long serialVersionUID = -5223340198679348323L;
  private static final String DEFAULT_CODE = "default";

  private static StatsService STATS_SERVICE;

  public static void setStatsService(StatsService statsService) {
    STATS_SERVICE = statsService;
  }

  private final Builder builder;

  protected CommonException(Builder builder) {
    super(builder.cause);
    this.builder = builder;

    if (Objects.nonNull(STATS_SERVICE)) {
      STATS_SERVICE.async(this);
    }
  }

  public int getId() {
    return builder.id;
  }

  public String getRequestId() {
    return builder.requestId;
  }

  public String getCode() {
    return StringUtils.isEmpty(builder.code)? DEFAULT_CODE : builder.code;
  }

  public String getSource() {
    return StringUtils.isEmpty(builder.source)? "undefined" : builder.source;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    return this.getMessage(Language.ENGLISH, Level.DEVELOPER);
  }

  public String getMessage(Language language, Level level) {
    if (Objects.nonNull(STATS_SERVICE)) {
      Alarm alarm = this.getAlarm();
      if (Objects.nonNull(alarm)) {
        Locale locale = STATS_SERVICE.getLocale(alarm, language, level);
        String message = locale.getText();
        for (Map.Entry<String, Object> entry : builder.options.entrySet()) {
          message = message.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return message;
      }
    }
    return builder.message;
  }

  public Map<String, Object> getOptions() {
    return builder.options;
  }

  public Alarm getAlarm() {
    Alarm alarm = STATS_SERVICE.get(this);
    if (Objects.isNull(alarm)) {
      alarm = STATS_SERVICE.get(DEFAULT_CODE);
    }
    return alarm;
  }

  public static CommonException cast(Throwable th) {
    if (th instanceof CommonException) {
      return (CommonException) th;
    }
    return CommonException.build(th);
  }

  public static CommonException.Builder newBuilder() {
    return CommonException.Builder.create();
  }

  public static CommonException build(Throwable th) {
    String message = th.getMessage();
    return build(th, message, message);
  }

  public static CommonException build(String pattern, Object... objects) {
    return build(null, pattern, objects);
  }

  public static CommonException build(Throwable th, String pattern, Object... objects) {
    Builder builder = newBuilder();
    if (th instanceof CommonException) {
      builder.setCode(((CommonException) th).getCode());
    }
    return builder
      .setMessage(pattern, objects)
      .setCause(th)
      .build();
  }

  public static final class Builder {

    private int id;
    private String requestId;
    private String code;
    private String source;
    private String message;
    private Throwable cause;
    private final Map<String, Object> options = new HashMap<>();

    private Builder() {
    }

    public CommonException.Builder setId(int id) {
      this.id = id;
      return this;
    }

    public CommonException.Builder setRequestId(String requestId) {
      this.requestId = requestId;
      return this;
    }

    public CommonException.Builder setCode(String code) {
      this.code = code;
      return this;
    }

    public CommonException.Builder setSource(String source) {
      this.source = source;
      return this;
    }

    public CommonException.Builder addOption(String key, Object value) {
      this.options.put(key, value);
      return this;
    }

    public CommonException.Builder setCause(Throwable cause) {
      if (Objects.nonNull(cause)) {
        this.cause = cause;
        if (StringUtils.isEmpty(this.message)) {
          this.setMessage(cause.getMessage());
        }
      }
      return this;
    }

    public CommonException.Builder setMessage(String pattern, Object... objects) {
      if (Objects.nonNull(pattern)) {
        String message = pattern;
        if (pattern.contains("{}") && objects.length > 0) {
          pattern = pattern.replace("{}", "%s");
        }
        if (pattern.contains("%s") && objects.length > 0) {
          message = String.format(pattern, objects);
        }
        message = RegexUtils.correctSpace(message);
        if (!StringUtils.isEmpty(message)) {
          switch (message.charAt(message.length() - 1)) {
            case '.':
            case '?':
            case '!':
              break;

            default:
              message += '.';
              break;
          }
        }
        this.message = message;
      }
      return this;
    }

    private static CommonException.Builder create() {
      return new CommonException.Builder();
    }

    public CommonException build() {
      if (StringUtils.isEmpty(requestId)) {
        this.setRequestId(UUID.randomUUID().toString());
      }
      return new CommonException(this);
    }
  }
}
