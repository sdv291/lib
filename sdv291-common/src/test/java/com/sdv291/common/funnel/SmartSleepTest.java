package com.sdv291.common.funnel;

import com.sdv291.common.duration.SmartSleep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class SmartSleepTest {

  @Test
  @Timeout(2500)
  void sleep2sec() throws Exception {
    SmartSleep smartSleep = new SmartSleep(2000, false);
    smartSleep.apply();
    smartSleep.apply();
  }
}
