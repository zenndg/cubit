package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.network.AntennaSpec;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitDataComponents;

/** The shared parts of a block, with the names and the numbers of the Network. */
final class NetworkParts {
  private static final String ID_KEY = "NetworkId";
  static final BlockIdentity.Kept SERVER_ID = kept(BlockName.Kind.SERVER);
  static final BlockIdentity.Kept RELAY_ID = kept(BlockName.Kind.RELAY);
  static final BlockIdentity.Kept ANTENNA_ID = kept(BlockName.Kind.ANTENNA);

  private NetworkParts() {}

  /** The catalog is read on each use, because the config may load after the block. */
  static BlockTier<ServerSpec> serverTier() {
    return new BlockTier<>(NetworkParts::spec);
  }

  static BlockEnergy.Start serverStart() {
    ServerSpec base = spec(RobotTier.BASE);
    return new BlockEnergy.Start(base.capacity(), base.inputPerTick());
  }

  static BlockEnergy.Start relayStart() {
    return new BlockEnergy.Start(
        CubitMod.CONFIG.network().relayCapacity(), CubitMod.CONFIG.network().relayInputPerTick());
  }

  static BlockEnergy.Start antennaStart() {
    AntennaSpec antenna = antenna();
    return new BlockEnergy.Start(antenna.capacity(), antenna.inputPerTick());
  }

  /** The antenna has no tier (answer 2199). */
  static AntennaSpec antenna() {
    return CubitMod.CONFIG.network().antennas().spec();
  }

  private static ServerSpec spec(RobotTier tier) {
    return CubitMod.CONFIG.network().server(tier);
  }

  private static BlockIdentity.Kept kept(BlockName.Kind kind) {
    return new BlockIdentity.Kept(kind, ID_KEY, CubitDataComponents.NETWORK_ID);
  }
}
