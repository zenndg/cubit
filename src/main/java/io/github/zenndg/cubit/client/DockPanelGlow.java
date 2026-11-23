package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.behavior.SolarPulse;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * The cells of the deployed panel reflect the light and brighten and darken slowly while it gives
 * energy, with the same pulse as the panel of the Cubit (answers 1853 and 1891). Under the moon the
 * pulse is weaker, in a light blue (answer 1950).
 */
final class DockPanelGlow extends GeoRenderLayer<InductionDockBlockEntity> {
  private static final String GLOW_PATH = "textures/block/induction_dock_glow.png";
  private static final int MAX_COLOR = 255;
  private static final int ALPHA_SHIFT = 24;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;

  /** The light of the moon over the one of the sun: weaker, and a light blue (answer 1950). */
  private static final double MOON_RED = 0.6;

  private static final double MOON_GREEN = 0.7;
  private static final double MOON_BLUE = 0.8;

  /** Spreads the docks over the pulse, so they do not all breathe at the same moment. */
  private static final int PULSE_SPREAD_TICKS = 23;

  private final RenderType glowType =
      RenderType.eyes(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, GLOW_PATH));

  DockPanelGlow(GeoRenderer<InductionDockBlockEntity> renderer) {
    super(renderer);
  }

  private static int tint(double strength, boolean moon) {
    return moon
        ? color(strength * MOON_RED, strength * MOON_GREEN, strength * MOON_BLUE)
        : color(strength, strength, strength);
  }

  private static int color(double red, double green, double blue) {
    return MAX_COLOR << ALPHA_SHIFT
        | channel(red) << RED_SHIFT
        | channel(green) << GREEN_SHIFT
        | channel(blue);
  }

  private static int channel(double strength) {
    return (int) Math.round(MAX_COLOR * strength);
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
    if (bodyBuffer == null || !DockMoonlight.gives(dock)) {
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
            tint(SolarPulse.at(tick), DockMoonlight.isNight(dock)));
  }
}
