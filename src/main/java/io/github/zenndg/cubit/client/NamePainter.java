package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.zenndg.cubit.domain.ownership.FloatingName;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * One name over a block: only the white text of the font of the game, with its shadow, without a
 * back or a border (answers 2518 and 2519). Its size is the one of the world, as the name tag of
 * the game, so it shrinks far away (2515). Through blocks it shows at 32%, and in full in front of
 * them (2425).
 */
final class NamePainter {
  /** The size of a pixel of the font in the world, the one of the name tag of the game. */
  private static final float PIXEL = 0.025f;

  private static final int GLYPH_ROWS = 8;
  private static final int TEXT_GAP = 1;
  private static final int WHITE_RGB = 0xFFFFFF;
  private static final float THROUGH_ALPHA = 0.32f;
  private static final float FULL = 1f;
  private static final int CHANNEL_MAX = 255;
  private static final int NO_BACK = 0;
  private static final boolean SHADOW = true;

  private final MultiBufferSource.BufferSource buffers;
  private final Font font;
  private final Camera camera;

  NamePainter(MultiBufferSource.BufferSource buffers, Font font, Camera camera) {
    this.buffers = buffers;
    this.font = font;
    this.camera = camera;
  }

  /** Through blocks, faint: drawn before the full one, so the front wins. */
  void throughBlocks(FloatingName spot, String name) {
    paint(spot, name, Font.DisplayMode.SEE_THROUGH, THROUGH_ALPHA);
  }

  void inFront(FloatingName spot, String name) {
    paint(spot, name, Font.DisplayMode.NORMAL, FULL);
  }

  /** Centered over the spot, with the bottom of the letters on it. */
  private void paint(FloatingName spot, String name, Font.DisplayMode mode, float alpha) {
    Matrix4f matrix = facingCamera(spot).last().pose();
    float left = -(font.width(name) - TEXT_GAP) / 2f;
    int white = FastColor.ARGB32.color(Math.round(alpha * CHANNEL_MAX), WHITE_RGB);
    font.drawInBatch(
        name,
        left,
        -GLYPH_ROWS,
        white,
        SHADOW,
        matrix,
        buffers,
        mode,
        NO_BACK,
        LightTexture.FULL_BRIGHT);
    buffers.endBatch();
  }

  /** At the spot, turned to the camera, with the pixels of the font as the unit. */
  private PoseStack facingCamera(FloatingName spot) {
    Vec3 at = new Vec3(spot.x(), spot.y(), spot.z()).subtract(camera.getPosition());
    PoseStack pose = new PoseStack();
    pose.translate(at.x, at.y, at.z);
    pose.mulPose(camera.rotation());
    pose.scale(PIXEL, -PIXEL, PIXEL);
    return pose;
  }
}
