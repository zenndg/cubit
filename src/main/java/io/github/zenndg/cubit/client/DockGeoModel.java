package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * The model of the Induction Dock, the same for the block and its item, with the cabinet, the arms
 * and the panel (answers 1860 and 1884 to 1889). The Ultimate dock has a body of netherite (1380).
 */
abstract class DockGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
  private static final String MODEL_PATH = "geo/block/induction_dock.geo.json";
  private static final String ANIMATIONS_PATH = "animations/block/induction_dock.animation.json";
  private static final String TEXTURE_PATH = "textures/block/induction_dock_body.png";
  private static final String ULTIMATE_PATH = "textures/block/induction_dock_body_ultimate.png";
  private static final ResourceLocation MODEL =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, MODEL_PATH);
  private static final ResourceLocation ANIMATIONS =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, ANIMATIONS_PATH);
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, TEXTURE_PATH);
  private static final ResourceLocation ULTIMATE_TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, ULTIMATE_PATH);

  abstract RobotTier tierOf(T animatable);

  @Override
  public ResourceLocation getModelResource(T animatable) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureResource(T animatable) {
    return tierOf(animatable) == RobotTier.ULTIMATE ? ULTIMATE_TEXTURE : TEXTURE;
  }

  @Override
  public ResourceLocation getAnimationResource(T animatable) {
    return ANIMATIONS;
  }
}
