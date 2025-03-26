package com.sdv291.common.funnel;

import java.util.concurrent.Callable;

public class FunnelTraffic extends FunnelTime {

  private final long bytesLimit;

  /**
   * @param bytesLimit the maximum number of bytes that can be transferred in a period
   * @param periodSec  period in a seconds
   */
  public FunnelTraffic(long bytesLimit, int periodSec) {
    super(bytesLimit, periodSec);
    this.bytesLimit = bytesLimit;
  }

  /**
   * This method is unsupported for traffic funnel.
   *
   * @deprecated use {@link #execute(Callable, long)} instead
   */
  @Override
  @Deprecated
  public <T> T execute(Callable<T> callable) {
    throw new UnsupportedOperationException();
  }

  /**
   * Perform processing taking into account traffic restrictions for the specified period.
   *
   * @param callable callable handler
   * @param bytes    size of transferring bytes
   * @param <T>      the type of the operation result
   *
   * @return execution result
   *
   * @throws Exception if something wrong
   */
  public <T> T execute(Callable<T> callable, long bytes) throws Exception {
    if (bytes > this.bytesLimit && isTooLarge()) {
      return null;
    }
    return super.execute(callable, bytes);
  }

  protected boolean isTooLarge() {
    // you can override this and define here throwable if needed
    return false;
  }
}
