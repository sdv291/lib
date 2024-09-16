package com.sdv291.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

  @Test
  void constants() {
    assertEquals("", StringUtils.EMPTY);
  }

  @Test
  void isEmpty() {
    assertTrue(StringUtils.isEmpty(null));
    assertTrue(StringUtils.isEmpty(""));
    assertTrue(StringUtils.isEmpty(" "));
    assertTrue(StringUtils.isEmpty("  "));
  }

  @Test
  void notEmpty() {
    assertTrue(StringUtils.notEmpty("null"));
    assertTrue(StringUtils.notEmpty(" . "));
  }
}
