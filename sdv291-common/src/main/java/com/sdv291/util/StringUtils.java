package com.sdv291.util;

import java.util.Objects;

public abstract class StringUtils {

  public static final String EMPTY = "";
  public static final String SPACE = " ";
  public static final String DOUBLE_SPACE = "  ";

  public static boolean isEmpty(String value) {
    return Objects.isNull(value) || value.trim().isEmpty();
  }

  public static boolean notEmpty(String value) {
    return !isEmpty(value);
  }

  public static String correctSpace(String value) {
    if (isEmpty(value)) {
      return EMPTY;
    }
    if (value.contains(DOUBLE_SPACE)) {
      value = value.replaceAll("\\s+", SPACE);
    }
    return value.trim();
  }
}
