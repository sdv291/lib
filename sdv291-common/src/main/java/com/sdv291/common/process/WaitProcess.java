package com.sdv291.common.process;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class WaitProcess<T> extends AbstractProcess<T> {

  private final AtomicLong counter = new AtomicLong(0);

  public WaitProcess(Config config) {
    super(config);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void process(List<T> items) {
    items.forEach(n -> this.counter.decrementAndGet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean offer(T data, long timeout) throws InterruptedException {
    this.counter.incrementAndGet();
    return super.offer(data, timeout);
  }

  public void waitComplete() throws InterruptedException {
    while (this.counter.get() > 0L) {
      Thread.sleep(1000);
    }
  }
}
