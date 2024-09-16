package com.sdv291.common.util;

public abstract class RegexUtils {

  public static String[] splitFixedLength(String value, int length) {
    if (StringUtils.isEmpty(value)) {
      return new String[0];
    }
    return value.split("(?<=\\G.{" + length + "})");
  }

  public static String[] splitCamelCase(String value) {
    if (StringUtils.isEmpty(value)) {
      return new String[0];
    }
    return value.split("(?=\\p{Lu})");
  }

  public static String clearHtml(String value) {
    if (StringUtils.isEmpty(value)) {
      return StringUtils.EMPTY;
    }
    return value.replaceAll("<[^>]*>", StringUtils.EMPTY);
  }

  public static String removeNumbers(String value) {
    if (StringUtils.isEmpty(value)) {
      return StringUtils.EMPTY;
    }
    return value.replaceAll("\\d", StringUtils.EMPTY);
  }

  public static String numbersOnly(String value) {
    if (StringUtils.isEmpty(value)) {
      return StringUtils.EMPTY;
    }
    return value.replaceAll("\\D", StringUtils.EMPTY);
  }

  public static String correctSpace(String value) {
    if (StringUtils.isEmpty(value)) {
      return StringUtils.EMPTY;
    }
    if (value.contains("  ")) {
      value = value.replaceAll("\\s+", " ");
    }
    return value.trim();
  }
}
