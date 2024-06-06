package com.sdv291;

import java.util.Objects;

public class Holder<I, O> {

  private final I filter;
  private volatile O result;
  private volatile Exception error;
  private volatile boolean finished = false;

  public Holder(I filter) {
    this.filter = filter;
  }

  public I getFilter() {
    return this.filter;
  }

  public boolean isFinished() {
    return this.finished;
  }

  public boolean isOk() {
    if (!this.isFinished()) {
      throw new IllegalStateException("The result has not yet been received");
    }
    return Objects.isNull(this.error);
  }

  public boolean isError() {
    return !isOk();
  }

  public O getResult() throws Exception {
    return this.getResult(0);
  }

  public O getResult(long timeout) throws Exception {
    if (!this.finished) {
      try {
        synchronized (this) {
          if (!this.finished) {
            this.wait(timeout);
          }
        }
      } catch (InterruptedException ex) {
        this.failed(ex);
      }
    }
    if (Objects.nonNull(this.error)) {
      throw this.error;
    }
    return this.result;
  }

  public void successful(O result) {
    this.setResult(result, null);
  }

  public void failed(Exception ex) {
    this.setResult(null, ex);
  }

  private void setResult(O result, Exception ex) {
    if (!this.finished) {
      synchronized (this) {
        if (!this.finished) {
          this.error = ex;
          this.result = result;
          this.finished = true;
          this.notifyAll();
        }
      }
    }
  }
}
