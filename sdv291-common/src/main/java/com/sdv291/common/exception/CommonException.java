package com.sdv291.common.exception;

import com.sdv291.common.util.RegexUtils;
import com.sdv291.common.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CommonException extends RuntimeException {

  private static final long serialVersionUID = -5223340198679348323L;

  private final Builder builder;

  private CommonException(Builder builder) {
    super(builder.message, builder.cause);
    this.builder = builder;
  }

  public int getId() {
    return builder.id;
  }

  public String getCode() {
    return builder.code;
  }

  public Map<String, Object> getOptions() {
    return builder.options;
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
    private String code;
    private String message;
    private Throwable cause;
    private final Map<String, Object> options = new HashMap<>();

    private Builder() {
    }

    public CommonException.Builder setId(int id) {
      this.id = id;
      return this;
    }

    public CommonException.Builder setCode(String code) {
      this.code = code;
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
      return new CommonException(this);
    }
  }
}
