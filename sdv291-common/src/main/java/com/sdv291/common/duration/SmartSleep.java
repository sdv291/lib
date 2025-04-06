package com.sdv291.common.duration;

import java.util.concurrent.atomic.AtomicLong;

public class SmartSleep {

  private final long sleepMillis;
  private final boolean peerThread;
  private final AtomicLong lastCall;
  private final ThreadLocal<AtomicLong> holder;

  /**
   * Smart sleep instance.
   *
   * @param sleepMillis Sleep millis.
   * @param peerThread If true - sleep will applied peer thread, otherwise for each call.
   */
  public SmartSleep(long sleepMillis, boolean peerThread) {
    this.sleepMillis = sleepMillis;
    this.peerThread = peerThread;

    this.holder = ThreadLocal.withInitial(() -> new AtomicLong(System.currentTimeMillis()));
    this.lastCall = holder.get();
  }

  /**
   * Apply sleep if previous call was before certain time.
   * Sleep time will be calculated as remaining.
   * If the time elapsed since the last call is longer
   * than the sleep time, it will continue without sleep.
   *
   * @throws InterruptedException On error.
   */
  public void apply() throws InterruptedException {
    long currMillis = System.currentTimeMillis();
    long elapsedMillis = currMillis - getLast().getAndSet(currMillis);
    if (elapsedMillis < this.sleepMillis) {
      suspend(this.sleepMillis - elapsedMillis);
    }
  }

  protected void suspend(long millis) throws InterruptedException {
    Thread.sleep(millis);
  }

  private AtomicLong getLast() {
    return peerThread? this.holder.get() : this.lastCall;
  }
}
