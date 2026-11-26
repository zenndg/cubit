package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.ownership.FloatingName;
import net.minecraft.world.entity.player.Player;

/**
 * A block whose name floats over it for its owner with the Tablet in a hand, as a Dock, a Network
 * Server or a Solar Relay (answers 2469 and 2517). The client reads it, so the name and the owner
 * travel to it in the update of the block.
 */
public interface ShowsName {
  BlockName shownName();

  boolean isOwner(Player player);

  FloatingName nameSpot();

  /** Whether the block is gone, as a block entity tells. */
  boolean isRemoved();
}
