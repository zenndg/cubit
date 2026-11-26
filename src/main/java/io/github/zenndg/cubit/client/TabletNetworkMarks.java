package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.AntennaPlacing;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletAppLook;
import io.github.zenndg.cubit.network.NetworkLinksPayload;
import io.github.zenndg.cubit.network.NetworkLinksPayload.Mark;
import io.github.zenndg.cubit.player.NetworkAppKeys;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
 * The arrows of the Network app, with two lines over each: the antennas of the chosen Cubit in the
 * color of their channel, its Docks in the red of the Dock app, its chests in the orange of the
 * Unload app and the Docks with only energy in gray (answers 2592 and 2595). The server sends them
 * once a second, and they show only with the app, through blocks.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class TabletNetworkMarks {
  private static final float ALPHA = 0.9f;
  private static final float SCALE = 1.2f;
  private static final double HALF = 0.5;
  private static final double FIRST_LINE = 1.55;
  private static final double LINE_GAP = 0.27;
  private static final int ENERGY_GRAY = 0x9E9E9E;

  private static List<Mark> marks = List.of();
  private static Set<BlockPos> covered = Set.of();

  private TabletNetworkMarks() {}

  static void show(NetworkLinksPayload payload) {
    marks = payload.marks();
    covered = marks.stream().map(Mark::pos).collect(Collectors.toUnmodifiableSet());
  }

  /** An arrow already says the name of the block, so its floating name leaves the place to it. */
  static boolean covers(BlockPos pos) {
    return covered.contains(pos);
  }

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL
        || player == null
        || marks.isEmpty()
        || !NetworkAppHeld.by(player)) {
      return;
    }
    double ticks =
        player.level().getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
    for (Mark mark : marks) {
      draw(buffers, event.getCamera(), mark, ticks);
    }
    buffers.endBatch();
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }

  private static void draw(
      MultiBufferSource.BufferSource buffers, Camera camera, Mark mark, double ticks) {
    Vec3 top = topOf(mark);
    MobMarker.Style style = MobMarker.Style.of(colorOf(mark), SCALE);
    MobMarker.drawOver(buffers, camera, List.of(top), style, ticks, ALPHA);
    TabletChestMarks.line(
        buffers, camera, top.add(0, FIRST_LINE, 0), Component.literal(mark.name()));
    TabletChestMarks.line(buffers, camera, top.add(0, FIRST_LINE - LINE_GAP, 0), detailOf(mark));
  }

  /** Over the top of the block, at its whole height. */
  private static Vec3 topOf(Mark mark) {
    return new Vec3(
        mark.pos().getX() + HALF, mark.pos().getY() + mark.tall(), mark.pos().getZ() + HALF);
  }

  private static int colorOf(Mark mark) {
    return switch (mark.kind()) {
      case ANTENNA -> AntennaChannel.of(mark.detail()).map(ChannelTint::band).orElse(ENERGY_GRAY);
      case DOCK -> TabletAppLook.color(TabletApp.DOCK);
      case CHEST -> TabletAppLook.color(TabletApp.UNLOAD);
      case FED -> ENERGY_GRAY;
    };
  }

  private static Component detailOf(Mark mark) {
    return switch (mark.kind()) {
      case ANTENNA ->
          AntennaChannel.of(mark.detail())
              .map(channel ->
                  Component.translatable(
                      NetworkAppKeys.ARROW_CHANNEL, AntennaPlacing.colorOf(channel)))
              .orElse(Component.empty());
      case DOCK -> Component.translatable(NetworkAppKeys.ARROW_OF, mark.detail());
      case CHEST -> Component.translatable(NetworkAppKeys.ARROW_FREE_SLOTS, mark.number());
      case FED -> Component.translatable(NetworkAppKeys.ARROW_ENERGY_ONLY);
    };
  }
}
