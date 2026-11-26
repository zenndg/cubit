package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.block.AntennaClientView;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Draws the whole Signal Antenna from its lower half, with the band and the ring in the color of
 * its channel and the LED lit while it has signal (answers 2556 and 2557).
 */
public final class SignalAntennaRenderer extends GeoBlockRenderer<SignalAntennaBlockEntity> {
  public SignalAntennaRenderer(BlockEntityRendererProvider.Context context) {
    super(new SignalAntennaModel<>());
    addRenderLayer(
        new NetworkGlowLayer<>(
            this, CubitBlocks.SIGNAL_ANTENNA_ID, antenna -> antenna.view().linked()));
  }

  @Override
  public AABB getRenderBoundingBox(SignalAntennaBlockEntity antenna) {
    return new AABB(antenna.getBlockPos()).expandTowards(0, 1, 0);
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      SignalAntennaBlockEntity antenna,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
    AntennaClientView view = antenna.view();
    boolean lit = view.linked();
    boolean light = RelayLights.isLight(bone);
    super.renderRecursively(
        poseStack,
        antenna,
        bone,
        renderType,
        bufferSource,
        buffer,
        isReRender,
        partialTick,
        light && lit ? LightTexture.FULL_BRIGHT : packedLight,
        packedOverlay,
        light ? RelayLights.colour(lit) : ChannelBones.colour(bone, view.channel(), colour));
  }
}
