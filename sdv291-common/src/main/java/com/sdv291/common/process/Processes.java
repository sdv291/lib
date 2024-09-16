package com.sdv291.common.process;

import java.util.List;
import java.util.function.Consumer;

/**
 * Utility methods for {@link Process}.
 * This class supports the following kinds of methods:
 *
 * <ul>
 *   <li> Methods that create and return an {@link Process}
 *        set up with commonly useful configuration settings.</li>
 * </ul>
 *
 * @author Dmitry Salanzhyi
 */
public class Processes {

  /**
   * Creates a process that can automatically scalable for processing data.
   *
   * @param config   the config for a process
   * @param consumer consumer for packet processing
   * @param <E>      the type of elements
   *
   * @return the newly created process
   */
  public static <E> Process<E> newProcess(ProcessConfig config, Consumer<List<E>> consumer) {
    return new AbstractProcess<E>(config) {

      /**
       * {@inheritDoc}
       */
      @Override
      protected void process(List<E> items) {
        consumer.accept(items);
      }
    };
  }
}
