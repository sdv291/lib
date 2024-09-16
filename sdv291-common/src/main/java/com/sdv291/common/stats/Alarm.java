package com.sdv291.common.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Alarm {

  private String id;
  private String code;
  @JsonProperty("typeId")
  private Type type;
  private String projectId;
  private List<Locale> locales;
  private Map<String, Object> properties;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public List<Locale> getLocales() {
    return locales;
  }

  public void setLocales(List<Locale> locales) {
    this.locales = locales;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
