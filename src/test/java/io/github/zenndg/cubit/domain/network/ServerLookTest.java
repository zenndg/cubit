package io.github.zenndg.cubit.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ServerLookTest {
  private static final int BARS = 10;
  private static final ServerLook.Columns FULL = new ServerLook.Columns(BARS, BARS);

  private static ServerLook look(int channels) {
    return new ServerLook(true, true, true, FULL, channels);
  }

  @Test
  void showsTheColumnsAndTheChannels() {
    assertEquals(ServerSpec.MAX_CHANNELS, look(ServerSpec.MAX_CHANNELS).channels());
    assertEquals(FULL, look(0).columns());
    assertEquals(new ServerLook.Columns(0, 0), ServerLook.OFF.columns());
  }

  @Test
  void rejectsWhatTheFrontDoesNotHave() {
    assertThrows(IllegalArgumentException.class, () -> look(-1));
    assertThrows(IllegalArgumentException.class, () -> look(ServerSpec.MAX_CHANNELS + 1));
    assertThrows(IllegalArgumentException.class, () -> new ServerLook.Columns(-1, 0));
    assertThrows(IllegalArgumentException.class, () -> new ServerLook.Columns(0, BARS + 1));
  }
}
