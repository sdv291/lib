package com.sdv291.common.stats;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Level {

  SYSTEM(1),
  DEVELOPER(2),
  SUPPORT(3),
  CUSTOMER(4),
  LOGGING(5);

  private final int id;

  Level(int id) {
    this.id = id;
  }

  @JsonValue
  public int getId() {
    return id;
  }
}
