package io.github.zenndg.cubit.client;

/** Which row of a list shows first, when the list has more rows than the screen. */
final class ScrollRows {
  private final int visible;
  private int first;

  ScrollRows(int visible) {
    this.visible = visible;
  }

  /** The first row to show, kept inside a list that may have become shorter. */
  int first(int total) {
    first = Math.clamp(first, 0, Math.max(0, total - visible));
    return first;
  }

  void scroll(double delta, int total) {
    first -= (int) Math.signum(delta);
    first(total);
  }

  boolean overflows(int total) {
    return total > visible;
  }
}
