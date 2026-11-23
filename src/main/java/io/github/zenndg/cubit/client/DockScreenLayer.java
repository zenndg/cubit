package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DockStatusProperty;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * The screen of the dock, the line under it and the strip of the plate, drawn with the picture of
 * its status, the same five of the old model, and lit in the dark (answer 1886). The body never
 * draws them: only this layer does, with every other part hidden.
 */
final class DockScreenLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
  private static final String SCREEN = "status";
  private static final String ROOT = "root";
  private static final String TEXTURE_PATH = "textures/block/induction_dock_status_%s.png";
  private static final int NO_TINT = -1;

  private final Function<T, DockStatusProperty> status;
  private final Map<DockStatusProperty, RenderType> types = new EnumMap<>(DockStatusProperty.class);

  DockScreenLayer(GeoRenderer<T> renderer, Function<T, DockStatusProperty> status) {
    super(renderer);
    this.status = status;
    for (DockStatusProperty value : DockStatusProperty.values()) {
      ResourceLocation texture =
          ResourceLocation.fromNamespaceAndPath(
              CubitMod.MOD_ID, TEXTURE_PATH.formatted(value.getSerializedName()));
      types.put(value, RenderType.entityCutoutNoCull(texture));
    }
  }

  @Override
  public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      RenderType renderType,
      MultiBufferSource buffers,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    model.getBone(SCREEN).ifPresent(screen -> screen.setHidden(true));
  }

  @Override
  public void render(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      RenderType renderType,
      MultiBufferSource buffers,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    GeoBone root = model.getBone(ROOT).orElse(null);
    GeoBone screen = model.getBone(SCREEN).orElse(null);
    if (buffer == null || root == null || screen == null) {
      return;
    }
    showOnly(root, screen, true);
    RenderType type = types.get(status.apply(animatable));
    getRenderer()
        .reRender(
            model,
            poseStack,
            buffers,
            animatable,
            type,
            buffers.getBuffer(type),
            partialTick,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            NO_TINT);
    showOnly(root, screen, false);
  }

  /** Shows only the screen, or puts the body back with the screen hidden. */
  private static void showOnly(GeoBone root, GeoBone screen, boolean only) {
    for (GeoBone part : root.getChildBones()) {
      part.setHidden(only != (part == screen));
    }
  }
}
