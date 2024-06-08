package com.sdv291;

/**
 * Bi pair instance.
 *
 * @param <L> {@inheritDoc}
 * @param <M> the type of the middle argument
 * @param <R> {@inheritDoc}
 */
public class BiPair<L, M, R> extends Pair<L, R> {

  private final M middle;

  private BiPair(L left, M middle, R right) {
    super(left, right);
    this.middle = middle;
  }

  public M getMiddle() {
    return middle;
  }

  public static <L, M, R> BiPair<L, M, R> of(L left, M middle, R right) {
    return new BiPair<>(left, middle, right);
  }
}
