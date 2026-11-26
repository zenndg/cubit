package io.github.zenndg.cubit.domain.network;

/**
 * A block linked to a Cubit by the Network app, an antenna, a Dock or a chest, by its dimension and
 * its place. It is kept by its place, so a block broken and placed back there counts again (detail
 * 22 of the Network).
 */
public record LinkSpot(String dimension, int x, int y, int z) {
  private static final String NO_DIMENSION = "A linked block needs a dimension";

  public LinkSpot {
    if (dimension == null || dimension.isBlank()) {
      throw new IllegalArgumentException(NO_DIMENSION);
    }
  }

  public LinkChunk chunk() {
    return LinkChunk.of(dimension, x, z);
  }
}
