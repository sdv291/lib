package com.sdv291.common.funnel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunnelTrafficTest {

  @Test
  void _10bp2sec() throws Exception {
    FunnelTraffic funnel = new FunnelTraffic(10, 2);
    for (int i = 0; i < 10; i++) {
      funnel.execute(Object::new, 5);
    }
    assertTrue(funnel.isLimitReached());
    assertTrue(funnel.getResumeAfter() > 0);
  }

  @Test
  void _101bp2sec() throws Exception {
    FunnelTraffic funnel = new FunnelTraffic(101, 2);
    for (int i = 0; i < 20; i++) {
      funnel.execute(Object::new, 5);
    }
    assertFalse(funnel.isLimitReached());
    assertEquals(0, funnel.getResumeAfter());
  }
}
