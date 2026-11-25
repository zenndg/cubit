package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarReading;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import java.util.Optional;

/** The numbers of the screen of a Network Server. */
final class ServerScreenValues extends NetworkScreenValues {
  static final int SUN = NetworkMenu.FIRST_FREE;
  static final int SUN_MOST = 8;
  static final int BANKS = 10;
  static final int CHUNK_COST = 12;
  static final int CHUNK_STATE = 14;
  static final int CHUNK_ON = 15;
  static final int CHUNKS_HELD = 16;
  static final int UPS = 17;
  static final int COUNT = 18;

  private final NetworkServerBlockEntity server;

  ServerScreenValues(NetworkServerBlockEntity server) {
    super(COUNT, server);
    this.server = server;
  }

  @Override
  void refresh() {
    super.refresh();
    Optional<SolarReading> sun = server.relay().map(SolarRelayBlockEntity::solar);
    put(SUN, sun.map(SolarReading::given).orElse(Energy.ZERO));
    put(SUN_MOST, sun.map(SolarReading::max).orElse(server.spec().relay().dayPerTick()));
    put(BANKS, server.bankGiven());
    put(CHUNK_COST, server.chunkCost());
    put(CHUNK_STATE, server.chunkState().ordinal());
    put(CHUNK_ON, server.chunkLoading());
    put(CHUNKS_HELD, server.chunksHeld());
    put(UPS, server.ups().percent());
  }
}
