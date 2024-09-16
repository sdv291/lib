package com.sdv291.common.util;

import com.sdv291.common.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ConverterUtils {

  private static final Pair<String, Long>[] SIZE_PAIRS = new Pair[]{
    Pair.of("EB", (long) 1024 * 1024 * 1024 * 1024 * 1024 * 1024),
    Pair.of("PB", (long) 1024 * 1024 * 1024 * 1024 * 1024),
    Pair.of("TB", (long) 1024 * 1024 * 1024 * 1024),
    Pair.of("GB", (long) 1024 * 1024 * 1024),
    Pair.of("MB", (long) 1024 * 1024),
    Pair.of("kB", (long) 1024)
  };

  public static String formatTime(long time) {
    List<String> list = new ArrayList<>();

    long cur = time / TimeUnit.DAYS.toMillis(1);
    if (cur > 0) {
      list.add(String.format("%sd.", cur));
    }
    cur = (time / TimeUnit.HOURS.toMillis(1)) % TimeUnit.DAYS.toHours(1);
    if (cur > 0) {
      list.add(String.format("%sh.", cur));
    }
    cur = (time % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1);
    if (cur > 0) {
      list.add(String.format("%smin.", cur));
    }
    cur = (time % TimeUnit.MINUTES.toMillis(1)) / TimeUnit.SECONDS.toMillis(1);
    if (cur > 0) {
      list.add(String.format("%ssec.", cur));
    }
    cur = time % TimeUnit.SECONDS.toMillis(1);
    if (cur > 0 || list.isEmpty()) {
      list.add(String.format("%smls.", cur));
    }
    return String.join(" ", list);
  }

  public static String formatSize(long size) {
    for (Pair<String, Long> pair : SIZE_PAIRS) {
      double value = pair.getRight();
      if (size >= value) {
        return String.format("%.1f-%s", size / value, pair.getLeft());
      }
    }
    return String.format("%s-bytes", size);
  }

  public static String formatExtendedSize(long size) {
    String value = formatSize(size);
    if (size >= 1024) {
      value = String.format("%s (%d bytes)", value, size);
    }
    return value;
  }

  public static String toString(Throwable th) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    th.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}
