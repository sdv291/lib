package com.sdv291.exception;

import com.sdv291.util.RegexUtils;
import com.sdv291.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class CommonException extends RuntimeException {

  private static final long serialVersionUID = -5223340198679348323L;

  private final Builder builder;

  private CommonException(Builder builder) {
    super(builder.message, builder.cause);
    this.builder = builder;
  }

  public int getId() {
    return builder.id;
  }

  public ExceptionCode getCode() {
    return builder.code;
  }

  public static CommonException.Builder newBuilder() {
    return CommonException.Builder.create();
  }

  public static CommonException build(Throwable th) {
    String message = th.getMessage();
    return build(th, message, message);
  }

  public static CommonException unimplemented() {
    return newBuilder()
      .setCode(ExceptionCode.UNIMPLEMENTED)
      .build();
  }

  public static CommonException unsupported(Object... objects) {
    return newBuilder()
      .setMessage("%s", objects)
      .setCode(ExceptionCode.UNSUPPORTED)
      .build();
  }

  public static CommonException build(String pattern, Object... objects) {
    return build(null, pattern, objects);
  }

  public static CommonException build(Throwable th, String pattern, Object... objects) {
    Builder builder = Builder.create();
    if (th instanceof CommonException) {
      builder.setCode(((CommonException) th).getCode());
    }
    return builder
      .setMessage(pattern, objects)
      .setCause(th)
      .build();
  }

  public static String toString(Throwable th) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    th.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  public static final class Builder {

    private int id;
    private Throwable cause;
    private String message;
    private ExceptionCode code = ExceptionCode.DEFAULT;

    private Builder() {
    }

    public CommonException.Builder setId(int id) {
      this.id = id;
      return this;
    }

    public CommonException.Builder setCode(ExceptionCode code) {
      if (Objects.nonNull(code)) {
        this.code = code;
      }
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
      if (Objects.nonNull(cause) && !Objects.equals(ExceptionCode.DEFAULT, code)) {
        if (cause instanceof InterruptedException) {
          this.setCode(ExceptionCode.INTERRUPTED);
        }
      }
      return new CommonException(this);
    }
  }
}
