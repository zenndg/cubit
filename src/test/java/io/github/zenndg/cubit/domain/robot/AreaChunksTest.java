package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.network.LinkChunk;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class AreaChunksTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final int LAST = 15;
  private static final int NEXT = 16;
  private static final int FAR = 40;
  private static final int RING = 3;
  private static final AreaChunks TWO =
      AreaChunks.of(
          OVERWORLD, List.of(new LinkChunk(OVERWORLD, 0, 0), new LinkChunk(OVERWORLD, 1, 0)));

  @Test
  void withoutAntennasItDrawsAnywhere() {
    assertSame(AreaChunks.ANYWHERE, AreaChunks.of(OVERWORLD, List.of()));
    assertTrue(AreaChunks.ANYWHERE.holds(new AreaColumn(FAR, FAR)));
  }

  @Test
  void onlyTheColumnsOfItsChunksAndOfThisDimension() {
    assertTrue(TWO.holds(new AreaColumn(0, LAST)));
    assertTrue(TWO.holds(new AreaColumn(NEXT, 0)));
    assertFalse(TWO.holds(new AreaColumn(0, NEXT)));
    AreaChunks elsewhere = AreaChunks.of(NETHER, List.of(new LinkChunk(OVERWORLD, 0, 0)));
    assertFalse(elsewhere.holds(new AreaColumn(0, 0)));
  }

  @Test
  void aLineHoldsWhenEachOfItsBlocksDoes() {
    assertTrue(TWO.holdsLine(new AreaColumn(0, 0), new AreaColumn(LAST + NEXT, 0)));
    assertFalse(TWO.holdsLine(new AreaColumn(0, 0), new AreaColumn(0, NEXT)));
    assertTrue(TWO.holdsLine(new AreaColumn(1, 1), new AreaColumn(1, 1)));
  }

  @Test
  void aRingOfChunksDoesNotHoldTheHoleInItsMiddle() {
    List<LinkChunk> ring = new ArrayList<>();
    for (int x = 0; x < RING; x++) {
      for (int z = 0; z < RING; z++) {
        if (x != 1 || z != 1) {
          ring.add(new LinkChunk(OVERWORLD, x, z));
        }
      }
    }
    int far = RING * LinkChunk.SIDE - 1;
    AreaPath around =
        new AreaPath(
            List.of(
                new AreaColumn(0, 0),
                new AreaColumn(far, 0),
                new AreaColumn(far, far),
                new AreaColumn(0, far)));
    AreaShape shape = AreaShape.of(around).orElseThrow();
    assertFalse(AreaChunks.of(OVERWORLD, ring).holdsShape(shape));
    assertTrue(AreaChunks.ANYWHERE.holdsShape(shape));
    AreaPath inside =
        new AreaPath(
            List.of(
                new AreaColumn(0, 0),
                new AreaColumn(LAST, 0),
                new AreaColumn(LAST, LAST),
                new AreaColumn(0, LAST)));
    assertTrue(TWO.holdsShape(AreaShape.of(inside).orElseThrow()));
  }
}
