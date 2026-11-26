package io.github.zenndg.cubit.domain.network;

/**
 * The chunk of a block linked to a Cubit by the Network app, by its dimension and its chunk
 * numbers. Two antennas of a Cubit are neighbours only when their chunks share a side (answer
 * 2257).
 */
public record LinkChunk(String dimension, int x, int z) {
  public static final int SIDE = ChunkWall.SIDE;
  private static final String NO_DIMENSION = "A chunk needs a dimension";

  public LinkChunk {
    if (dimension == null || dimension.isBlank()) {
      throw new IllegalArgumentException(NO_DIMENSION);
    }
  }

  /** The chunk of the block at these columns. */
  public static LinkChunk of(String dimension, int blockX, int blockZ) {
    return new LinkChunk(dimension, Math.floorDiv(blockX, SIDE), Math.floorDiv(blockZ, SIDE));
  }

  /** In the same dimension and sharing a side: not the same chunk, nor one at a corner. */
  public boolean besides(LinkChunk other) {
    return dimension.equals(other.dimension) && Math.abs(x - other.x) + Math.abs(z - other.z) == 1;
  }

  /** Whether the column of a block, in its dimension, is in this chunk. */
  public boolean holds(int blockX, int blockZ) {
    return Math.floorDiv(blockX, SIDE) == x && Math.floorDiv(blockZ, SIDE) == z;
  }
}
