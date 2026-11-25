package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockSpec;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.registry.CubitDataComponents;

/** The shared parts of a block, with the names and the numbers of a dock. */
final class DockParts {
  private static final String ID_KEY = "DockId";
  private static final BlockIdentity.Kept ID =
      new BlockIdentity.Kept(BlockName.Kind.DOCK, ID_KEY, CubitDataComponents.DOCK_ID);

  private DockParts() {}

  static BlockIdentity identity(Runnable changed) {
    return new BlockIdentity(ID, changed);
  }

  /** The catalog is read on each use, because the config may load after the block. */
  static BlockTier<DockSpec> tier() {
    return new BlockTier<>(tier -> CubitMod.CONFIG.dockCatalog().dock(tier));
  }
}
