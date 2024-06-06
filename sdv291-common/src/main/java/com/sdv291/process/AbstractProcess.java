package com.sdv291.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractProcess<T> implements Process<T> {

  static final int MINIMUM_PACK_SIZE = 1;
  static final int MINIMUM_WORKER_COUNT = 1;
  static final int MINIMUM_OVERLOAD_THRESHOLD = 1;

  private volatile boolean allowTerminate;
  private final ProcessConfig config;
  private final Overload overload;

  private final Runnable worker;
  private final AtomicInteger workerCount = new AtomicInteger(0);

  private final BlockingQueue<T> queue;

  public AbstractProcess(ProcessConfig config) {
    this.config = config;
    this.overload = new Overload(config.getOverloadThreshold());

    this.queue = new LinkedBlockingQueue<>();
    this.worker = worker();
  }

  public final boolean offer(T item, long timeout) throws InterruptedException {
    if (Objects.isNull(item)) {
      throw new NullPointerException();
    }
    this.allowTerminate = false;
    try {
      if (this.overload.isOccurred() || Objects.equals(this.workerCount.get(), 0)) {
        this.workerCount.incrementAndGet();
        this.config.getExecutorService().submit(this.worker);
      }
      return this.queue.offer(item, timeout, TimeUnit.MILLISECONDS);
    } finally {
      allowTerminate = true;
    }
  }

  protected abstract void process(List<T> items);

  private Runnable worker() {
    return () -> {
      List<T> items = new ArrayList<>(this.config.getMaxPackSize());

      while (true) {
        items.clear();
        fillItems(items);

        if (items.isEmpty()) {
          if (isAdditionalWorker()) {
            this.workerCount.decrementAndGet();
            break;
          }
          continue;
        }

        process(items);
      }
    };
  }

  private void fillItems(List<T> items) {
    do {
      T item;
      try {
        item = this.queue.poll(this.config.getPollTimeoutMillis(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }

      if (Objects.isNull(item)) {
        this.overload.reset();
        break;
      }
      items.add(item);
      if (items.size() >= this.config.getMaxPackSize()) {
        if (this.workerCount.get() < this.config.getMaxWorkerCount() && this.overload.test()) {
          // An overload has occurred
        }
        break;
      }
    } while (true);
  }

  private boolean isAdditionalWorker() {
    if (!this.allowTerminate || Objects.equals(this.workerCount.get(), this.config.isAllowSuspendWorkers()? 0 : 1)) {
      return false;
    }
    return this.queue.isEmpty();
  }

  static class Overload {

    private final int threshold;
    private final AtomicBoolean state = new AtomicBoolean(false);
    private final AtomicInteger count = new AtomicInteger(0);

    private Overload(int threshold) {
      this.threshold = threshold;
    }

    public boolean test() {
      if (this.count.incrementAndGet() < this.threshold) {
        return false;
      }
      this.reset();
      this.state.set(true);
      return true;
    }

    public boolean isOccurred() {
      return this.state.getAndSet(false);
    }

    public void reset() {
      this.count.set(0);
    }
  }
}
