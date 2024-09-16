package com.sdv291.common.funnel;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class FunnelTime {

  private static final long RETRY_INTERVAL = 100L;

  private final long limit;
  private final AtomicLong[] counts;
  private volatile int currentSecond = 0;

  /**
   * @param limit     the maximum number in a period
   * @param periodSec period in a seconds
   */
  public FunnelTime(long limit, int periodSec) {
    this.limit = limit;
    this.counts = new AtomicLong[periodSec];
    for (int i = 0; i < periodSec; i++) {
      this.counts[i] = new AtomicLong(0L);
    }
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      this.currentSecond = (this.currentSecond + 1) % periodSec;
      this.counts[this.currentSecond].set(0L);
    }, 1, 1, TimeUnit.SECONDS);
  }

  public <T> T execute(Callable<T> callable) throws Exception {
    return execute0(callable, 1);
  }

  protected <T> T execute0(Callable<T> callable, long limit) throws Exception {
    while (true) {
      long sum = Stream.of(this.counts).mapToLong(AtomicLong::get).sum();
      if (Objects.equals(sum, 0L) || sum + limit <= this.limit) {
        this.counts[this.currentSecond].addAndGet(limit);
        return callable.call();
      }
      Thread.sleep(RETRY_INTERVAL);
    }
  }
}
