package com.sdv291.common.process;

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

  private final Runnable worker;
  private final BlockingQueue<T> queue;

  /**
   * Determine if the worker can be destroyed at this time.
   */
  private volatile boolean allowWorkerDestroy;
  private final Config config;

  /**
   * Overload state {@code true} if overload has occurred
   * and the max number of workers has not been reached.
   */
  private final AtomicBoolean overloadState = new AtomicBoolean(false);
  /**
   * Count how many times in a row the max batch size was reached.
   */
  private final AtomicInteger overloadCount = new AtomicInteger(0);
  /**
   * Count of alive workers.
   */
  private final AtomicInteger workerCount = new AtomicInteger(0);

  public AbstractProcess(Config config) {
    this.config = config;
    this.queue = new LinkedBlockingQueue<>();
    this.worker = this.worker();
  }

  private Runnable worker() {
    return () -> {
      List<T> items = new ArrayList<>(this.config.getMaxPackSize());

      while (true) {
        items.clear();
        this.prepareItems(items);

        if (items.isEmpty()) {
          if (this.allowWorkerDestroy && this.queue.isEmpty()) {
            this.workerCount.decrementAndGet();
            break;
          }
          continue;
        }

        this.process(items);
      }
    };
  }

  private void prepareItems(List<T> items) {
    do {
      T item;
      try {
        item = this.queue.poll(this.config.getPollTimeoutMillis(), TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        throw new RuntimeException(ex);
      }
      if (Objects.isNull(item)) {
        // reset overload because queue is empty
        this.overloadCount.set(0);
        break;
      }
      items.add(item);

      if (items.size() >= this.config.getMaxPackSize()) {
        // the packet is of max size, it may cause overload
        if (this.workerCount.get() < this.config.getMaxWorkerCount() && this.testOverload()) {
          // the process does not have a max count of workers and
          // the max batch size has been reached several times in a row
          this.onOverloadOccurs();
          this.createWorker();
        }
        break;
      }
    } while (true);
  }

  /**
   * Proceed items in async.
   *
   * @param items Batch of items.
   */
  protected abstract void process(List<T> items);

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean offer(T data, long timeout) throws InterruptedException {
    if (Objects.isNull(data)) {
      throw new NullPointerException();
    }
    this.allowWorkerDestroy = false;
    try {
      this.createWorker();
      return this.queue.offer(data, timeout, TimeUnit.MILLISECONDS);
    } finally {
      this.allowWorkerDestroy = true;
    }
  }

  private void createWorker() {
    if (!this.overloadState.getAndSet(false) && this.workerCount.get() > 0) {
      // if there is no overload and there is at least one alive worker
      return;
    }
    if (this.workerCount.incrementAndGet() <= this.config.getMaxWorkerCount()) {
      this.config.getExecutorService().submit(() -> {
        this.onCreateWorker();
        try {
          this.worker.run();
        } finally {
          this.onDestroyWorker();
        }
      });
    }
  }

  /**
   * Invoked when a new worker is created.
   */
  protected void onCreateWorker() {
  }

  /**
   * Invoked when a worker is destroyed.
   */
  protected void onDestroyWorker() {
  }

  /**
   * Invoked when an overload occurs.
   */
  protected void onOverloadOccurs() {
  }

  public boolean testOverload() {
    if (this.overloadCount.incrementAndGet() < this.config.getOverloadThreshold()) {
      return false;
    }
    this.overloadState.set(true);
    this.overloadCount.set(0);
    return true;
  }
}
