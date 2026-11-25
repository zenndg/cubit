package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.robot.AreaBounds;
import io.github.zenndg.cubit.domain.robot.AreaCorner;
import io.github.zenndg.cubit.domain.robot.AreaDrawing;
import io.github.zenndg.cubit.domain.robot.AreaDrawingSpec;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.domain.tier.AreaSpec;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.network.AreaMarkPayload;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * What the Area app shows on the client: the drawing halfway, with the block in the aim and the
 * next line when it would be accepted, and then the area of the chosen Cubit (answers 2240 and
 * 2358). Only with the Tablet in hand and the Area app active.
 */
final class TabletAreaMarks {
  private static AreaMarkPayload current = AreaMarkPayload.NOTHING;
  private static Optional<Applied> applied = Optional.empty();
  private static Optional<Drawn> drawn = Optional.empty();

  private TabletAreaMarks() {}

  static void show(AreaMarkPayload payload) {
    current = payload;
    drawn = Optional.empty();
    applied =
        payload
            .area()
            .map(area ->
                new Applied(
                    area.corners().stream().map(TabletAreaMarks::corner).toList(), area.high()));
  }

  /** A drawing with the click the aim would make, or the area of the chosen Cubit. */
  sealed interface Shown permits Drawn, Applied {}

  /**
   * @param aimed the block in the aim, up to the reach
   * @param next what a click on it would do
   */
  record Drawn(AreaDrawing drawing, Optional<AreaCorner> aimed, Optional<Next> next)
      implements Shown {}

  /** A click that would go in, and the most points of the drawing. */
  record Next(AreaDrawing.Click click, int maxPoints) {
    boolean accepted() {
      return adds() || click.step() == AreaDrawing.Step.CLOSED;
    }

    boolean adds() {
      return click.step() == AreaDrawing.Step.ADDED;
    }

    Optional<AreaBounds> bounds() {
      return click.drawing().bounds();
    }
  }

  /** The area the Cubit has. */
  record Applied(List<AreaCorner> corners, int high) implements Shown {}

  static Optional<Shown> shown(float partialTick) {
    if (!holdsAreaApp()) {
      return Optional.empty();
    }
    if (current.drawing().isPresent()) {
      return Optional.of(drawn(current.drawing().get(), partialTick));
    }
    return applied.map(area -> area);
  }

  /** The area of the chosen Cubit, also while its new drawing has no point yet. */
  static Optional<Applied> applied() {
    return applied;
  }

  /** Worked out again only when the drawing or the block in the aim changes. */
  private static Drawn drawn(AreaMarkPayload.Drawing sent, float partialTick) {
    Optional<AreaCorner> aimed = aimed(sent.limits().reach(), partialTick);
    if (drawn.filter(last -> last.aimed().equals(aimed)).isEmpty()) {
      drawn = Optional.of(drawn(sent, aimed));
    }
    return drawn.get();
  }

  private static Drawn drawn(AreaMarkPayload.Drawing sent, Optional<AreaCorner> aimed) {
    AreaMarkPayload.Limits limits = sent.limits();
    AreaDrawing drawing =
        new AreaDrawing(
            sent.points().stream().map(TabletAreaMarks::corner).toList(), sent.closed());
    AreaDrawingSpec spec =
        new AreaDrawingSpec(limits.minPoints(), limits.maxPoints(), limits.reach());
    AreaSpec tier = new AreaSpec(limits.size(), limits.height());
    Optional<Next> next =
        aimed.map(at -> new Next(drawing.click(at, spec, tier), spec.maxPoints()));
    return new Drawn(drawing, aimed, next);
  }

  private static AreaCorner corner(BlockPos pos) {
    return new AreaCorner(pos.getX(), pos.getY(), pos.getZ());
  }

  private static Optional<AreaCorner> aimed(int reach, float partialTick) {
    LocalPlayer player = Minecraft.getInstance().player;
    HitResult hit = player.pick(reach, partialTick, false);
    return hit instanceof BlockHitResult block && hit.getType() == HitResult.Type.BLOCK
        ? Optional.of(corner(block.getBlockPos()))
        : Optional.empty();
  }

  private static boolean holdsAreaApp() {
    LocalPlayer player = Minecraft.getInstance().player;
    if (player == null) {
      return false;
    }
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack held = player.getItemInHand(hand);
      if (held.is(CubitItems.TABLET.get()) && TabletItem.appOf(held) == TabletApp.AREA) {
        return true;
      }
    }
    return false;
  }
}
