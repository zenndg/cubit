package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.zenndg.cubit.domain.network.ServerFront;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * The 16 lamps and the three columns of 10 bars on the door of the Network Server, drawn by code on
 * the dark plates of the model, so each one shows its own state, also with the door open (answers
 * 2329 and 2341). The numbers are the places of the model, in pixels.
 */
final class ServerFrontLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
  private static final String DOOR = "door";
  private static final String WHITE_PATH = "textures/misc/white.png";
  private static final RenderType TYPE =
      RenderType.entityCutoutNoCull(ResourceLocation.withDefaultNamespace(WHITE_PATH));
  private static final int LAMPS_PER_ROW = 4;
  private static final float LAMP_X = 4.35f;
  private static final float LAMP_Y = 22.95f;
  private static final float LAMP_STEP_X = 1.95f;
  private static final float LAMP_STEP_Y = 1.9f;
  private static final float LAMP_FROM = 0.1f;
  private static final float LAMP_TO_X = 1.35f;
  private static final float LAMP_TO_Y = 1.4f;
  private static final float DOT_FROM = 0.5f;
  private static final float DOT_TO_X = 0.95f;
  private static final float DOT_TO_Y = 1f;
  private static final float BAR_Y = 6.3f;
  private static final float BAR_STEP = 0.77f;
  private static final float BAR_HEIGHT = 0.55f;
  private static final float FRONT = 1f;
  private static final float CLOSED_FRONT = 1.02f;
  private static final float DOT_FRONT = 0.95f;
  private static final float BACK = 1.1f;
  private static final float[] ENERGY_COLUMN = {4f, 6.4f};
  private static final float[] XP_COLUMN = {6.9f, 9.1f};
  private static final float[] ITEMS_COLUMN = {9.6f, 12.1f};
  private static final int OFF = 0xFF1E2025;
  private static final int CLOSED = 0xFF09090B;
  private static final int DOT = 0xFF686D76;
  private static final int DOT_OFF = 0xFF3A3D44;
  private static final int CYAN = 0xFF6EE2F8;
  private static final int ORANGE = 0xFFFFAC46;

  private final BiFunction<T, Float, ServerShown> shown;

  ServerFrontLayer(GeoRenderer<T> renderer, BiFunction<T, Float, ServerShown> shown) {
    super(renderer);
    this.shown = shown;
  }

  @Override
  public void renderForBone(
      PoseStack poseStack,
      T animatable,
      GeoBone bone,
      RenderType bodyType,
      MultiBufferSource buffers,
      VertexConsumer bodyBuffer,
      float partialTick,
      int packedLight,
      int packedOverlay) {
    if (!DOOR.equals(bone.getName()) || bodyType == null) {
      return;
    }
    ServerShown now = shown.apply(animatable, partialTick);
    PixelBoxes boxes =
        new PixelBoxes(poseStack.last(), buffers.getBuffer(TYPE), packedLight, packedOverlay);
    drawLamps(boxes, now);
    drawColumn(boxes, ENERGY_COLUMN, now.bars().energy(), CYAN, now);
    drawColumn(boxes, XP_COLUMN, 0, OFF, now);
    drawColumn(boxes, ITEMS_COLUMN, now.bars().items(), ORANGE, now);
    buffers.getBuffer(bodyType);
  }

  private static void drawLamps(PixelBoxes boxes, ServerShown now) {
    boolean powered = now.light() > 0;
    for (int index = 0; index < ServerSpec.MAX_CHANNELS; index++) {
      float x = LAMP_X + index % LAMPS_PER_ROW * LAMP_STEP_X;
      float y = LAMP_Y - index / LAMPS_PER_ROW * LAMP_STEP_Y;
      boolean closed =
          ServerFront.lamp(index, now.look().channels(), powered) == ServerFront.Lamp.CLOSED;
      boxes.draw(
          new PixelBoxes.Box(
              x + LAMP_FROM,
              y + LAMP_FROM,
              closed ? CLOSED_FRONT : FRONT,
              x + LAMP_TO_X,
              y + LAMP_TO_Y,
              BACK),
          closed ? CLOSED : OFF);
      if (!closed) {
        boxes.draw(
            new PixelBoxes.Box(
                x + DOT_FROM, y + DOT_FROM, DOT_FRONT, x + DOT_TO_X, y + DOT_TO_Y, FRONT),
            powered ? DOT : DOT_OFF);
      }
    }
  }

  /** The bars climb with the light of the startup. */
  private static void drawColumn(
      PixelBoxes boxes, float[] column, int lit, int colour, ServerShown now) {
    float power = ServerLights.power(now);
    for (int index = 0; index < ServerFront.BARS; index++) {
      float y = BAR_Y + index * BAR_STEP;
      PixelBoxes.Box bar = new PixelBoxes.Box(column[0], y, FRONT, column[1], y + BAR_HEIGHT, BACK);
      if (index < lit && now.light() * ServerFront.BARS >= index && power > 0) {
        boxes.lit(bar, FastColor.ARGB32.lerp(power, OFF, colour), LightTexture.FULL_BRIGHT);
      } else {
        boxes.draw(bar, OFF);
      }
    }
  }
}
