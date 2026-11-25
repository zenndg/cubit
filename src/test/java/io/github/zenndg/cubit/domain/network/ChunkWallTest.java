package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChunkWallTest {
  private static final int FLOOR = 63;
  private static final int MIDDLE = 8;
  private static final ChunkWall WALL = ChunkWall.around(MIDDLE, FLOOR + 1, MIDDLE);

  private static ChunkWall.Viewer at(double x) {
    return new ChunkWall.Viewer(true, true, x, FLOOR + 1, MIDDLE);
  }

  @Test
  void itGoesAroundTheChunkFromTheSupportBlock() {
    assertEquals(new ChunkWall(0, 0, FLOOR, FLOOR + 1 + ChunkWall.HEIGHT), WALL);
    assertEquals(ChunkWall.SIDE, WALL.maxX());
    assertEquals(ChunkWall.SIDE, WALL.maxZ());
  }

  @Test
  void aNegativePlaceHasItsOwnChunk() {
    ChunkWall wall = ChunkWall.around(-1, FLOOR + 1, -ChunkWall.SIDE);
    assertEquals(-ChunkWall.SIDE, wall.minX());
    assertEquals(0, wall.maxX());
    assertEquals(-ChunkWall.SIDE, wall.minZ());
  }

  @Test
  void theOwnerWithTheTabletSeesItWithinReach() {
    assertTrue(WALL.seenBy(at(MIDDLE)));
    assertTrue(WALL.seenBy(at(ChunkWall.SIDE + ChunkWall.REACH)));
    assertFalse(WALL.seenBy(at(ChunkWall.SIDE + ChunkWall.REACH + 1)));
    assertFalse(WALL.seenBy(at(-ChunkWall.REACH - 1)));
  }

  @Test
  void theHeightCountsToo() {
    double far = FLOOR - ChunkWall.REACH - 1;
    assertFalse(WALL.seenBy(new ChunkWall.Viewer(true, true, MIDDLE, far, MIDDLE)));
  }

  @Test
  void nobodyElseSeesItNorTheOwnerWithoutTheTablet() {
    assertFalse(WALL.seenBy(new ChunkWall.Viewer(false, true, MIDDLE, FLOOR, MIDDLE)));
    assertFalse(WALL.seenBy(new ChunkWall.Viewer(true, false, MIDDLE, FLOOR, MIDDLE)));
  }

  @Test
  void aWallHasAHeight() {
    assertThrows(IllegalArgumentException.class, () -> new ChunkWall(0, 0, FLOOR, FLOOR));
  }
}
