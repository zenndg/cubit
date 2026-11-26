package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.EnergyShare.Need;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnergyShareTest {
  private static final Energy CHUNK = new Energy(26);
  private static final Energy ROOM = new Energy(5_000);
  private static final Energy SHORT = new Energy(40);
  private static final Energy FULL_ANTENNA = Energy.ZERO;
  private static final Energy ODD = new Energy(1_001);
  private static final Energy SMALL_ROOM = new Energy(100);

  private static long sum(List<Energy> shares) {
    return shares.stream().mapToLong(Energy::amount).sum();
  }

  @Test
  void theChunksComeFirstInTheOrderOfTheChannels() {
    List<Energy> shares =
        EnergyShare.of(SHORT, List.of(new Need(CHUNK, ROOM), new Need(CHUNK, ROOM)));
    assertEquals(CHUNK, shares.get(0));
    assertEquals(new Energy(SHORT.amount() - CHUNK.amount()), shares.get(1));
  }

  @Test
  void theRestIsSharedEvenlyAndNothingIsCreatedNorLost() {
    List<Energy> shares =
        EnergyShare.of(ODD, List.of(new Need(Energy.ZERO, ROOM), new Need(Energy.ZERO, ROOM)));
    assertEquals(ODD.amount(), sum(shares));
    assertEquals(1, Math.abs(shares.get(0).amount() - shares.get(1).amount()));
  }

  @Test
  void whatAFullAntennaLeavesGoesToTheOthers() {
    List<Energy> shares =
        EnergyShare.of(
            ODD,
            List.of(
                new Need(Energy.ZERO, FULL_ANTENNA),
                new Need(Energy.ZERO, SMALL_ROOM),
                new Need(Energy.ZERO, ROOM)));
    assertEquals(Energy.ZERO, shares.get(0));
    assertEquals(SMALL_ROOM, shares.get(1));
    assertEquals(ODD.amount() - SMALL_ROOM.amount(), shares.get(2).amount());
  }

  @Test
  void neverMoreThanTheRoomOfAll() {
    List<Energy> shares = EnergyShare.of(ROOM, List.of(new Need(CHUNK, SMALL_ROOM)));
    assertEquals(SMALL_ROOM, shares.get(0));
    assertEquals(List.of(), EnergyShare.of(ROOM, List.of()));
  }
}
