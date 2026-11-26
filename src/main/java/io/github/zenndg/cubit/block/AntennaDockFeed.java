package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.network.DockFeed;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * How an antenna feeds the Docks of its chunk in a tick: after what it spends itself, only what
 * passes its UPS and its floor, first to the Docks charging a Cubit and then evenly, each within
 * its input (detail 12 of Delivery 5 and answer 2599). It loses exactly what the Docks keep, and,
 * switched off or stopped, gives nothing, while the Docks still show it feeds them.
 */
final class AntennaDockFeed {
  private AntennaDockFeed() {}

  /** The stock after this tick, without what the Docks kept. */
  static EnergyStock tick(ServerLevel level, SignalAntennaBlockEntity antenna, EnergyStock stock) {
    List<DockNetworkInput> docks = docksOf(level, antenna.fedDocks());
    long now = level.getGameTime();
    docks.forEach(dock -> dock.linked(now));
    if (!antenna.givesToDocks() || docks.isEmpty()) {
      return stock;
    }
    Energy offered = NetworkParts.antenna().forDocks(stock.energy().charge(), antenna.ups());
    List<DockFeed.Need> needs =
        docks.stream().map(dock -> new DockFeed.Need(dock.chargesCubit(), dock.room())).toList();
    List<Energy> shares = DockFeed.of(offered, needs);
    Energy kept = Energy.ZERO;
    for (int index = 0; index < docks.size(); index++) {
      kept = kept.plus(docks.get(index).take(shares.get(index)));
    }
    return stock.spend(kept).stock();
  }

  /** Its Docks still there, all in its own chunk, which it keeps loaded. */
  private static List<DockNetworkInput> docksOf(ServerLevel level, List<BlockPos> fed) {
    List<DockNetworkInput> docks = new ArrayList<>(fed.size());
    for (BlockPos pos : fed) {
      if (level.isLoaded(pos)
          && level.getBlockEntity(pos) instanceof InductionDockBlockEntity dock) {
        docks.add(dock.network());
      }
    }
    return docks;
  }
}
