package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LinkChunkTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String BLANK = " ";
  private static final String NETHER = "minecraft:the_nether";
  private static final int LAST_OF_FIRST = 15;
  private static final int FIRST_OF_SECOND = 16;
  private static final int BEFORE_ZERO = -1;
  private static final LinkChunk ORIGIN = new LinkChunk(OVERWORLD, 0, 0);

  @Test
  void aBlockBelongsToTheChunkOfItsSixteenColumns() {
    assertEquals(ORIGIN, LinkChunk.of(OVERWORLD, LAST_OF_FIRST, 0));
    assertEquals(new LinkChunk(OVERWORLD, 1, 0), LinkChunk.of(OVERWORLD, FIRST_OF_SECOND, 0));
    assertEquals(
        new LinkChunk(OVERWORLD, BEFORE_ZERO, BEFORE_ZERO),
        LinkChunk.of(OVERWORLD, BEFORE_ZERO, BEFORE_ZERO));
  }

  @Test
  void onlyChunksSharingASideInTheSameDimensionAreBeside() {
    assertTrue(ORIGIN.besides(new LinkChunk(OVERWORLD, 1, 0)));
    assertTrue(ORIGIN.besides(new LinkChunk(OVERWORLD, 0, BEFORE_ZERO)));
    assertFalse(ORIGIN.besides(new LinkChunk(OVERWORLD, 1, 1)));
    assertFalse(ORIGIN.besides(ORIGIN));
    assertFalse(ORIGIN.besides(new LinkChunk(OVERWORLD, 2, 0)));
    assertFalse(ORIGIN.besides(new LinkChunk(NETHER, 1, 0)));
  }

  @Test
  void itHoldsTheColumnsOfItsSixteenBlocks() {
    assertTrue(ORIGIN.holds(0, LAST_OF_FIRST));
    assertFalse(ORIGIN.holds(FIRST_OF_SECOND, 0));
    assertFalse(ORIGIN.holds(BEFORE_ZERO, 0));
  }

  @Test
  void aChunkNeedsADimension() {
    assertThrows(IllegalArgumentException.class, () -> new LinkChunk(BLANK, 0, 0));
  }
}
