package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;

/**
 * What the buttons of the screen of a Signal Antenna do on the server: the general switch, the
 * switch of the chunk, locked by the config of the world, the wall, the reserve, which only changes
 * with the antenna on, and the arrows of the channel (answers 2213, 2214, 2309 and 2548).
 */
final class AntennaControl {
  static final AntennaControl NONE = new AntennaControl(null);
  private static final int BACK = -1;
  private static final int FORWARD = 1;

  private final SignalAntennaBlockEntity antenna;

  AntennaControl(SignalAntennaBlockEntity antenna) {
    this.antenna = antenna;
  }

  boolean press(int button) {
    if (antenna == null) {
      return false;
    }
    return switch (button) {
      case SignalAntennaMenu.ENABLE_BUTTON -> toggleEnabled();
      case SignalAntennaMenu.WALL_BUTTON -> toggleWall();
      case SignalAntennaMenu.CHUNK_BUTTON -> toggleChunk();
      case SignalAntennaMenu.CHANNEL_BACK_BUTTON -> antenna.stepChannel(BACK);
      case SignalAntennaMenu.CHANNEL_FORWARD_BUTTON -> antenna.stepChannel(FORWARD);
      default -> antenna.enabled() && changeUps(button);
    };
  }

  private boolean toggleEnabled() {
    antenna.setEnabled(!antenna.enabled());
    return true;
  }

  private boolean toggleWall() {
    antenna.setShowWall(!antenna.showsWall());
    return true;
  }

  private boolean toggleChunk() {
    if (!CubitMod.CONFIG.network().chunks().enabled()) {
      return false;
    }
    antenna.setChunkLoading(!antenna.chunkLoading());
    return true;
  }

  private boolean changeUps(int button) {
    switch (button) {
      case SignalAntennaMenu.UPS_LESS_BUTTON -> antenna.setUps(antenna.ups().less());
      case SignalAntennaMenu.UPS_MORE_BUTTON -> antenna.setUps(antenna.ups().more());
      default -> {
        return false;
      }
    }
    return true;
  }
}
