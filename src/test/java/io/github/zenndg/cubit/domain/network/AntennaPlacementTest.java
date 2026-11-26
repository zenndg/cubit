package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.network.AntennaPlacement.Check;
import org.junit.jupiter.api.Test;

class AntennaPlacementTest {
  @Test
  void aLinkedAntennaWithAFreeChannelIsPlaced() {
    assertEquals(
        AntennaPlacement.READY, AntennaPlacement.decide(new Check(true, true, true, false, true)));
    assertTrue(AntennaPlacement.READY.allows());
  }

  @Test
  void theFirstCauseIsTheOneShown() {
    assertEquals(
        AntennaPlacement.NOT_LINKED,
        AntennaPlacement.decide(new Check(false, false, false, true, false)));
    assertEquals(
        AntennaPlacement.NO_SERVER,
        AntennaPlacement.decide(new Check(true, false, false, true, false)));
    assertEquals(
        AntennaPlacement.OTHER_DIMENSION,
        AntennaPlacement.decide(new Check(true, true, false, true, false)));
    assertEquals(
        AntennaPlacement.CHUNK_TAKEN,
        AntennaPlacement.decide(new Check(true, true, true, true, false)));
    assertEquals(
        AntennaPlacement.CHANNELS_FULL,
        AntennaPlacement.decide(new Check(true, true, true, false, false)));
    assertFalse(AntennaPlacement.CHANNELS_FULL.allows());
  }
}
