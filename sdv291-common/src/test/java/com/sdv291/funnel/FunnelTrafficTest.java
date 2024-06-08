package com.sdv291.funnel;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunnelTrafficTest {

  @Test
  void _10bp5sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTraffic funnelTime = new FunnelTraffic(10, 5);
    for (int i = 0; i < 10; i++) {
      funnelTime.execute(Object::new, 2);
    }
    assertEquals(5, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }

  @Test
  void _100bp5sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTraffic funnelTime = new FunnelTraffic(100, 5);
    for (int i = 0; i < 20; i++) {
      funnelTime.execute(Object::new, 10);
      Thread.sleep(500);
    }
    assertEquals(10, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }

  @Test
  void _200bp5sec() throws Exception {
    long start = System.currentTimeMillis();
    FunnelTraffic funnelTime = new FunnelTraffic(200, 5);
    funnelTime.execute(Object::new, 200);
    for (int i = 0; i < 4; i++) {
      funnelTime.execute(Object::new, 50);
    }
    assertEquals(5, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
  }
}
