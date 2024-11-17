package com.sdv291.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommonExceptionTest {

  @Test
  void defaultException() {
    CommonException ex = CommonException.newBuilder().build();
    assertEquals(0, ex.getId());
    assertNull(ex.getMessage());
    assertNull(ex.getCode());
  }
}
