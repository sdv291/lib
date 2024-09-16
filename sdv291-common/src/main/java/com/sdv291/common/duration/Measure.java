package com.sdv291.common.duration;

import com.sdv291.common.util.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

public class Measure {

  static int MAX_NAME_LENGTH = 60;
  static int MAX_PARAM_LENGTH = 20;

  private final String name;
  private final AtomicLong count = new AtomicLong(0);
  private final AtomicLong interactionTime = new AtomicLong(0);

  public static void setMaxNameLength(int maxNameLength) {
    MAX_NAME_LENGTH = maxNameLength;
  }

  public static void setMaxParamLength(int maxParamLength) {
    MAX_PARAM_LENGTH = maxParamLength;
  }

  public Measure(String name) {
    this.name = name;
  }

  public void calcInteractionTime(long start) {
    this.addInteractionTime(System.currentTimeMillis() - start);
  }

  public void addInteractionTime(long elapsedTime) {
    this.interactionTime.addAndGet(elapsedTime);
    this.count.incrementAndGet();
  }

  public long getCount() {
    return this.count.get();
  }

  public long getInteractionTime() {
    return this.interactionTime.get();
  }

  public void reset() {
    this.interactionTime.set(0);
    this.count.set(0);
  }

  public String getMeasure() {
    long avg = 0;
    if (this.getInteractionTime() > 0 && this.getCount() > 0) {
      avg = this.getInteractionTime() / this.getCount();
    }
    return String.join(StringUtils.EMPTY,
      String.format("%1$-" + MAX_NAME_LENGTH + "s", name),
      String.format("%1$-" + MAX_PARAM_LENGTH + "s", this.getInteractionTime()),
      String.format("%1$-" + MAX_PARAM_LENGTH + "s", avg),
      String.valueOf(this.getCount())
    );
  }

  public static String getHeader() {
    return String.join(StringUtils.EMPTY,
      String.format("%1$-" + MAX_NAME_LENGTH + "s", "Name"),
      String.format("%1$-" + MAX_PARAM_LENGTH + "s", "Total time(ms)"),
      String.format("%1$-" + MAX_PARAM_LENGTH + "s", "Average time(ms)"),
      "Exec count"
    );
  }
}
