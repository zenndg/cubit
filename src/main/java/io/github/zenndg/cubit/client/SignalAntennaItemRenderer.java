package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.item.NetworkItem;
import io.github.zenndg.cubit.item.SavedNetwork;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * The item of the Signal Antenna, with its mast up, the LED off and the band in the color of its
 * last channel, when it has one (answer 2558).
 */
public final class SignalAntennaItemRenderer extends GeoItemRenderer<NetworkItem> {
  public SignalAntennaItemRenderer() {
    super(new SignalAntennaModel<>());
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
    int tinted =
        ChannelBones.colour(bone, SavedNetwork.of(getCurrentItemStack()).link().channel(), colour);
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
}
