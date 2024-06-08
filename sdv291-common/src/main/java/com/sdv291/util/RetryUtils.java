package com.sdv291.util;

import com.sdv291.exception.CommonException;
import com.sdv291.exception.ExceptionCode;

import java.util.Objects;
import java.util.concurrent.Callable;

public abstract class RetryUtils {

  public static <T> T retry(int attempts, long delayBetweenRetryMillis, Callable<T> call) {
    if (attempts <= 0) {
      attempts = 1;
    }
    while (true) {
      try {
        return call.call();
      } catch (Throwable th) {
        if (Objects.equals(attempts--, 0)) {
          throw CommonException.newBuilder()
            .setCode(ExceptionCode.RETRY_REACHED)
            .setCause(th)
            .build();
        }
      }
      if (delayBetweenRetryMillis > 0) {
        try {
          Thread.sleep(delayBetweenRetryMillis);
        } catch (InterruptedException ex) {
          throw CommonException.build(ex);
        }
      }
    }
  }
}
