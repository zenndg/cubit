package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletAppLook;
import io.github.zenndg.cubit.network.EscortMarksPayload;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

/**
 * The arrows over the ones the Galactic Patrollers of the player escort, in the gold of the Escort
 * app, while it is open, following them as they move (answer 1661). The server sends the ones to
 * mark once a second and after each click.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class TabletEscortMarks {
  private static final float ALPHA = 0.9f;
  private static final float SCALE = 1.2f;
  private static final double ABOVE = 0.3;

  private static List<Integer> escorted = List.of();

  private TabletEscortMarks() {}

  static void show(EscortMarksPayload payload) {
    escorted = payload.entityIds();
  }

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL
        || player == null
        || escorted.isEmpty()
        || !TabletChestMarks.holdsTablet(player)) {
      return;
    }
    float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
    List<Vec3> tops =
        escorted.stream()
            .map(id -> player.level().getEntity(id))
            .filter(Objects::nonNull)
            .map(entity -> topOf(entity, partial))
            .toList();
    if (tops.isEmpty()) {
      return;
    }
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
    MobMarker.Style style = MobMarker.Style.of(TabletAppLook.color(TabletApp.ESCORT), SCALE);
    double ticks = player.level().getGameTime() + partial;
    MobMarker.drawOver(buffers, event.getCamera(), tops, style, ticks, ALPHA);
    buffers.endBatch();
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }

  private static Vec3 topOf(Entity entity, float partial) {
    return entity.getPosition(partial).add(0, entity.getBbHeight() + ABOVE, 0);
  }
}
