package io.github.zenndg.cubit.block;

import java.util.UUID;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;

/** A block its owner names, as a Dock or a block of the Network (answers 1867 and 2420). */
interface NamedBlock {
  BlockIdentity identity();

  boolean isOwner(Player player);

  /** Where its item keeps the id, which a new item does not have. */
  DataComponentType<UUID> idComponent();
}
