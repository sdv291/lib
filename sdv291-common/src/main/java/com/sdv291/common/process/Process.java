package com.sdv291.common.process;

/**
 * Process interface for easy operation.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #offer(Object, long)}.
 *
 * @param <T> the type of the input to the operation
 *
 * @author Dmitry Salanzhyi
 */
@FunctionalInterface
public interface Process<T> {

  long UNLIMITED_TIMEOUT = 0L;

  /**
   * Add the specified data into the process.
   *
   * @param data the element to add for async handle
   *
   * @return {@code true} if the element was added to this process,
   * otherwise {@code false}
   *
   * @throws InterruptedException if interrupted while waiting
   * @throws NullPointerException if the specified element is null
   */
  default boolean offer(T data) throws InterruptedException {
    return offer(data, UNLIMITED_TIMEOUT);
  }

  /**
   * Add the specified data into the process, waiting up to the
   * specified wait time if necessary for space to become available.
   *
   * @param data    the element to add for async handle
   * @param timeout timeout in millis
   *
   * @return {@code true} if the element was added to this process,
   * otherwise {@code false}
   *
   * @throws InterruptedException if interrupted while waiting
   * @throws NullPointerException if the specified element is null
   */
  boolean offer(T data, long timeout) throws InterruptedException;
}
