package io.github.zenndg.cubit.domain.desk;

/** What a plan uses of an item: a base material, or a part already made (2006 and 2052). */
public record CascadeNeed(String item, int count, boolean ready) {
  private static final String EMPTY = "A need has at least one item: ";

  public CascadeNeed {
    if (count < 1) {
      throw new IllegalArgumentException(EMPTY + item);
    }
  }
}
