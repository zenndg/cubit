package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.block.DockStatusProperty;
import io.github.zenndg.cubit.domain.dock.DockDoorLight;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.InductionDockItem;
import io.github.zenndg.cubit.item.TierLook;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * The item of the dock, drawn by the same model, with the panel in its cabinet, the idle screen and
 * the tier kept in the item (answers 1373 and 1860).
 */
public final class DockItemRenderer extends GeoItemRenderer<InductionDockItem> {
  public DockItemRenderer() {
    super(new ItemModel());
    addRenderLayer(new DockScreenLayer<>(this, item -> DockStatusProperty.IDLE, item -> false));
  }

  /** The texture follows the tier of the stack being drawn, read on the render thread. */
  @Override
  public void renderByItem(
      ItemStack stack,
      ItemDisplayContext context,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay) {
    ItemModel.drawn = TierLook.dockTierOf(stack);
    super.renderByItem(stack, context, poseStack, bufferSource, packedLight, packedOverlay);
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      InductionDockItem item,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
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
        DockLights.isLight(bone)
            ? DockLights.colour(DockDoorLight.RED, 0, colour)
            : TierBand.colour(bone, ItemModel.drawn, colour));
  }

  private static final class ItemModel extends DockGeoModel<InductionDockItem> {
    private static RobotTier drawn = RobotTier.BASE;

    @Override
    RobotTier tierOf(InductionDockItem item) {
      return drawn;
    }
  }
}
