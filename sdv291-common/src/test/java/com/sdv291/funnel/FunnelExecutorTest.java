package com.sdv291.funnel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class FunnelExecutorTest {

  @Test
  @Timeout(5000)
  void limit() {
    int count = 2;
    AtomicInteger counter = new AtomicInteger(0);
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    FunnelExecutor executor = new FunnelExecutor(count);
    for (int i = 0; i < 10; i++) {
      executorService.submit(() -> {
        try {
          executor.execute(() -> {
            counter.incrementAndGet();
            Thread.sleep(500); // wait to check in main thread how many threads are here
            return counter.decrementAndGet();
          });
        } catch (InterruptedException ignore) {
        }
      });
    }

    do {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (counter.get() > count) { // check how many threads in funnel logic
        throw new RuntimeException("Executors more then allowed " + count);
      }
    } while (counter.get() > 0);
  }
}
