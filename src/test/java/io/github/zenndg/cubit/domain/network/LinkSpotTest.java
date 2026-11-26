package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LinkSpotTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final String BLANK = "";
  private static final int X = 33;
  private static final int Y = 64;
  private static final int Z = -5;

  @Test
  void aSpotKnowsItsChunk() {
    assertEquals(LinkChunk.of(OVERWORLD, X, Z), new LinkSpot(OVERWORLD, X, Y, Z).chunk());
  }

  @Test
  void aSpotNeedsADimension() {
    assertThrows(IllegalArgumentException.class, () -> new LinkSpot(BLANK, X, Y, Z));
  }
}
