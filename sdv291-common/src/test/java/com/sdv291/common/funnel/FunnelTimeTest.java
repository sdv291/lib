package com.sdv291.common.funnel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class FunnelTimeTest {

  @Test
  void _5rp5sec() throws Exception {
    FunnelTime funnel = new FunnelTime(5, 5);
    for (int i = 0; i < 5; i++) {
      funnel.execute(Object::new);
    }
    assertTrue(funnel.isLimitReached());
    assertTrue(funnel.getResumeAfter() > 0);
  }

  @Test
  void _10rp2sec() throws Exception {
    FunnelTime funnel = new FunnelTime(10, 2);
    for (int i = 0; i < 20; i++) {
      funnel.execute(Object::new);
    }
    assertTrue(funnel.isLimitReached());
    assertTrue(funnel.getResumeAfter() > 0);
  }

  @Test
  void _4rp2sec() throws Exception {
    FunnelTime funnel = new FunnelTime(4, 2);
    for (int i = 0; i < 4; i++) {
      funnel.execute(Object::new);
    }
    assertTrue(funnel.isLimitReached());
    assertTrue(funnel.getResumeAfter() > 0);
    Thread.sleep(2500);
    assertFalse(funnel.isLimitReached());
    assertEquals(0, funnel.getResumeAfter());
  }
}
