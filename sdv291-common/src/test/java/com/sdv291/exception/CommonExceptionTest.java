package com.sdv291.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonExceptionTest {

  @Test
  void defaultException() {
    CommonException ex = CommonException.newBuilder().build();
    assertEquals(ExceptionCode.DEFAULT, ex.getCode());
    assertEquals(0, ex.getId());
    assertNull(ex.getMessage());
  }

  @Test
  void unimplementedException() {
    CommonException ex = CommonException.unimplemented();
    assertEquals(ExceptionCode.UNIMPLEMENTED, ex.getCode());
    assertEquals(0, ex.getId());
  }

  @Test
  void unsupportedException() {
    CommonException ex = CommonException.unsupported();
    assertEquals(ExceptionCode.UNSUPPORTED, ex.getCode());
    assertEquals(0, ex.getId());
  }

  @Test
  void stringException() {
    CommonException ex = CommonException.build("test");
    assertNotNull(CommonException.toString(ex));
  }
}
