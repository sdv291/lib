package com.sdv291.pool;

/**
 * An object that may hold entry until it is closed.
 * The {@link #close()} method of an {@code AutoCloseable}
 * object is called automatically when exiting a try-with-resources
 * block for which the object has been declared in the resource
 * specification header. This construction ensures prompt
 * release, avoiding resource exhaustion exceptions and errors that
 * may otherwise occur.
 *
 * @author Dmitry Salanzhyi
 */
public interface Provider<E> extends AutoCloseable {

  /**
   * @return Entry form pool
   */
  E getEntry();

  /**
   * Retrieve entry into the pool.
   * This method is invoked automatically on objects managed by the
   * try-with-resources statement.
   */
  @Override
  void close();
}
