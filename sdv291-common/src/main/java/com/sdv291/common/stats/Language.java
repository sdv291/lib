package com.sdv291.common.stats;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {

  ENGLISH(1),
  RUSSIAN(2),
  UKRAINIAN(3),
  ESPANOL(4);

  private final int id;

  Language(int id) {
    this.id = id;
  }

  @JsonValue
  public int getId() {
    return id;
  }
}
