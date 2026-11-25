package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.block.NetworkBlock;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Draws the whole Network Server from its lower half, turned by its facing, with the band and the
 * body of its tier, the front and the lights of its state (answers 2329, 2338 and 2388).
 */
public final class NetworkServerRenderer extends GeoBlockRenderer<NetworkServerBlockEntity> {
  public NetworkServerRenderer(BlockEntityRendererProvider.Context context) {
    super(
        new NetworkServerModel<>() {
          @Override
          RobotTier tierOf(NetworkServerBlockEntity server) {
            return NetworkServerRenderer.tierOf(server);
          }

          @Override
          float fanDegrees(NetworkServerBlockEntity server, float partialTick) {
            return server.view().fanDegrees(partialTick);
          }
        });
    addRenderLayer(new ServerFrontLayer<>(this, NetworkServerRenderer::shown));
    addRenderLayer(
        new NetworkGlowLayer<>(
            this, CubitBlocks.NETWORK_SERVER_ID, server -> server.view().light(1f) > 0));
  }

  private static RobotTier tierOf(NetworkServerBlockEntity server) {
    return server.getBlockState().getValue(NetworkBlock.TIER).tier();
  }

  private static ServerShown shown(NetworkServerBlockEntity server, float partialTick) {
    long age = server.getLevel() == null ? 0 : server.getLevel().getGameTime();
    return ServerShown.of(server.view(), age, partialTick);
  }

  @Override
  public AABB getRenderBoundingBox(NetworkServerBlockEntity server) {
    return new AABB(server.getBlockPos()).expandTowards(0, 1, 0).inflate(1);
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      NetworkServerBlockEntity server,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
    ServerPart part =
        ServerPart.of(bone, shown(server, partialTick), tierOf(server), isReRender, colour);
    super.renderRecursively(
        poseStack,
        server,
        bone,
        renderType,
        bufferSource,
        buffer,
        isReRender,
        partialTick,
        part.bright() ? LightTexture.FULL_BRIGHT : packedLight,
        packedOverlay,
        part.colour());
  }
}
