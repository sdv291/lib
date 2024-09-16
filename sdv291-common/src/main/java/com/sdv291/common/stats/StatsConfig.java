package com.sdv291.common.stats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdv291.common.util.StringUtils;

import java.util.Objects;

public class StatsConfig {

  private final String projectId;
  private final ObjectMapper objectMapper;

  private StatsConfig(StatsConfig.Builder builder) {
    this.projectId = builder.projectId;
    this.objectMapper = builder.objectMapper;
  }

  public String getProjectId() {
    return projectId;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public static StatsConfig.Builder newBuilder() {
    return StatsConfig.Builder.create();
  }

  public static class Builder {

    private String projectId;
    private ObjectMapper objectMapper;

    private Builder() {
    }

    public StatsConfig.Builder setProjectId(String projectId) {
      if (StringUtils.isEmpty(projectId)) {
        throw new IllegalArgumentException("The 'projectId' should be defined");
      }
      this.projectId = projectId;
      return this;
    }

    public StatsConfig.Builder setObjectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }

    private static StatsConfig.Builder create() {
      return new StatsConfig.Builder();
    }

    public StatsConfig build() {
      if (Objects.isNull(objectMapper)) {
        objectMapper = new ObjectMapper();
      }
      return new StatsConfig(this);
    }
  }
}
