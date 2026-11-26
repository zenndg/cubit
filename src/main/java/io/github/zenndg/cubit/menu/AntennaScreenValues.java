package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.AntennaPlacing;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;

/**
 * The numbers of the screen of a Signal Antenna. A channel and a tier go with one more than their
 * order, so zero means none. The distance goes in two values, as the energy, because it may pass
 * the 16 bits of one.
 */
final class AntennaScreenValues extends NetworkScreenValues {
  static final int CHANNEL = NetworkMenu.FIRST_FREE;
  static final int SIGNAL = 7;
  static final int MINUTES_LEFT = 8;
  static final int DISTANCE = 9;
  static final int SPENDING = 11;
  static final int ENABLED = 13;
  static final int CHUNK_ON = 14;
  static final int WALL = 15;
  static final int CHUNK_STATE = 16;
  static final int CHUNKS_HELD = 17;
  static final int UPS = 18;
  static final int SERVER_TIER = 19;
  static final int LINKED_CUBITS = 20;
  static final int LINKED_DOCKS = 21;
  static final int COUNT = 22;
  static final int NONE = 0;

  private final SignalAntennaBlockEntity antenna;
  private final AntennaCubits cubits = new AntennaCubits();

  AntennaScreenValues(SignalAntennaBlockEntity antenna) {
    super(COUNT, antenna);
    this.antenna = antenna;
  }

  @Override
  void refresh() {
    super.refresh();
    put(CHANNEL, antenna.link().channel().map(channel -> channel.ordinal() + 1).orElse(NONE));
    put(SIGNAL, antenna.signal().ordinal());
    put(MINUTES_LEFT, antenna.minutesLeft());
    put(DISTANCE, new Energy(antenna.distance()));
    put(SPENDING, antenna.spending());
    put(ENABLED, antenna.enabled());
    put(CHUNK_ON, antenna.chunkLoading());
    put(WALL, antenna.showsWall());
    put(CHUNK_STATE, antenna.chunkState().ordinal());
    put(CHUNKS_HELD, antenna.chunksHeld());
    put(UPS, antenna.ups().percent());
    put(LINKED_CUBITS, cubits.count(antenna));
    put(LINKED_DOCKS, antenna.fedDocks().size());
    put(
        SERVER_TIER,
        AntennaPlacing.serverOf(antenna).map(server -> server.tier().ordinal() + 1).orElse(NONE));
  }
}
