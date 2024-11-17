package com.sdv291.common.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdv291.common.util.StringUtils;

import java.util.Objects;

public class Config {

  private static final String PATH = "/api/v1/stats";
  private static final String URL = "http://stats.sdv291.com";

  private final String url;
  private final String projectId;
  private final ObjectMapper objectMapper;

  private Config(Config.Builder builder) {
    this.url = builder.getUrl();
    this.projectId = builder.getProjectId();
    this.objectMapper = builder.getObjectMapper();
  }

  public String getUrl() {
    return url;
  }

  public String getProjectId() {
    return projectId;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public static Config.Builder newBuilder() {
    return Config.Builder.create();
  }

  public static class Builder {

    private String url;
    private String projectId;
    private ObjectMapper objectMapper;

    private Builder() {
    }

    private String getUrl() {
      return (StringUtils.isEmpty(url)? URL : url) + PATH;
    }

    public Config.Builder setUrl(String url) {
      if (StringUtils.isEmpty(url)) {
        throw new IllegalArgumentException("The 'url' should be defined");
      }
      this.url = url;
      return this;
    }

    private String getProjectId() {
      return projectId;
    }

    public Config.Builder setProjectId(String projectId) {
      if (StringUtils.isEmpty(projectId)) {
        throw new IllegalArgumentException("The 'projectId' should be defined");
      }
      this.projectId = projectId;
      return this;
    }

    private ObjectMapper getObjectMapper() {
      return Objects.isNull(objectMapper)? new ObjectMapper() : objectMapper;
    }

    public Config.Builder setObjectMapper(ObjectMapper objectMapper) {
      if (Objects.isNull(objectMapper)) {
        throw new IllegalArgumentException("The 'objectMapper' should be defined");
      }
      this.objectMapper = objectMapper;
      return this;
    }

    private static Config.Builder create() {
      return new Config.Builder();
    }

    public Config build() {
      return new Config(this);
    }
  }
}
