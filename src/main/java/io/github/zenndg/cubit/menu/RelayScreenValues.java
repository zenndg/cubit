package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.SolarReading;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;

/** The numbers of the screen of a Solar Relay. */
final class RelayScreenValues extends NetworkScreenValues {
  static final int NOW = NetworkMenu.FIRST_FREE;
  static final int MOST = 8;
  static final int SKY = 10;
  static final int PANEL = 11;
  static final int PAIRED = 12;
  static final int TIER = 13;
  static final int COUNT = 14;

  private final SolarRelayBlockEntity relay;

  RelayScreenValues(SolarRelayBlockEntity relay) {
    super(COUNT, relay);
    this.relay = relay;
  }

  @Override
  void refresh() {
    super.refresh();
    SolarReading solar = relay.solar();
    put(NOW, solar.given());
    put(MOST, solar.max());
    put(SKY, solar.state().ordinal());
    put(PANEL, solar.panel().ordinal());
    put(PAIRED, relay.server().isPresent());
    put(TIER, relay.tier().ordinal());
  }
}
