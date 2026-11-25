package io.github.zenndg.cubit.menu;

import net.minecraft.world.entity.player.Player;

/** The screen of a block its owner may name, as a Dock or a block of the Network (answer 1868). */
public interface RenamableMenu {
  /** On the server: checks the player and names the block. False when it changes nothing. */
  boolean rename(Player player, String typed);
}
