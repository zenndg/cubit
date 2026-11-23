package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.entity.LaserOrb;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * The laser orb of the Galactic Patroller: a small blue ball of light, the color of the eyes of the
 * Cubit, that always faces the camera and glows in the dark (answer 1558). Its blue trail and its
 * burst are particles of the orb itself.
 */
final class LaserOrbRenderer extends EntityRenderer<LaserOrb> {
  private static final String TEXTURE_PATH = "textures/entity/laser_orb.png";
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, TEXTURE_PATH);
  private static final RenderType TYPE = RenderType.entityTranslucentEmissive(TEXTURE);
  private static final float SIZE = 0.4f;
  private static final float HALF = 0.5f;
  private static final int WHITE = -1;

  LaserOrbRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public void render(
      LaserOrb orb,
      float yaw,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffers,
      int packedLight) {
    poseStack.pushPose();
    poseStack.scale(SIZE, SIZE, SIZE);
    poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
    PoseStack.Pose pose = poseStack.last();
    VertexConsumer buffer = buffers.getBuffer(TYPE);
    vertex(buffer, pose, -HALF, -HALF, 0, 1);
    vertex(buffer, pose, HALF, -HALF, 1, 1);
    vertex(buffer, pose, HALF, HALF, 1, 0);
    vertex(buffer, pose, -HALF, HALF, 0, 0);
    poseStack.popPose();
    super.render(orb, yaw, partialTick, poseStack, buffers, packedLight);
  }

  private static void vertex(
      VertexConsumer buffer, PoseStack.Pose pose, float x, float y, int u, int v) {
    buffer
        .addVertex(pose, x, y, 0)
        .setColor(WHITE)
        .setUv(u, v)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(LightTexture.FULL_BRIGHT)
        .setNormal(pose, 0, 1, 0);
  }

  @Override
  public ResourceLocation getTextureLocation(LaserOrb orb) {
    return TEXTURE;
  }
}
