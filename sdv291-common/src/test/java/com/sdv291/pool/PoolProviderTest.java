package com.sdv291.pool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PoolProviderTest {

  @Test
  void notEq() {
    PoolProvider<Object> provider = new PoolProvider<>(Object::new);
    try (Provider<Object> providerEntry = provider.get()) {
      assertNotEquals(new Object(), providerEntry.getEntry());
    }
  }

  @Test
  void eq() {
    Object entry = new Object();
    PoolProvider<Object> provider = new PoolProvider<>(() -> entry);
    try (Provider<Object> providerEntry = provider.get()) {
      assertEquals(entry, providerEntry.getEntry());
    }
  }
}
