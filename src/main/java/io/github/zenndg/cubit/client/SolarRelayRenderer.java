package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.block.NetworkBlock;
import io.github.zenndg.cubit.block.RelayClientView;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Draws the whole Solar Relay from its lower half, with the band of the tier of its server, the
 * panel after the sun, the dish and the lights of the link (answers 2217, 2338 and 2374).
 */
public final class SolarRelayRenderer extends GeoBlockRenderer<SolarRelayBlockEntity> {
  public SolarRelayRenderer(BlockEntityRendererProvider.Context context) {
    super(
        new SolarRelayModel<>() {
          @Override
          void pose(
              SolarRelayBlockEntity relay,
              GeoBone tilt,
              GeoBone roll,
              GeoBone yaw,
              float partialTick) {
            RelayClientView view = relay.view();
            lean(tilt, roll, view.tilt(partialTick), view.roll(partialTick));
            yaw.setRotY(view.dish(partialTick) * Mth.DEG_TO_RAD);
          }
        });
    addRenderLayer(
        new NetworkGlowLayer<>(this, CubitBlocks.SOLAR_RELAY_ID, relay -> relay.view().linked()));
  }

  @Override
  public AABB getRenderBoundingBox(SolarRelayBlockEntity relay) {
    return new AABB(relay.getBlockPos()).expandTowards(0, 1, 0);
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      SolarRelayBlockEntity relay,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
    boolean lit = relay.view().linked();
    boolean light = RelayLights.isLight(bone);
    int tinted =
        TierBand.colour(bone, relay.getBlockState().getValue(NetworkBlock.TIER).tier(), colour);
    super.renderRecursively(
        poseStack,
        relay,
        bone,
        renderType,
        bufferSource,
        buffer,
        isReRender,
        partialTick,
        light && lit ? LightTexture.FULL_BRIGHT : packedLight,
        packedOverlay,
        light ? RelayLights.colour(lit) : tinted);
  }
}
