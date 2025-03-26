package com.sdv291.common.funnel;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Request rate limits help prevent overload and ensure
 * optimal resource utilization by all users.
 */
public class FunnelTime {

  private static final long RETRY_INTERVAL = 1000L;

  private final long limit;
  protected final AtomicLong[] counts;
  private volatile int currentSecond = 0;
  private final AtomicBoolean limitReached = new AtomicBoolean(false);

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
      this.limitReached.set(Stream.of(this.counts).mapToLong(AtomicLong::get).sum() >= this.limit);
    }, 1L, 1L, TimeUnit.SECONDS);
  }

  public <T> T execute(Callable<T> callable) throws Exception {
    return execute(callable, 1L);
  }

  public <T> T execute(Callable<T> callable, long quantity) throws Exception {
    if (quantity < 0) {
      quantity = 0;
    }
    while (true) {
      long sum = Stream.of(this.counts).mapToLong(AtomicLong::get).sum();
      this.limitReached.set(sum + quantity >= this.limit);
      // checking eq 0 is mandatory since the quantity may be greater than the limit
      if (Objects.equals(sum, 0L) || sum + quantity <= this.limit) {
        this.counts[this.currentSecond].addAndGet(quantity);
        return callable.call();
      }
      Thread.sleep(RETRY_INTERVAL);
    }
  }

  public boolean isLimitReached() {
    return this.limitReached.get();
  }

  public int getResumeAfter() {
    long limit = 0L;
    int retry = 0;
    int sec = this.currentSecond;
    while (retry < this.counts.length) {
      limit += this.counts[sec--].get();
      if (sec < 0) {
        sec = this.counts.length - 1;
      }
      if (limit >= this.limit) {
        break;
      }
      retry++;
    }
    return this.counts.length - retry;
  }
}
