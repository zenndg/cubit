package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

/**
 * The model of the Solar Relay, the same for the block and its item. The animations unfold the
 * leaves and lean the panel, and this takes the lean to where the sun is, so it only leans once it
 * is unfolded (answers 2384 and 2385).
 */
abstract class SolarRelayModel<T extends GeoAnimatable> extends GeoModel<T> {
  static final String TILT = "tilt";
  static final String ROLL = "roll";
  private static final String DISH_YAW = "dish_yaw";
  private static final String DISH = "dish";
  private static final String MODEL_PATH = "geo/block/solar_relay.geo.json";
  private static final String ANIMATIONS_PATH = "animations/block/solar_relay.animation.json";
  private static final String TEXTURE_PATH = "textures/block/solar_relay.png";
  private static final ResourceLocation MODEL = path(MODEL_PATH);
  private static final ResourceLocation ANIMATIONS = path(ANIMATIONS_PATH);
  private static final ResourceLocation TEXTURE = path(TEXTURE_PATH);

  /** How far the animations lean the panel, which says how far the lean went. */
  private static final float ANIMATED_TILT = 32f * Mth.DEG_TO_RAD;

  /** The dish looks a little up. */
  private static final float DISH_UP = -14f * Mth.DEG_TO_RAD;

  private static ResourceLocation path(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  @Override
  public ResourceLocation getModelResource(T relay) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureResource(T relay) {
    return TEXTURE;
  }

  @Override
  public ResourceLocation getAnimationResource(T relay) {
    return ANIMATIONS;
  }

  @Override
  public void setCustomAnimations(T relay, long instanceId, AnimationState<T> state) {
    super.setCustomAnimations(relay, instanceId, state);
    GeoBone dish = getAnimationProcessor().getBone(DISH);
    GeoBone tilt = getAnimationProcessor().getBone(TILT);
    GeoBone roll = getAnimationProcessor().getBone(ROLL);
    GeoBone yaw = getAnimationProcessor().getBone(DISH_YAW);
    if (dish != null && tilt != null && roll != null && yaw != null) {
      dish.setRotX(DISH_UP);
      pose(relay, tilt, roll, yaw, state.getPartialTick());
    }
  }

  abstract void pose(T relay, GeoBone tilt, GeoBone roll, GeoBone yaw, float partialTick);

  /** Leans the panel as far as its animation went, from 0 to 1, toward an angle in degrees. */
  static void lean(GeoBone tilt, GeoBone roll, float toTilt, float toRoll) {
    float share = Mth.clamp(tilt.getRotX() / ANIMATED_TILT, 0f, 1f);
    tilt.setRotX(toTilt * Mth.DEG_TO_RAD * share);
    roll.setRotZ(-toRoll * Mth.DEG_TO_RAD * share);
  }
}
