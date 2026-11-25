package io.github.zenndg.cubit.domain.robot;

/** A block the Area app of the Tablet marks as a point of an area (answers 1051 and 2240). */
public record AreaCorner(int x, int y, int z) {
  public AreaColumn column() {
    return new AreaColumn(x, z);
  }
}
