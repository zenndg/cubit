package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DockSolarReading;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.behavior.SolarPulse;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * The cells of the deployed panel reflect the light and brighten and darken slowly while it gives
 * energy, with the same pulse as the panel of the Cubit (answers 1853 and 1891).
 */
final class DockPanelGlow extends GeoRenderLayer<InductionDockBlockEntity> {
  private static final String GLOW_PATH = "textures/block/induction_dock_glow.png";
  private static final int MAX_COLOR = 255;
  private static final int ALPHA_SHIFT = 24;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;

  /** Spreads the docks over the pulse, so they do not all breathe at the same moment. */
  private static final int PULSE_SPREAD_TICKS = 23;

  private final RenderType glowType =
      RenderType.eyes(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, GLOW_PATH));

  DockPanelGlow(GeoRenderer<InductionDockBlockEntity> renderer) {
    super(renderer);
  }

  private static int gray(double strength) {
    int value = (int) Math.round(MAX_COLOR * strength);
    return MAX_COLOR << ALPHA_SHIFT | value << RED_SHIFT | value << GREEN_SHIFT | value;
  }

  @Override
  public void render(
      PoseStack poseStack,
      InductionDockBlockEntity dock,
      BakedGeoModel model,
      RenderType bodyType,
      MultiBufferSource buffers,
      VertexConsumer bodyBuffer,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    DockSolarReading solar = dock.solar();
    if (bodyBuffer == null || !solar.sunlit() || solar.panel() != DockPanelState.DEPLOYED) {
      return;
    }
    double tick =
        DockRenderer.ticks(dock, partialTick) + dock.getBlockPos().hashCode() * PULSE_SPREAD_TICKS;
    getRenderer()
        .reRender(
            model,
            poseStack,
            buffers,
            dock,
            glowType,
            buffers.getBuffer(glowType),
            partialTick,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            gray(SolarPulse.at(tick)));
  }
}
