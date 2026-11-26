package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.network.DockFeed.Need;
import java.util.List;
import org.junit.jupiter.api.Test;

class DockFeedTest {
  private static final Energy CAPACITY = new Energy(600_000);
  private static final Percentage FLOOR = new Percentage(5);
  private static final Energy FLOOR_ENERGY = new Energy(30_000);
  private static final UpsReserve HALF = UpsReserve.of(50);
  private static final Energy HALF_ENERGY = new Energy(300_000);
  private static final Energy ROOM = new Energy(5_000);
  private static final Energy SMALL_ROOM = new Energy(1_000);
  private static final Energy SHORT = new Energy(3_001);
  private static final Energy PLENTY = new Energy(20_000);
  private static final Energy EXTRA = new Energy(1_234);

  private static long sum(List<Energy> shares) {
    return shares.stream().mapToLong(Energy::amount).sum();
  }

  @Test
  void onlyWhatPassesTheUpsOrTheFloorGoesToTheDocks() {
    assertEquals(EXTRA, DockFeed.spare(HALF_ENERGY.plus(EXTRA), CAPACITY, HALF, FLOOR));
    assertEquals(Energy.ZERO, DockFeed.spare(HALF_ENERGY, CAPACITY, HALF, FLOOR));
    assertEquals(EXTRA, DockFeed.spare(FLOOR_ENERGY.plus(EXTRA), CAPACITY, UpsReserve.NONE, FLOOR));
    assertEquals(Energy.ZERO, DockFeed.spare(FLOOR_ENERGY, CAPACITY, UpsReserve.NONE, FLOOR));
  }

  @Test
  void docksChargingACubitComeFirst() {
    List<Energy> shares = DockFeed.of(SHORT, List.of(new Need(false, ROOM), new Need(true, ROOM)));
    assertEquals(Energy.ZERO, shares.get(0));
    assertEquals(SHORT, shares.get(1));
  }

  @Test
  void theRestGoesEvenlyEachUpToItsRoomWithoutCreatingNorLosing() {
    List<Energy> shares =
        DockFeed.of(
            PLENTY,
            List.of(new Need(true, SMALL_ROOM), new Need(false, ROOM), new Need(false, ROOM)));
    assertEquals(SMALL_ROOM, shares.get(0));
    assertEquals(ROOM, shares.get(1));
    assertEquals(ROOM, shares.get(2));
    List<Energy> even = DockFeed.of(SHORT, List.of(new Need(false, ROOM), new Need(false, ROOM)));
    assertEquals(SHORT.amount(), sum(even));
    assertEquals(1, Math.abs(even.get(0).amount() - even.get(1).amount()));
  }

  @Test
  void noDocksTakeNothing() {
    assertEquals(List.of(), DockFeed.of(PLENTY, List.of()));
  }
}
