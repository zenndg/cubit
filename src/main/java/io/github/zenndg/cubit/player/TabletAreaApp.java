package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.robot.AreaCheck;
import io.github.zenndg.cubit.domain.robot.AreaChunks;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.AreaDrawing;
import io.github.zenndg.cubit.domain.robot.AreaMarking;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotTablet;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The Area app: a click on an own Cubit chooses it, and the blocks after it are the points of a
 * drawing in right angles, which the first point closes. Shift and the click take out the last
 * point, or apply the closed drawing, and, with no drawing, erase the area of the Cubit in the
 * second click (answers 2240 to 2246, 2299 and 2352). A drawing lives only in the memory of the
 * server.
 */
public final class TabletAreaApp {
  private static final Map<UUID, AreaMarking> MARKING = new ConcurrentHashMap<>();

  private TabletAreaApp() {}

  static void click(ServerPlayer player, TabletUse.Click click) {
    dropFromElsewhere(player);
    if (player.isShiftKeyDown()) {
      shiftClick(player, click);
    } else if (click instanceof TabletUse.Living living) {
      pick(player, living.entity());
    } else {
      mark(player, click);
    }
    TabletAreaView.sync(player);
  }

  /** The drawing halfway, for the line the client draws (answer 2358). */
  static Optional<AreaMarking> marking(ServerPlayer player) {
    return Optional.ofNullable(MARKING.get(player.getUUID()));
  }

  /** Switching the app or leaving the game drops the drawing halfway (answers 1060 and 2245). */
  static void forget(ServerPlayer player) {
    TabletAreaErase.forget(player);
    if (MARKING.remove(player.getUUID()) != null) {
      TabletAreaView.sync(player);
    }
  }

  /** A drawing stays in the dimension it started in. */
  private static void dropFromElsewhere(ServerPlayer player) {
    marking(player)
        .filter(marking -> !marking.dimension().equals(dimensionOf(player)))
        .ifPresent(marking -> MARKING.remove(player.getUUID()));
  }

  /** With a point marked, the drawing comes before the area of a Cubit in the aim (2352). */
  private static void shiftClick(ServerPlayer player, TabletUse.Click click) {
    Optional<AreaMarking> drawn =
        marking(player).filter(marking -> !marking.drawing().points().isEmpty());
    if (drawn.isPresent()) {
      if (drawn.get().drawing().closed()) {
        apply(player, drawn.get());
      } else {
        undo(player, drawn.get());
      }
    } else if (click instanceof TabletUse.Living living) {
      TabletAreaErase.click(player, living.entity());
    } else if (MARKING.remove(player.getUUID()) != null) {
      say(player, Component.translatable(TabletKeys.AREA_CANCELLED));
    } else {
      say(player, Component.translatable(TabletKeys.AREA_PICK_CUBIT));
    }
  }

  private static void undo(ServerPlayer player, AreaMarking marking) {
    AreaDrawing left = marking.drawing().undo();
    MARKING.put(player.getUUID(), marking.with(left));
    say(player, Component.translatable(TabletKeys.AREA_UNDONE, left.points().size()));
  }

  /** The Cubit of the drawing keeps it; another one starts over (answer 2357). */
  private static void pick(ServerPlayer player, Entity entity) {
    if (!(entity instanceof RobotEntity robot)
        || !Ownership.owns(robot.ownerId(), player.getUUID())) {
      say(player, Component.translatable(TabletKeys.AREA_ONLY_YOURS));
      return;
    }
    if (!RobotTablet.areaOf(robot).unlocked()) {
      say(player, Component.translatable(TabletKeys.AREA_LOCKED, robot.getDisplayName()));
      return;
    }
    TabletSelection.choose(player, robot.getUUID());
    if (marking(player).filter(marking -> marking.robot().equals(robot.getUUID())).isEmpty()) {
      MARKING.put(player.getUUID(), AreaMarking.of(robot.getUUID(), dimensionOf(player)));
    }
    say(player, Component.translatable(TabletKeys.AREA_FIRST_POINT, robot.getDisplayName()));
  }

  private static void mark(ServerPlayer player, TabletUse.Click click) {
    AreaMarking marking = MARKING.get(player.getUUID());
    if (marking == null) {
      say(player, Component.translatable(TabletKeys.AREA_PICK_CUBIT));
      return;
    }
    Optional<RobotEntity> robot = RobotTablet.find(player, marking.robot());
    if (robot.isEmpty()) {
      MARKING.remove(player.getUUID());
      say(player, Component.translatable(TabletKeys.AREA_CUBIT_GONE));
      return;
    }
    Optional<BlockPos> block = blockOf(player, click);
    if (block.isEmpty()) {
      int reach = CubitMod.CONFIG.areaDrawing().reachBlocks();
      say(player, Component.translatable(TabletKeys.AREA_NO_BLOCK, reach));
      return;
    }
    draw(player, robot.get(), marking, block.get());
  }

  private static void draw(
      ServerPlayer player, RobotEntity robot, AreaMarking marking, BlockPos pos) {
    AreaDrawing.Click click =
        marking
            .drawing()
            .click(
                new AreaCorner(pos.getX(), pos.getY(), pos.getZ()),
                CubitMod.CONFIG.areaDrawing(),
                RobotTablet.areaOf(robot),
                reachOf(robot, marking.dimension()));
    MARKING.put(player.getUUID(), marking.with(click.drawing()));
    say(player, TabletAreaTexts.of(click, robot));
  }

  /** The block clicked, or the one in the aim up to the reach of the config (answer 2311). */
  private static Optional<BlockPos> blockOf(ServerPlayer player, TabletUse.Click click) {
    if (click instanceof TabletUse.Block block) {
      return Optional.of(block.pos());
    }
    HitResult hit = player.pick(CubitMod.CONFIG.areaDrawing().reachBlocks(), 1, false);
    return hit instanceof BlockHitResult block && hit.getType() == HitResult.Type.BLOCK
        ? Optional.of(block.getBlockPos())
        : Optional.empty();
  }

  /** A refused area keeps the drawing, so only what is wrong is drawn again. */
  private static void apply(ServerPlayer player, AreaMarking marking) {
    Optional<RobotEntity> robot = RobotTablet.find(player, marking.robot());
    Optional<WorkArea> area = marking.drawing().area(marking.dimension());
    if (robot.isEmpty() || area.isEmpty()) {
      MARKING.remove(player.getUUID());
      say(player, Component.translatable(TabletKeys.AREA_CUBIT_GONE));
      return;
    }
    if (!reachOf(robot.get(), marking.dimension()).holdsShape(area.get().shape())) {
      say(player, Component.translatable(NetworkAppKeys.OUTSIDE, robot.get().getDisplayName()));
      return;
    }
    AreaCheck check = RobotTablet.checkArea(robot.get(), area.get());
    if (check != AreaCheck.OK) {
      say(player, TabletAreaTexts.refusal(check, robot.get()));
      return;
    }
    robot.get().workArea().assign(area.get(), player);
    MARKING.remove(player.getUUID());
    say(player, TabletAreaTexts.applied(area.get(), robot.get()));
  }

  /**
   * The chunks of the antennas of the Cubit, checked again when the area is applied, for an antenna
   * unlinked after the drawing closed (answers 2586 and 2601).
   */
  private static AreaChunks reachOf(RobotEntity robot, String dimension) {
    return AreaChunks.of(dimension, robot.docking().network().links().chunks());
  }

  static String dimensionOf(ServerPlayer player) {
    return player.level().dimension().location().toString();
  }

  static void say(ServerPlayer player, Component text) {
    player.displayClientMessage(text, true);
  }
}
