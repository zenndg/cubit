package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;

/**
 * Draws small boxes of one color over a GeckoLib model, from places in the pixels of the model seen
 * from the front: x to the right, y up and z away. GeckoLib flips x, so the boxes do the same.
 */
record PixelBoxes(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay) {
  private static final float PIXELS = 16f;
  private static final float MIDDLE = 8f;

  /** A box from its lower left front corner to its upper right back one. */
  record Box(float x, float y, float z, float toX, float toY, float toZ) {}

  void draw(Box box, int colour) {
    lit(box, colour, packedLight);
  }

  void lit(Box box, int colour, int light) {
    float left = at(-(box.toX() - MIDDLE));
    float right = at(-(box.x() - MIDDLE));
    float bottom = at(box.y());
    float top = at(box.toY());
    float front = at(box.z() - MIDDLE);
    float back = at(box.toZ() - MIDDLE);
    Face face = new Face(colour, light);
    face.across(Direction.NORTH, new float[] {right, left}, new float[] {bottom, top}, front);
    face.level(Direction.UP, new float[] {right, left}, top, new float[] {front, back});
    face.level(Direction.DOWN, new float[] {left, right}, bottom, new float[] {front, back});
    face.side(Direction.WEST, left, new float[] {bottom, top}, new float[] {front, back});
    face.side(Direction.EAST, right, new float[] {bottom, top}, new float[] {back, front});
  }

  private static float at(float pixels) {
    return pixels / PIXELS;
  }

  /** The faces of a box, all of the same color; the back is never seen. */
  private final class Face {
    private final int colour;
    private final int light;

    Face(int colour, int light) {
      this.colour = colour;
      this.light = light;
    }

    void across(Direction normal, float[] x, float[] y, float z) {
      vertex(normal, x[0], y[0], z);
      vertex(normal, x[1], y[0], z);
      vertex(normal, x[1], y[1], z);
      vertex(normal, x[0], y[1], z);
    }

    void level(Direction normal, float[] x, float y, float[] z) {
      vertex(normal, x[0], y, z[0]);
      vertex(normal, x[1], y, z[0]);
      vertex(normal, x[1], y, z[1]);
      vertex(normal, x[0], y, z[1]);
    }

    void side(Direction normal, float x, float[] y, float[] z) {
      vertex(normal, x, y[0], z[0]);
      vertex(normal, x, y[0], z[1]);
      vertex(normal, x, y[1], z[1]);
      vertex(normal, x, y[1], z[0]);
    }

    private void vertex(Direction normal, float x, float y, float z) {
      buffer
          .addVertex(pose, x, y, z)
          .setColor(colour)
          .setUv(0, 0)
          .setOverlay(packedOverlay)
          .setLight(light)
          .setNormal(pose, normal.getStepX(), normal.getStepY(), normal.getStepZ());
    }
  }
}
