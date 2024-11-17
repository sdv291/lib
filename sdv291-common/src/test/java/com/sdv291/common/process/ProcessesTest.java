package com.sdv291.common.process;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ProcessesTest {

  @Test
  void baseProcess() throws InterruptedException {
    Process<Object> process = Processes.newProcess(
      Config.newBuilder().build(),
      (items) -> assertFalse(items.isEmpty())
    );
    process.offer(new Object());
  }
}
