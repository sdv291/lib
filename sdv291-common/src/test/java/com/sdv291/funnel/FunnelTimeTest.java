package com.sdv291.funnel;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunnelTimeTest {

  @Test
  void _5rp5sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTime funnelTime = new FunnelTime(5, 5);
    for (int i = 0; i < 10; i++) {
      funnelTime.execute(Object::new);
    }
    assertEquals(5, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }

  @Test
  void _10rp5sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTime funnelTime = new FunnelTime(10, 5);
    for (int i = 0; i < 20; i++) {
      funnelTime.execute(Object::new);
      Thread.sleep(500);
    }
    assertEquals(10, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }

  @Test
  void _4rp8sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTime funnelTime = new FunnelTime(4, 8);
    funnelTime.execute(Object::new);
    Thread.sleep(7000);
    funnelTime.execute(Object::new);
    for (int i = 0; i < 4; i++) {
      funnelTime.execute(Object::new);
    }
    assertEquals(15, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }
}
