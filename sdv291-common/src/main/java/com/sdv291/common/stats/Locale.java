package com.sdv291.common.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Locale {

  private String id;
  @JsonProperty("levelId")
  private Level level;
  @JsonProperty("languageId")
  private Language language;
  private String title;
  private String text;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
