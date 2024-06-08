package com.sdv291.util;

import java.util.Objects;

public abstract class StringUtils {

  public static final String EMPTY = "";

  public static boolean isEmpty(String value) {
    return Objects.isNull(value) || value.trim().isEmpty();
  }

  public static boolean notEmpty(String value) {
    return !isEmpty(value);
  }
}
