package com.sdv291.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

  @Test
  void constants() {
    assertEquals("", StringUtils.EMPTY);
    assertEquals(" ", StringUtils.SPACE);
    assertEquals("  ", StringUtils.DOUBLE_SPACE);
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

  @Test
  void correctSpace() {
    assertEquals("", StringUtils.correctSpace(" "));
    assertEquals("", StringUtils.correctSpace("  "));
    assertEquals(".", StringUtils.correctSpace(" . "));
    assertEquals(".", StringUtils.correctSpace("  .  "));
  }
}
