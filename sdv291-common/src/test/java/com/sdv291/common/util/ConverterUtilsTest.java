package com.sdv291.common.util;

import com.sdv291.common.exception.CommonException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConverterUtilsTest {

  @Test
  void formatTime() {
    assertEquals("1d.", ConverterUtils.formatTime(TimeUnit.DAYS.toMillis(1L)));
    assertEquals("1h.", ConverterUtils.formatTime(TimeUnit.HOURS.toMillis(1L)));
    assertEquals("1min.", ConverterUtils.formatTime(TimeUnit.MINUTES.toMillis(1L)));
    assertEquals("1sec.", ConverterUtils.formatTime(TimeUnit.SECONDS.toMillis(1L)));
    assertEquals("1mls.", ConverterUtils.formatTime(TimeUnit.MILLISECONDS.toMillis(1L)));
    assertEquals("0mls.", ConverterUtils.formatTime(0));
  }

  @Test
  void formatSize() {
    assertEquals("0-bytes", ConverterUtils.formatSize(0));
    assertEquals("1.0-kB", ConverterUtils.formatSize(1024));
    assertEquals("1.0-MB", ConverterUtils.formatSize((long) 1024 * 1024));
    assertEquals("1.0-GB", ConverterUtils.formatSize((long) 1024 * 1024 * 1024));
    assertEquals("1.0-TB", ConverterUtils.formatSize((long) 1024 * 1024 * 1024 * 1024));
    assertEquals("1.0-PB", ConverterUtils.formatSize((long) 1024 * 1024 * 1024 * 1024 * 1024));
    assertEquals("1.0-EB", ConverterUtils.formatSize((long) 1024 * 1024 * 1024 * 1024 * 1024 * 1024));
  }

  @Test
  void stringException() {
    CommonException ex = CommonException.build("test");
    assertNotNull(ConverterUtils.toString(ex));
  }
}
