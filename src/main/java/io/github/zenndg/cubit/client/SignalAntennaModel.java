package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * The model of the Signal Antenna, the same for the block and its item (answers 2555 to 2557). The
 * animations raise the mast and move the dish, and the renderers tint the channel and light the
 * LED.
 */
final class SignalAntennaModel<T extends GeoAnimatable> extends GeoModel<T> {
  private static final String MODEL_PATH = "geo/block/signal_antenna.geo.json";
  private static final String ANIMATIONS_PATH = "animations/block/signal_antenna.animation.json";
  private static final String TEXTURE_PATH = "textures/block/signal_antenna.png";
  private static final ResourceLocation MODEL = path(MODEL_PATH);
  private static final ResourceLocation ANIMATIONS = path(ANIMATIONS_PATH);
  private static final ResourceLocation TEXTURE = path(TEXTURE_PATH);

  private static ResourceLocation path(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  @Override
  public ResourceLocation getModelResource(T antenna) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureResource(T antenna) {
    return TEXTURE;
  }

  @Override
  public ResourceLocation getAnimationResource(T antenna) {
    return ANIMATIONS;
  }
}
