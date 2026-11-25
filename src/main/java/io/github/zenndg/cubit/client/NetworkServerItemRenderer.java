package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.network.ServerFront;
import io.github.zenndg.cubit.domain.network.ServerLook;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.NetworkItem;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/** The item of the Network Server, still, with the tier, the energy and the items it keeps. */
public final class NetworkServerItemRenderer extends GeoItemRenderer<NetworkItem> {
  public NetworkServerItemRenderer() {
    super(new ItemModel());
    addRenderLayer(
        new ServerFrontLayer<>(this, (item, partialTick) -> ServerShown.still(ItemModel.look)));
    addRenderLayer(
        new NetworkGlowLayer<>(
            this, CubitBlocks.NETWORK_SERVER_ID, item -> ItemModel.look.powered()));
  }

  /** What is drawn follows the stack, read on the render thread. */
  @Override
  public void renderByItem(
      ItemStack stack,
      ItemDisplayContext context,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay) {
    SavedNetwork saved = SavedNetwork.of(stack);
    ItemModel.tier = saved.tier();
    ItemModel.look = lookOf(saved);
    super.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
  }

  private static ServerLook lookOf(SavedNetwork saved) {
    ServerSpec spec = CubitMod.CONFIG.network().server(saved.tier());
    ServerLook.Columns bars =
        new ServerLook.Columns(
            ServerFront.bars(saved.energy(), spec.capacity().amount()),
            ServerFront.bars(saved.items().size(), spec.storageSlots()));
    return new ServerLook(saved.energy() > 0, false, false, bars, spec.channels());
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
    ServerPart part =
        ServerPart.of(bone, ServerShown.still(ItemModel.look), ItemModel.tier, isReRender, colour);
    super.renderRecursively(
        poseStack,
        item,
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

  private static final class ItemModel extends NetworkServerModel<NetworkItem> {
    private static RobotTier tier = RobotTier.BASE;
    private static ServerLook look = ServerLook.OFF;

    @Override
    RobotTier tierOf(NetworkItem item) {
      return tier;
    }

    @Override
    float fanDegrees(NetworkItem item, float partialTick) {
      return 0;
    }
  }
}
