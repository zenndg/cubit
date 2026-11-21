package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

/**
 * The hologram over the cloth: the item of the recipe, blue and turning, growing and filling in
 * with the progress. In a pause it stops and blinks in amber, and it shines when an item is ready
 * (answers 1465, 1470, 1471 and 1509).
 */
final class DeskHologram extends BlockAndItemGeoLayer<EngineeringDeskBlockEntity> {
  private static final String BONE = "hologram";
  private static final int BLUE = 0x55C8FF;
  private static final int SHINE = 0xD8F6FF;
  private static final float MIN_ALPHA = 0.25f;
  private static final float ALPHA_RANGE = 0.6f;
  private static final float SHINE_ALPHA = 1f;
  private static final float MIN_SCALE = 0.25f;
  private static final float SCALE_RANGE = 0.2f;
  private static final float LIFT = 0.3f;
  private static final float DEGREES_PER_TICK = 3f;
  private static final float BLINK_TICKS = 30f;
  private static final float HALF = 0.5f;
  private static final int MAX_CHANNEL = 255;

  DeskHologram(GeoRenderer<EngineeringDeskBlockEntity> renderer) {
    super(renderer);
  }

  @Override
  protected ItemStack getStackForBone(GeoBone bone, EngineeringDeskBlockEntity desk) {
    if (!BONE.equals(bone.getName()) || !isShown(desk)) {
      return null;
    }
    ItemStack result = desk.shownResult();
    return result.isEmpty() ? null : result;
  }

  private static boolean isShown(EngineeringDeskBlockEntity desk) {
    return desk.look().hasOrder() || desk.clientView().isShining(gameTime(desk));
  }

  @Override
  protected void renderStackForBone(
      PoseStack poseStack,
      GeoBone bone,
      ItemStack stack,
      EngineeringDeskBlockEntity desk,
      MultiBufferSource bufferSource,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    float ticks = gameTime(desk) + partialTick;
    boolean shining = desk.clientView().isShining(gameTime(desk));
    float share =
        shining
            ? 1f
            : desk.clientView().fraction(desk.look(), desk.spec().craftTicks(), gameTime(desk));
    float scale = MIN_SCALE + SCALE_RANGE * share;
    poseStack.translate(0, LIFT, 0);
    if (desk.look() == DeskLook.CRAFTING) {
      poseStack.mulPose(Axis.YP.rotationDegrees(ticks * DEGREES_PER_TICK));
    }
    poseStack.scale(scale, scale, scale);
    Minecraft.getInstance()
        .getItemRenderer()
        .renderStatic(
            stack,
            ItemDisplayContext.NONE,
            LightTexture.FULL_BRIGHT,
            packedOverlay,
            poseStack,
            new TintedBuffers(bufferSource, colour(desk.look(), shining, share, ticks)),
            desk.getLevel(),
            (int) desk.getBlockPos().asLong());
  }

  private static int colour(DeskLook look, boolean shining, float share, float ticks) {
    if (shining) {
      return withAlpha(SHINE, SHINE_ALPHA);
    }
    if (look == DeskLook.PAUSED) {
      float blink = HALF + HALF * Mth.sin(ticks * Mth.TWO_PI / BLINK_TICKS);
      return withAlpha(DeskGlow.PAUSE_AMBER, MIN_ALPHA + ALPHA_RANGE * HALF * blink);
    }
    return withAlpha(BLUE, MIN_ALPHA + ALPHA_RANGE * share);
  }

  private static int withAlpha(int rgb, float alpha) {
    return FastColor.ARGB32.color((int) (alpha * MAX_CHANNEL), rgb);
  }

  private static long gameTime(EngineeringDeskBlockEntity desk) {
    return desk.getLevel() == null ? 0 : desk.getLevel().getGameTime();
  }
}
