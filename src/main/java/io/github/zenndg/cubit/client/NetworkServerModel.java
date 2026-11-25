package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

/**
 * The model of the Network Server, the same for the block and its item. The Ultimate has a body of
 * netherite (answer 2341), and the four fans turn by code, to speed up and slow down.
 */
abstract class NetworkServerModel<T extends GeoAnimatable> extends GeoModel<T> {
  private static final String MODEL_PATH = "geo/block/network_server.geo.json";
  private static final String ANIMATIONS_PATH = "animations/block/network_server.animation.json";
  private static final String TEXTURE_PATH = "textures/block/network_server.png";
  private static final String ULTIMATE_PATH = "textures/block/network_server_ultimate.png";
  private static final ResourceLocation MODEL = path(MODEL_PATH);
  private static final ResourceLocation ANIMATIONS = path(ANIMATIONS_PATH);
  private static final ResourceLocation TEXTURE = path(TEXTURE_PATH);
  private static final ResourceLocation ULTIMATE_TEXTURE = path(ULTIMATE_PATH);

  /** Each fan starts at its own angle, so they do not look like one. */
  private static final String[] FANS = {"fan_l0", "fan_l1", "fan_r0", "fan_r1"};

  private static final float APART = 22f;

  abstract RobotTier tierOf(T server);

  abstract float fanDegrees(T server, float partialTick);

  private static ResourceLocation path(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  @Override
  public ResourceLocation getModelResource(T server) {
    return MODEL;
  }

  @Override
  public ResourceLocation getTextureResource(T server) {
    return tierOf(server) == RobotTier.ULTIMATE ? ULTIMATE_TEXTURE : TEXTURE;
  }

  @Override
  public ResourceLocation getAnimationResource(T server) {
    return ANIMATIONS;
  }

  @Override
  public void setCustomAnimations(T server, long instanceId, AnimationState<T> state) {
    super.setCustomAnimations(server, instanceId, state);
    float degrees = fanDegrees(server, state.getPartialTick());
    for (int index = 0; index < FANS.length; index++) {
      GeoBone fan = getAnimationProcessor().getBone(FANS[index]);
      if (fan != null) {
        fan.setRotX((degrees + index * APART) * Mth.DEG_TO_RAD);
      }
    }
  }
}
