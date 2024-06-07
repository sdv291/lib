package com.sdv291.pool;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class PoolProvider<E> {

  private final Supplier<E> supplier;
  private final Queue<Provider<E>> pool = new ConcurrentLinkedQueue<>();
  private final ThreadLocal<Provider<E>> providerEntry = new ThreadLocal<>();

  public PoolProvider(Supplier<E> supplier) {
    this.supplier = supplier;
  }

  public Provider<E> get() {
    Provider<E> provider = this.pool.poll();
    try {
      if (Objects.isNull(provider)) {
        provider = getProvider();
      }
      return provider;
    } finally {
      this.providerEntry.set(provider);
    }
  }

  private Provider<E> getProvider() {
    return new Provider<E>() {

      /**
       * {@inheritDoc}
       */
      @Override
      public E getEntry() {
        return supplier.get();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void close() {
        Provider<E> provider = providerEntry.get();
        if (Objects.nonNull(provider)) {
          pool.offer(provider);
        }
      }
    };
  }
}
