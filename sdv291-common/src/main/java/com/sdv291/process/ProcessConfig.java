package com.sdv291.process;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProcessConfig {

  private final int maxPackSize;
  private final int maxWorkerCount;
  private final long pollTimeoutMillis;
  private final int overloadThreshold;
  private final ExecutorService executorService;

  private ProcessConfig(Builder builder) {
    this.maxPackSize = builder.maxPackSize;
    this.maxWorkerCount = builder.maxWorkerCount;
    this.pollTimeoutMillis = builder.pollTimeoutMillis;
    this.overloadThreshold = builder.overloadThreshold;
    this.executorService = builder.executorService;
  }

  public int getMaxPackSize() {
    return maxPackSize;
  }

  public int getMaxWorkerCount() {
    return maxWorkerCount;
  }

  public long getPollTimeoutMillis() {
    return pollTimeoutMillis;
  }

  public int getOverloadThreshold() {
    return overloadThreshold;
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  public static Builder newBuilder() {
    return Builder.create();
  }

  public static class Builder {

    private int maxPackSize = AbstractProcess.MINIMUM_PACK_SIZE;
    private int maxWorkerCount = AbstractProcess.MINIMUM_WORKER_COUNT;
    private long pollTimeoutMillis = TimeUnit.SECONDS.toMillis(1);
    private int overloadThreshold = AbstractProcess.MINIMUM_OVERLOAD_THRESHOLD;
    private ExecutorService executorService;

    private Builder() {
    }

    public Builder setMaxPackSize(int maxPackSize) {
      if (AbstractProcess.MINIMUM_PACK_SIZE > maxPackSize) {
        throw new IllegalArgumentException("The 'maxPackSize' value must be greater than 0");
      }
      this.maxPackSize = maxPackSize;
      return this;
    }

    public Builder setMaxWorkerCount(int maxWorkerCount) {
      if (AbstractProcess.MINIMUM_WORKER_COUNT > maxWorkerCount) {
        throw new IllegalArgumentException("The 'maxWorkerCount' value must be greater than 0");
      }
      this.maxWorkerCount = maxWorkerCount;
      return this;
    }

    public Builder setPollTimeoutMillis(long pollTimeoutMillis) {
      if (0 > pollTimeoutMillis) {
        throw new IllegalArgumentException("The 'pollTimeoutMillis' value must be a positive or 0");
      }
      this.pollTimeoutMillis = pollTimeoutMillis;
      return this;
    }

    public Builder setOverloadThreshold(int overloadThreshold) {
      if (AbstractProcess.MINIMUM_OVERLOAD_THRESHOLD > overloadThreshold) {
        throw new IllegalArgumentException("The 'overloadThreshold' value must be greater than 0");
      }
      this.overloadThreshold = overloadThreshold;
      return this;
    }

    public Builder setExecutorService(ExecutorService executorService) {
      if (Objects.isNull(executorService)) {
        throw new NullPointerException("The 'executorService' value must be defined");
      }
      this.executorService = executorService;
      return this;
    }

    private static Builder create() {
      return new Builder();
    }

    public ProcessConfig build() {
      if (Objects.isNull(this.executorService)) {
        this.setExecutorService(Executors.newFixedThreadPool(maxWorkerCount));
      }
      return new ProcessConfig(this);
    }
  }
}
