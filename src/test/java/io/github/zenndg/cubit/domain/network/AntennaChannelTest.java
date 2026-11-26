package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AntennaChannelTest {
  private static final int BASE_OPEN = 4;
  private static final int ALL = 16;
  private static final int RIGHT = 1;
  private static final int LEFT = -1;
  private static final String UNKNOWN = "teal";
  private static final String LIGHT_BLUE = "light_blue";
  private static final String YELLOW = "yellow";

  @Test
  void theKeysFollowTheIdsInEnglish() {
    assertEquals(LIGHT_BLUE, AntennaChannel.LIGHT_BLUE.key());
    assertEquals(Optional.of(AntennaChannel.YELLOW), AntennaChannel.of(YELLOW));
    assertEquals(Optional.empty(), AntennaChannel.of(UNKNOWN));
    assertEquals(ALL, AntennaChannel.values().length);
  }

  @Test
  void aTierOpensTheFirstChannels() {
    assertTrue(AntennaChannel.CYAN.isOpen(BASE_OPEN));
    assertFalse(AntennaChannel.GRAY.isOpen(BASE_OPEN));
  }

  @Test
  void theLastChannelComesBackWhileItIsFree() {
    Optional<AntennaChannel> last = Optional.of(AntennaChannel.CYAN);
    assertEquals(last, AntennaChannel.pick(last, BASE_OPEN, Set.of(AntennaChannel.BLACK)));
    assertEquals(
        Optional.of(AntennaChannel.BLACK),
        AntennaChannel.pick(last, BASE_OPEN, Set.of(AntennaChannel.CYAN)));
  }

  @Test
  void aClosedLastChannelGivesTheFirstFree() {
    Optional<AntennaChannel> closed = Optional.of(AntennaChannel.YELLOW);
    assertEquals(
        Optional.of(AntennaChannel.BLUE),
        AntennaChannel.pick(closed, BASE_OPEN, Set.of(AntennaChannel.BLACK)));
  }

  @Test
  void fullChannelsGiveNone() {
    Set<AntennaChannel> taken =
        Set.of(
            AntennaChannel.BLACK, AntennaChannel.BLUE, AntennaChannel.BROWN, AntennaChannel.CYAN);
    assertEquals(Optional.empty(), AntennaChannel.pick(Optional.empty(), BASE_OPEN, taken));
  }

  @Test
  void theArrowsSkipTheTakenAndGoRound() {
    Set<AntennaChannel> taken = Set.of(AntennaChannel.BLUE);
    assertEquals(AntennaChannel.BROWN, AntennaChannel.BLACK.step(RIGHT, BASE_OPEN, taken));
    assertEquals(AntennaChannel.CYAN, AntennaChannel.BLACK.step(LEFT, BASE_OPEN, taken));
    Set<AntennaChannel> others =
        Set.of(AntennaChannel.BLUE, AntennaChannel.BROWN, AntennaChannel.CYAN);
    assertEquals(AntennaChannel.BLACK, AntennaChannel.BLACK.step(RIGHT, BASE_OPEN, others));
  }
}
