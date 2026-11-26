package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.block.ClickedBlocks;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.entity.RobotEntity;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

/**
 * The Network app, by the chosen Cubit: a click on an own Cubit chooses it, on an antenna links or
 * unlinks it, and on a Dock or a chest marks or unmarks it for that Cubit. Without a chosen Cubit,
 * an antenna and then a Dock of its chunk only feed that Dock (answers 2588 and 2589).
 */
final class TabletNetworkApp {
  private TabletNetworkApp() {}

  static void click(ServerPlayer player, Optional<RobotEntity> chosen, TabletUse.Click click) {
    switch (click) {
      case TabletUse.Living living -> pick(player, living);
      case TabletUse.Block block -> onBlock(player, chosen, block.pos());
      case TabletUse.Air air -> say(player, NetworkAppKeys.PICK);
    }
    TabletChestView.send(player);
  }

  /** Switching the app or leaving the game drops what waits for a second click. */
  static void forget(ServerPlayer player) {
    TabletNetworkFeed.forget(player);
    TabletNetworkLinks.forget(player);
  }

  private static void pick(ServerPlayer player, TabletUse.Living living) {
    if (!(living.entity() instanceof RobotEntity robot)
        || !Ownership.owns(robot.ownerId(), player.getUUID())) {
      say(player, NetworkAppKeys.ONLY_YOURS);
      return;
    }
    forget(player);
    TabletSelection.choose(player, robot.getUUID());
    say(player, NetworkAppKeys.CHOSEN, robot.getDisplayName());
  }

  private static void onBlock(ServerPlayer player, Optional<RobotEntity> chosen, BlockPos pos) {
    BlockEntity found = ClickedBlocks.at(player.serverLevel(), pos);
    if (found instanceof SignalAntennaBlockEntity antenna) {
      onAntenna(player, chosen, antenna);
    } else if (found instanceof InductionDockBlockEntity dock) {
      onDock(player, chosen, dock);
    } else if (!hasItems(player, pos)) {
      say(player, NetworkAppKeys.ONLY_BLOCKS);
    } else if (chosen.isPresent()) {
      TabletNetworkLinks.chest(player, chosen.get(), pos);
    } else {
      say(player, NetworkAppKeys.PICK);
    }
  }

  private static void onAntenna(
      ServerPlayer player, Optional<RobotEntity> chosen, SignalAntennaBlockEntity antenna) {
    if (!antenna.isOwner(player)) {
      say(player, NetworkAppKeys.ONLY_YOURS);
    } else if (chosen.isPresent()) {
      TabletNetworkLinks.antenna(player, chosen.get(), antenna);
    } else {
      TabletNetworkFeed.antenna(player, antenna);
    }
  }

  /** An antenna clicked before, without a Cubit, comes before the chosen one (answer 2588). */
  private static void onDock(
      ServerPlayer player, Optional<RobotEntity> chosen, InductionDockBlockEntity dock) {
    if (!dock.isOwner(player)) {
      say(player, NetworkAppKeys.ONLY_YOURS);
    } else if (TabletNetworkFeed.waits(player)) {
      TabletNetworkFeed.dock(player, dock);
    } else if (chosen.isPresent()) {
      TabletNetworkLinks.dock(player, chosen.get(), dock);
    } else {
      say(player, NetworkAppKeys.PICK);
    }
  }

  /** Any block of the game or of mods with the capability of items (detail 11). */
  private static boolean hasItems(ServerPlayer player, BlockPos pos) {
    return player.serverLevel().getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null;
  }

  static void say(ServerPlayer player, String key, Object... values) {
    player.displayClientMessage(Component.translatable(key, values), true);
  }
}
