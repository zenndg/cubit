package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.network.EnergyShare;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;

/**
 * How a Network Server feeds its antennas through its Solar Relay: what it may send in a tick goes
 * only to the antennas with signal, shared by {@link EnergyShare} in the order of the channels, and
 * the Server loses exactly what they kept (Delivery 4, detail 16, and rule 13). The antennas are
 * looked for once a second, only in loaded chunks (rules 21 and 22).
 */
final class AntennaFeed {
  private static final int LOOK_TICKS = 20;

  private List<SignalAntennaBlockEntity> antennas = List.of();
  private int listed;
  private int receiving;
  private Energy sent = Energy.ZERO;
  private int untilLook;

  /** The stock after this tick, with what the antennas kept taken from it. */
  EnergyStock tick(ServerLevel level, UUID server, EnergyStock stock, Energy offered) {
    if (untilLook <= 0) {
      untilLook = LOOK_TICKS;
      look(level, server);
    }
    untilLook--;
    List<SignalAntennaBlockEntity> live =
        antennas.stream().filter(SignalAntennaBlockEntity::receivesFromNetwork).toList();
    List<EnergyShare.Need> needs = new ArrayList<>(live.size());
    live.forEach(
        antenna -> needs.add(new EnergyShare.Need(Energy.ZERO, antenna.roomFromNetwork())));
    List<Energy> shares = EnergyShare.of(offered.min(stock.energy().charge()), needs);
    Energy kept = Energy.ZERO;
    int fed = 0;
    for (int index = 0; index < live.size(); index++) {
      Energy accepted = live.get(index).takeFromNetwork(shares.get(index));
      kept = kept.plus(accepted);
      fed += accepted.isZero() ? 0 : 1;
    }
    sent = kept;
    receiving = fed;
    return stock.spend(kept).stock();
  }

  /** Its antennas of the same dimension, loaded, by the order of their channels. */
  private void look(ServerLevel level, UUID server) {
    List<NetworkRoster.Antenna<GlobalPos>> mine =
        NetworkData.of(level.getServer()).roster().antennas().stream()
            .filter(antenna -> antenna.server().equals(server))
            .sorted(Comparator.comparing(NetworkRoster.Antenna::channel))
            .toList();
    listed = mine.size();
    List<SignalAntennaBlockEntity> found = new ArrayList<>(mine.size());
    for (NetworkRoster.Antenna<GlobalPos> antenna : mine) {
      GlobalPos place = antenna.place();
      if (place.dimension().equals(level.dimension())
          && level.isLoaded(place.pos())
          && level.getBlockEntity(place.pos()) instanceof SignalAntennaBlockEntity block) {
        found.add(block);
      }
    }
    antennas = List.copyOf(found);
  }

  /** What the antennas kept in the last tick, for the screen and the relay. */
  Energy sent() {
    return sent;
  }

  /** How many antennas took energy in the last tick. */
  int receiving() {
    return receiving;
  }

  /** How many antennas the Server has, wherever they are. */
  int listed() {
    return listed;
  }
}
