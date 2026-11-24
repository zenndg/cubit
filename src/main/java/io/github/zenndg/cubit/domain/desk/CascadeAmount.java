package io.github.zenndg.cubit.domain.desk;

/**
 * How many items a plan is asked for, from zero, as a chosen item starts, to the largest order of
 * the tier (answers 2011 and 2053).
 */
public record CascadeAmount(int value) {
  public static final CascadeAmount NONE = new CascadeAmount(0);
  private static final String NEGATIVE = "An amount cannot be negative: ";

  public CascadeAmount {
    if (value < 0) {
      throw new IllegalArgumentException(NEGATIVE + value);
    }
  }

  public boolean isNone() {
    return value == 0;
  }

  public CascadeAmount adjust(int delta, int max) {
    return new CascadeAmount(Math.clamp((long) value + delta, 0, max));
  }
}
