package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.NetworkItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** The item of the Solar Relay, with the panel folded, the lights off and the grey band (2374). */
public final class SolarRelayItemRenderer extends GeoItemRenderer<NetworkItem> {
  public SolarRelayItemRenderer() {
    super(new ItemModel());
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      NetworkItem item,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
    int tinted = TierBand.colour(bone, RobotTier.BASE, colour);
    super.renderRecursively(
        poseStack,
        item,
        bone,
        renderType,
        bufferSource,
        buffer,
        isReRender,
        partialTick,
        packedLight,
        packedOverlay,
        RelayLights.isLight(bone) ? RelayLights.colour(false) : tinted);
  }

  private static final class ItemModel extends SolarRelayModel<NetworkItem> {
    private static final String LEFT = "leaf_l";
    private static final String RIGHT = "leaf_r";

    /** The leaves go over the middle one, each from its side. */
    private static final float FOLDED = Mth.PI;

    @Override
    public void setCustomAnimations(
        NetworkItem item, long instanceId, AnimationState<NetworkItem> state) {
      super.setCustomAnimations(item, instanceId, state);
      fold(LEFT, FOLDED);
      fold(RIGHT, -FOLDED);
    }

    private void fold(String name, float angle) {
      GeoBone leaf = getAnimationProcessor().getBone(name);
      if (leaf != null) {
        leaf.setRotY(angle);
      }
    }

    @Override
    void pose(NetworkItem item, GeoBone tilt, GeoBone roll, GeoBone yaw, float partialTick) {
      tilt.setRotX(0);
      roll.setRotZ(0);
      yaw.setRotY(0);
    }
  }
}
