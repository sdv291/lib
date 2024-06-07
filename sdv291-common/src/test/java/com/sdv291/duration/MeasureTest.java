package com.sdv291.duration;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeasureTest {

  @Test
  void maxNameLength() {
    assertEquals(60, Measure.MAX_NAME_LENGTH);
    Measure.setMaxNameLength(100);
    assertEquals(100, Measure.MAX_NAME_LENGTH);
  }

  @Test
  void maxParamLength() {
    assertEquals(20, Measure.MAX_PARAM_LENGTH);
    Measure.setMaxParamLength(10);
    assertEquals(10, Measure.MAX_PARAM_LENGTH);
  }

  @Test
  void header() {
    Measure.setMaxNameLength(10);
    Measure.setMaxParamLength(20);
    assertEquals("Name      Total time(ms)      Average time(ms)    Exec count", Measure.getHeader());
  }

  @Test
  void measure() {
    Measure.setMaxNameLength(10);
    Measure.setMaxParamLength(10);

    int count = 3;
    Measure measure = new Measure("test");
    for (int i = 0; i < count; i++) {
      measure.addInteractionTime(TimeUnit.SECONDS.toMillis(1));
    }
    assertEquals(TimeUnit.SECONDS.toMillis(count), measure.getInteractionTime());
    assertEquals(count, measure.getCount());
    assertEquals("test      3000      1000      3", measure.getMeasure());

    measure.reset();

    assertEquals(0, measure.getInteractionTime());
    assertEquals(0, measure.getCount());
    assertEquals("test      0         0         0", measure.getMeasure());
  }
}
