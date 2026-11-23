package io.github.zenndg.cubit.domain.desk;

/** How much of a base material the input has, for the color of its slot (answer 2006). */
public enum MaterialLevel {
  MISSING,
  PARTIAL,
  READY;

  public static MaterialLevel of(int has, int needs) {
    if (has >= needs) {
      return READY;
    }
    return has > 0 ? PARTIAL : MISSING;
  }
}
