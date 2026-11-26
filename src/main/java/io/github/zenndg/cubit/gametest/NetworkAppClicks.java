package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.player.TabletUse;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** The clicks of the Network app, as the player makes them, on absolute places. */
final class NetworkAppClicks {
  private NetworkAppClicks() {}

  static ItemStack tablet() {
    ItemStack tablet = new ItemStack(CubitItems.TABLET.get());
    TabletItem.setApp(tablet, TabletApp.NETWORK);
    return tablet;
  }

  static void choose(ServerPlayer owner, RobotEntity robot) {
    TabletUse.click(owner, tablet(), new TabletUse.Living(robot));
  }

  /** Shift and a click, which only switch the wall around the chunk of the player (2608). */
  static void shiftClick(ServerPlayer owner, ItemStack tablet, TabletUse.Click click) {
    owner.setShiftKeyDown(true);
    TabletUse.click(owner, tablet, click);
    owner.setShiftKeyDown(false);
  }

  static void click(ServerPlayer owner, BlockPos absolute) {
    TabletUse.click(owner, tablet(), new TabletUse.Block(absolute, Optional.empty()));
  }
}
