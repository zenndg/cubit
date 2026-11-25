package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.domain.robot.AreaColumn;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.player.TabletUse;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** The clicks of the Area app, as the player makes them. */
final class AreaClicks {
  private AreaClicks() {}

  static ItemStack tablet() {
    ItemStack tablet = new ItemStack(CubitItems.TABLET.get());
    TabletItem.setApp(tablet, TabletApp.AREA);
    return tablet;
  }

  static void choose(ServerPlayer owner, RobotEntity robot) {
    TabletUse.click(owner, tablet(), new TabletUse.Living(robot));
  }

  static void mark(GameTestHelper helper, ServerPlayer owner, BlockPos... points) {
    for (BlockPos point : points) {
      TabletUse.click(owner, tablet(), block(helper, point));
    }
  }

  static void shift(ServerPlayer owner, TabletUse.Click click) {
    owner.setShiftKeyDown(true);
    TabletUse.click(owner, tablet(), click);
    owner.setShiftKeyDown(false);
  }

  static TabletUse.Block block(GameTestHelper helper, BlockPos pos) {
    return new TabletUse.Block(helper.absolutePos(pos), Optional.empty());
  }

  /** The Cubit, the points, the first one again to close, and Shift to apply. */
  static void draw(
      GameTestHelper helper, ServerPlayer owner, RobotEntity robot, BlockPos... points) {
    choose(owner, robot);
    mark(helper, owner, points);
    mark(helper, owner, points[0]);
    shift(owner, new TabletUse.Air());
  }

  /** A box between two corners on the same floor. */
  static void drawBox(
      GameTestHelper helper, ServerPlayer owner, RobotEntity robot, BlockPos one, BlockPos other) {
    draw(helper, owner, robot, box(one, other));
  }

  static BlockPos[] box(BlockPos one, BlockPos other) {
    return new BlockPos[] {
      one,
      new BlockPos(other.getX(), one.getY(), one.getZ()),
      other,
      new BlockPos(one.getX(), one.getY(), other.getZ())
    };
  }

  /** Gives the area without clicks, from the floor of its points to some blocks over it. */
  static WorkArea area(GameTestHelper helper, RobotEntity robot, int height, BlockPos... points) {
    List<BlockPos> absolute = Arrays.stream(points).map(helper::absolutePos).toList();
    int low = absolute.getFirst().getY();
    return AreaBoxes.drawn(
        absolute.stream().map(pos -> new AreaColumn(pos.getX(), pos.getZ())).toList(),
        low,
        low + height - 1,
        robot.level().dimension().location().toString());
  }
}
