package com.sdv291.common.duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Duration {

  private final String name;
  private final AtomicLong count = new AtomicLong(0);
  private final AtomicLong interactionTime = new AtomicLong(0);
  private final Map<String, AtomicInteger> threadNames = new HashMap<>();
  private final AtomicInteger maxPackSize = new AtomicInteger(1);
  private final AtomicInteger processedCount = new AtomicInteger(0);

  public Duration(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void calcInteractionTime(long start) {
    this.addInteractionTime(System.currentTimeMillis() - start);
  }

  public void calcInteractionTime(long start, int packSize) {
    this.addInteractionTime(System.currentTimeMillis() - start, packSize);
  }

  public void addInteractionTime(long durationTime) {
    this.addInteractionTime(durationTime, 1);
  }

  public void addInteractionTime(long elapsedTime, int packSize) {
    String threadName = Thread.currentThread().getName();
    AtomicInteger counter = this.threadNames.get(threadName);
    if (Objects.isNull(counter)) {
      counter = new AtomicInteger(0);
      this.threadNames.put(threadName, counter);
    }
    counter.incrementAndGet();
    this.interactionTime.addAndGet(elapsedTime);
    this.count.incrementAndGet();
    this.processedCount.addAndGet(packSize);
    if (packSize > this.maxPackSize.get()) {
      this.maxPackSize.set(packSize);
    }
  }

  /**
   * @return Count of calls.
   */
  public long getCount() {
    return this.count.get();
  }

  /**
   * @return Total interaction time in millis.
   */
  public long getInteractionTime() {
    return this.interactionTime.get();
  }

  /**
   * @return Maximum package size.
   */
  public int getMaxPackSize() {
    return this.maxPackSize.get();
  }

  /**
   * @return Percentage of the number of maximum size packets sent.
   */
  public double getEffectivity() {
    if (this.processedCount.get() == 0) {
      return 0d;
    }
    return (this.processedCount.get() / (double) (this.getCount() * this.maxPackSize.get())) * 100;
  }

  /**
   * @return Thread names which handle this duration. Where "key" is thread name and "value" is count of calls.
   */
  public Map<String, AtomicInteger> getThreadNames() {
    return this.threadNames;
  }

  public void reset() {
    this.interactionTime.set(0);
    this.count.set(0);
    this.maxPackSize.set(0);
    this.processedCount.set(0);
    this.threadNames.clear();
  }
}
