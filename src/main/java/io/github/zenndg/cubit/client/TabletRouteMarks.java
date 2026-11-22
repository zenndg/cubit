package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletAppLook;
import io.github.zenndg.cubit.network.PatrolRoutePayload;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

/**
 * The route of the chosen Galactic Patroller while the Mark app is open: an arrow with its number
 * over each point, in the color of the Mark app, and a cyan dashed line joining the points in a
 * loop, through blocks (answers 1580 and 1612). The server sends it once a second and after each
 * click.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class TabletRouteMarks {
  private static final float ALPHA = 0.9f;
  private static final float SCALE = 1.2f;
  private static final float LINE_RED = 0.2f;
  private static final float LINE_GREEN = 0.9f;
  private static final float LINE_BLUE = 1f;
  private static final double HALF = 0.5;
  private static final double LINE_LIFT = 0.15;
  private static final double NUMBER_HEIGHT = 1.55;
  private static final double DASH = 0.5;
  private static final double GAP = 0.3;
  private static final double SHORTEST = 1.0e-3;
  private static final int CLOSES_A_LOOP = 3;

  private static List<PatrolRoutePayload.Point> points = List.of();

  private TabletRouteMarks() {}

  static void show(PatrolRoutePayload payload) {
    points = payload.points();
  }

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL
        || player == null
        || points.isEmpty()
        || !TabletChestMarks.holdsTablet(player)) {
      return;
    }
    double ticks =
        player.level().getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
    drawLine(buffers, event.getCamera().getPosition());
    drawArrows(buffers, event.getCamera(), ticks);
    buffers.endBatch();
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }

  private static void drawArrows(
      MultiBufferSource.BufferSource buffers, Camera camera, double ticks) {
    List<Vec3> tops = points.stream().map(point -> topOf(point.pos())).toList();
    MobMarker.Style style = MobMarker.Style.of(TabletAppLook.color(TabletApp.MARK), SCALE);
    MobMarker.drawOver(buffers, camera, tops, style, ticks, ALPHA);
    for (PatrolRoutePayload.Point point : points) {
      Vec3 at = topOf(point.pos()).add(0, NUMBER_HEIGHT, 0);
      TabletChestMarks.line(buffers, camera, at, Component.literal(String.valueOf(point.number())));
    }
  }

  /** From each point to the next, and from the last back to the first once there are three. */
  private static void drawLine(MultiBufferSource.BufferSource buffers, Vec3 camera) {
    VertexConsumer lines = buffers.getBuffer(ScanMarkRenderer.THROUGH_BLOCKS);
    PoseStack pose = new PoseStack();
    pose.translate(-camera.x, -camera.y, -camera.z);
    int segments = points.size() >= CLOSES_A_LOOP ? points.size() : points.size() - 1;
    for (int index = 0; index < segments; index++) {
      Vec3 from = topOf(points.get(index).pos()).add(0, LINE_LIFT, 0);
      Vec3 to = topOf(points.get((index + 1) % points.size()).pos()).add(0, LINE_LIFT, 0);
      dashes(lines, pose.last(), from, to);
    }
    buffers.endBatch(ScanMarkRenderer.THROUGH_BLOCKS);
  }

  private static void dashes(VertexConsumer lines, PoseStack.Pose pose, Vec3 from, Vec3 to) {
    Vec3 along = to.subtract(from);
    double length = along.length();
    if (length < SHORTEST) {
      return;
    }
    Vec3 unit = along.scale(1 / length);
    for (double start = 0; start < length; start += DASH + GAP) {
      vertex(lines, pose, from.add(unit.scale(start)), unit);
      vertex(lines, pose, from.add(unit.scale(Math.min(start + DASH, length))), unit);
    }
  }

  private static void vertex(VertexConsumer lines, PoseStack.Pose pose, Vec3 at, Vec3 normal) {
    lines
        .addVertex(pose, (float) at.x, (float) at.y, (float) at.z)
        .setColor(LINE_RED, LINE_GREEN, LINE_BLUE, ALPHA)
        .setNormal(pose, (float) normal.x, (float) normal.y, (float) normal.z);
  }

  private static Vec3 topOf(BlockPos pos) {
    return new Vec3(pos.getX() + HALF, pos.getY() + 1, pos.getZ() + HALF);
  }
}
