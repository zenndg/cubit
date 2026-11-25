package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.AreaBounds;
import io.github.zenndg.cubit.domain.robot.AreaDrawing;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

/**
 * Draws the line of the Area app after the level: red while open, green when closed or applied, and
 * the next line translucent up to the aim (answers 2240 and 2358 to 2361). While drawing, the
 * action bar shows the next point and the size, only when they change, so the texts of the server
 * still show.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class TabletAreaRenderer {
  private static final AreaLinePainter.Paint RED =
      new AreaLinePainter.Paint(0.95f, 0.25f, 0.25f, 1);
  private static final AreaLinePainter.Paint GREEN =
      new AreaLinePainter.Paint(0.3f, 0.9f, 0.35f, 1);
  private static final AreaLinePainter.Paint AIM =
      new AreaLinePainter.Paint(0.92f, 0.93f, 0.96f, 0.5f);
  private static final float NEXT_SHARE = 0.45f;

  private static Optional<Component> lastSaid = Optional.empty();

  private TabletAreaRenderer() {}

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
      return;
    }
    float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
    Optional<TabletAreaMarks.Shown> shown = TabletAreaMarks.shown(partialTick);
    if (shown.isEmpty()) {
      lastSaid = Optional.empty();
      return;
    }
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    AreaLinePainter painter =
        new AreaLinePainter(
            Minecraft.getInstance().renderBuffers().bufferSource(),
            event.getCamera().getPosition());
    switch (shown.get()) {
      case TabletAreaMarks.Drawn drawn -> drawing(painter, drawn);
      case TabletAreaMarks.Applied applied -> applied(painter, applied);
    }
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }

  private static void applied(AreaLinePainter painter, TabletAreaMarks.Applied area) {
    int low = area.corners().getFirst().y();
    paint(painter, AreaLineBars.volume(area.corners(), low, area.high()), GREEN);
  }

  private static void drawing(AreaLinePainter painter, TabletAreaMarks.Drawn drawn) {
    AreaDrawing drawing = drawn.drawing();
    if (drawing.points().isEmpty()) {
      TabletAreaMarks.applied().ifPresent(area -> applied(painter, area));
    }
    List<AABB> bars = new ArrayList<>(AreaLineBars.cubes(drawing.points()));
    bars.addAll(drawing.closed() ? volume(drawing) : AreaLineBars.path(drawing.points()));
    paint(painter, bars, drawing.closed() ? GREEN : RED);
    if (!drawing.closed()) {
      aim(painter, drawn);
      say(drawn.next());
    }
  }

  private static List<AABB> volume(AreaDrawing drawing) {
    AreaBounds bounds = drawing.bounds().orElseThrow();
    return AreaLineBars.volume(drawing.points(), bounds.min().y(), bounds.max().y());
  }

  /** The block in the aim and, when a click on it would go in, the line it would make. */
  private static void aim(AreaLinePainter painter, TabletAreaMarks.Drawn drawn) {
    drawn.aimed().ifPresent(at -> painter.seen(List.of(AreaLineBars.plate(at)), AIM));
    boolean accepted = drawn.next().filter(TabletAreaMarks.Next::accepted).isPresent();
    if (!accepted || drawn.drawing().points().isEmpty()) {
      return;
    }
    boolean closes = drawn.next().orElseThrow().click().step() == AreaDrawing.Step.CLOSED;
    List<AABB> next =
        AreaLineBars.step(
            drawn.drawing().points().getLast(),
            closes ? drawn.drawing().points().getFirst() : drawn.aimed().orElseThrow());
    painter.seen(next, (closes ? GREEN : RED).faded(NEXT_SHARE));
  }

  private static void paint(AreaLinePainter painter, List<AABB> bars, AreaLinePainter.Paint paint) {
    painter.hidden(bars, paint);
    painter.seen(bars, paint);
  }

  private static void say(Optional<TabletAreaMarks.Next> next) {
    Optional<Component> text =
        next.filter(TabletAreaMarks.Next::adds).flatMap(TabletAreaRenderer::textOf);
    if (text.isPresent() && !text.equals(lastSaid)) {
      Minecraft.getInstance().gui.setOverlayMessage(text.get(), false);
    }
    lastSaid = text;
  }

  private static Optional<Component> textOf(TabletAreaMarks.Next next) {
    return next.bounds()
        .map(bounds ->
            Component.translatable(
                TabletKeys.AREA_NEXT,
                next.click().drawing().points().size(),
                next.maxPoints(),
                bounds.width(),
                bounds.height(),
                bounds.depth()));
  }
}
