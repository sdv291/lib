package com.sdv291.funnel;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FunnelExecutor {

  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private final AtomicInteger count = new AtomicInteger(0);
  private final int limit;

  public FunnelExecutor(int limit) {
    this.limit = limit;
  }

  public <T> T execute(Callable<T> callable) throws InterruptedException {
    this.lock.lock();
    try {
      while (this.count.get() >= this.limit) {
        this.condition.await();
      }
      this.count.incrementAndGet();
    } finally {
      this.lock.unlock();
    }
    try {
      return callable.call();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      this.lock.lock();
      try {
        this.count.decrementAndGet();
        this.condition.signal();
      } finally {
        this.lock.unlock();
      }
    }
  }
}
