package com.sdv291.common.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexUtilsTest {

  @Test
  void splitByLength() {
    String value = "Hello world!";
    assertEquals(
      Arrays.toString(new String[]{"Hel", "lo ", "wor", "ld!"}),
      Arrays.toString(RegexUtils.splitFixedLength(value, 3))
    );
    assertEquals(
      Arrays.toString(new String[]{"Hello", " worl", "d!"}),
      Arrays.toString(RegexUtils.splitFixedLength(value, 5))
    );
  }

  @Test
  void splitCamelCase() {
    String value = "HelloWorld";
    assertEquals(
      Arrays.toString(new String[]{"Hello", "World"}),
      Arrays.toString(RegexUtils.splitCamelCase(value))
    );
  }

  @Test
  void clearHtml() {
    String value = "test";
    String content = "<div id='id1'><div id='id2'>" + value + "</div><img src='url'/></div>";
    assertEquals(value, RegexUtils.clearHtml(content));
  }

  @Test
  void correctSpace() {
    assertEquals("", RegexUtils.correctSpace(null));
    assertEquals("", RegexUtils.correctSpace(" "));
    assertEquals("", RegexUtils.correctSpace("  "));
    assertEquals(".", RegexUtils.correctSpace(" . "));
    assertEquals(".", RegexUtils.correctSpace("  .  "));
    assertEquals("test test", RegexUtils.correctSpace(" test  test "));
  }

  @Test
  void removeNumbers() {
    assertEquals("test", RegexUtils.removeNumbers("test123"));
  }

  @Test
  void numbersOnly() {
    assertEquals("123", RegexUtils.numbersOnly("test123"));
  }
}
