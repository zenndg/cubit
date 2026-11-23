package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.dock.DockDoorLight;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Draws the whole dock from its lower part, turned by its facing, with the band and the body of its
 * tier and the screen of its status, both read from the block state, the lights of the doors and
 * the pulse of the panel (answers 1853, 1860, 1863 and 1886).
 */
public final class DockRenderer extends GeoBlockRenderer<InductionDockBlockEntity> {
  /** The dock is three blocks tall with its panel, which never leaves them (answers 1892, 1893). */
  private static final int BLOCKS_ABOVE = 2;

  public DockRenderer(BlockEntityRendererProvider.Context context) {
    super(
        new DockGeoModel<>() {
          @Override
          RobotTier tierOf(InductionDockBlockEntity dock) {
            return DockRenderer.tierOf(dock);
          }
        });
    addRenderLayer(
        new DockScreenLayer<>(
            this, dock -> dock.getBlockState().getValue(InductionDockBlock.STATUS)));
    addRenderLayer(new DockPanelGlow(this));
  }

  static double ticks(InductionDockBlockEntity dock, float partialTick) {
    return dock.getLevel() == null ? partialTick : dock.getLevel().getGameTime() + partialTick;
  }

  private static RobotTier tierOf(InductionDockBlockEntity dock) {
    return dock.getBlockState().getValue(InductionDockBlock.TIER).tier();
  }

  @Override
  public AABB getRenderBoundingBox(InductionDockBlockEntity dock) {
    return new AABB(dock.getBlockPos()).expandTowards(0, BLOCKS_ABOVE, 0);
  }

  @Override
  public void renderRecursively(
      PoseStack poseStack,
      InductionDockBlockEntity dock,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour) {
    boolean light = DockLights.isLight(bone);
    int tinted = TierBand.colour(bone, tierOf(dock), colour);
    super.renderRecursively(
        poseStack,
        dock,
        bone,
        renderType,
        bufferSource,
        buffer,
        isReRender,
        partialTick,
        light ? LightTexture.FULL_BRIGHT : packedLight,
        packedOverlay,
        light ? doorLight(dock, partialTick, tinted) : tinted);
  }

  private static int doorLight(InductionDockBlockEntity dock, float partialTick, int colour) {
    DockDoorLight light = DockDoorLight.of(dock.solar().panel());
    return DockLights.colour(light, (long) ticks(dock, partialTick), colour);
  }
}
