package com.sdv291;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class HolderTest {

  private final Object filter = new Object();
  private final Boolean result = Boolean.TRUE;

  @Test
  void notFinished() {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    assertFalse(holder.isFinished());
  }

  @Test
  void successfulFinished() {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    holder.successful(result);
    assertTrue(holder.isFinished());
  }

  @Test
  void failedFinished() {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    holder.failed(new Exception());
    assertTrue(holder.isFinished());
  }

  @Test
  void successful() throws Exception {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    holder.successful(result);

    assertEquals(result, holder.getResult());
    assertTrue(holder::isFinished);
    assertTrue(holder::isOk);
    assertFalse(holder::isError);
  }

  @Test
  void failed() {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    holder.failed(new Exception());

    assertTrue(holder::isFinished);
    assertTrue(holder::isError);
    assertFalse(holder::isOk);
    assertThrows(Exception.class, holder::getResult);
  }

  @Test
  void timeout() throws Exception {
    Holder<Object, Boolean> holder = new Holder<>(filter);
    assertEquals(holder.getFilter(), filter);
    assertNull(holder.getResult(1000));
    assertTimeout(Duration.ofSeconds(1), () -> holder.getResult(100));
    assertFalse(holder::isFinished);
  }
}
