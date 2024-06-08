package com.sdv291;

/**
 * Pair instance.
 *
 * @param <L> the type of the left argument
 * @param <R> the type of the right argument
 */
public class Pair<L, R> {

  private final L left;
  private final R right;

  protected Pair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }

  public static <L, R> Pair<L, R> of(L left, R right) {
    return new Pair<>(left, right);
  }
}
