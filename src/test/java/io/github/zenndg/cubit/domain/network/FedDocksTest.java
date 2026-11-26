package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class FedDocksTest {
  private static final String OVERWORLD = "minecraft:overworld";
  private static final int Y = 64;
  private static final int OTHER_X = 4;
  private static final LinkSpot DOCK = new LinkSpot(OVERWORLD, 0, Y, 0);
  private static final LinkSpot OTHER = new LinkSpot(OVERWORLD, OTHER_X, Y, 0);

  @Test
  void aDockIsFedOnceAndUntilItIsTakenOut() {
    FedDocks fed = FedDocks.NONE.with(DOCK).with(DOCK).with(OTHER);
    assertEquals(List.of(DOCK, OTHER), fed.docks());
    assertTrue(fed.feeds(DOCK));
    FedDocks left = fed.without(DOCK);
    assertFalse(left.feeds(DOCK));
    assertEquals(left, left.without(DOCK));
  }

  @Test
  void aRepeatedSavedDockCountsOnce() {
    assertEquals(List.of(DOCK), new FedDocks(List.of(DOCK, DOCK)).docks());
  }
}
