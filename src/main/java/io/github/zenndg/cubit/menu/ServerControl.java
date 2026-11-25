package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;

/**
 * What the buttons of the Energy tab do on the server: the switch of the chunk, locked by the
 * config of the world, and the reserve, which only changes with the chunk on (answers 2213, 2214,
 * 2219).
 */
final class ServerControl {
  static final ServerControl NONE = new ServerControl(null);

  private final NetworkServerBlockEntity server;

  ServerControl(NetworkServerBlockEntity server) {
    this.server = server;
  }

  boolean press(int button) {
    if (server == null || !CubitMod.CONFIG.network().chunks().enabled()) {
      return false;
    }
    if (button == NetworkServerMenu.CHUNK_BUTTON) {
      server.setChunkLoading(!server.chunkLoading());
      return true;
    }
    return server.chunkLoading() && changeUps(button);
  }

  private boolean changeUps(int button) {
    switch (button) {
      case NetworkServerMenu.UPS_LESS_BUTTON -> server.setUps(server.ups().less());
      case NetworkServerMenu.UPS_MORE_BUTTON -> server.setUps(server.ups().more());
      default -> {
        return false;
      }
    }
    return true;
  }
}
