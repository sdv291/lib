package com.sdv291.common.stats;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Type {

  SYSTEM(1),
  INFO(2),
  WARNING(3),
  ERROR(4),
  SUCCESS(5),
  TIMING(6);

  private final int id;

  Type(int id) {
    this.id = id;
  }

  @JsonValue
  public int getId() {
    return id;
  }
}
