package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import java.util.function.Predicate;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * The lit layer of a block of the Network, seen in the dark without lighting the room (answer
 * 2281). The renderer gives each part the strength of its light, and black adds nothing.
 */
final class NetworkGlowLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
  private static final String PATH = "textures/block/%s_glow.png";
  private static final int WHITE = -1;

  private final RenderType glowType;
  private final Predicate<T> lit;

  NetworkGlowLayer(GeoRenderer<T> renderer, String name, Predicate<T> lit) {
    super(renderer);
    this.glowType =
        RenderType.eyes(
            ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH.formatted(name)));
    this.lit = lit;
  }

  @Override
  public void render(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      RenderType bodyType,
      MultiBufferSource buffers,
      VertexConsumer bodyBuffer,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    if (bodyBuffer == null || !lit.test(animatable)) {
      return;
    }
    getRenderer()
        .reRender(
            model,
            poseStack,
            buffers,
            animatable,
            glowType,
            buffers.getBuffer(glowType),
            partialTick,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            WHITE);
  }
}
