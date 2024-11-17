package com.sdv291.common.stats;

import java.util.HashMap;
import java.util.Map;

public class Criteria {

  private String code;
  private String source;
  private Throwable error;
  private Map<String, Object> options = new HashMap<>();

  private boolean register = true;
  private boolean properties = true;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public Map<String, Object> getOptions() {
    return options;
  }

  public void setOptions(Map<String, Object> options) {
    this.options = options;
  }


  public boolean isRegister() {
    return register;
  }

  public void setRegister(boolean register) {
    this.register = register;
  }

  public boolean isProperties() {
    return properties;
  }

  public void setProperties(boolean properties) {
    this.properties = properties;
  }
}
