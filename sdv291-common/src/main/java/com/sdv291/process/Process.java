package com.sdv291.process;

@FunctionalInterface
public interface Process<T> {

  long UNLIMITED_TIMEOUT = 0L;

  default boolean offer(T holder) throws InterruptedException {
    return offer(holder, UNLIMITED_TIMEOUT);
  }

  boolean offer(T holder, long timeout) throws InterruptedException;
}
