package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * The bars and cubes of the line of an area, shaded by their faces, full where they are in sight
 * and faint behind a block (answers 2358 and 2361). The numbers are only visual, so they stay here.
 */
final class AreaLinePainter {
  private static final String SEEN_NAME = "cubit_area_line";
  private static final String HIDDEN_NAME = "cubit_area_line_hidden";
  private static final String WALL_NAME = "cubit_chunk_wall";
  private static final int BUFFER_SIZE = 4096;
  private static final float HIDDEN_ALPHA = 0.28f;
  private static final float TOP = 1f;
  private static final float SIDE_Z = 0.82f;
  private static final float SIDE_X = 0.66f;
  private static final float BOTTOM = 0.5f;

  /** In sight, and hiding the bars behind. */
  private static final RenderType SEEN =
      type(SEEN_NAME, RenderStateShard.LEQUAL_DEPTH_TEST, RenderStateShard.COLOR_DEPTH_WRITE);

  /** Only where something is in front. */
  private static final RenderType HIDDEN =
      type(HIDDEN_NAME, RenderStateShard.GREATER_DEPTH_TEST, RenderStateShard.COLOR_WRITE);

  /** In sight, with what is behind still in sight through it. */
  private static final RenderType WALL =
      type(WALL_NAME, RenderStateShard.LEQUAL_DEPTH_TEST, RenderStateShard.COLOR_WRITE);

  private final MultiBufferSource.BufferSource buffers;
  private final Matrix4f pose;

  AreaLinePainter(MultiBufferSource.BufferSource buffers, Vec3 camera) {
    this.buffers = buffers;
    PoseStack stack = new PoseStack();
    stack.translate(-camera.x, -camera.y, -camera.z);
    this.pose = stack.last().pose();
  }

  /** A color with how much of it shows. */
  record Paint(float red, float green, float blue, float alpha) {
    Paint faded(float share) {
      return new Paint(red, green, blue, alpha * share);
    }

    private Paint shaded(float light) {
      return new Paint(red * light, green * light, blue * light, alpha);
    }
  }

  /** The faint part first, so it never covers what is in sight. */
  void hidden(Iterable<AABB> boxes, Paint paint) {
    draw(HIDDEN, boxes, paint.faded(HIDDEN_ALPHA));
  }

  void seen(Iterable<AABB> boxes, Paint paint) {
    draw(SEEN, boxes, paint);
  }

  void wall(Iterable<AABB> boxes, Paint paint) {
    draw(WALL, boxes, paint);
  }

  private void draw(RenderType type, Iterable<AABB> boxes, Paint paint) {
    VertexConsumer quads = buffers.getBuffer(type);
    for (AABB box : boxes) {
      top(quads, box, paint);
      sides(quads, box, paint);
    }
    buffers.endBatch(type);
  }

  private void top(VertexConsumer quads, AABB box, Paint paint) {
    Paint light = paint.shaded(TOP);
    Paint dark = paint.shaded(BOTTOM);
    vertex(quads, box.minX, box.maxY, box.minZ, light);
    vertex(quads, box.minX, box.maxY, box.maxZ, light);
    vertex(quads, box.maxX, box.maxY, box.maxZ, light);
    vertex(quads, box.maxX, box.maxY, box.minZ, light);
    vertex(quads, box.minX, box.minY, box.maxZ, dark);
    vertex(quads, box.minX, box.minY, box.minZ, dark);
    vertex(quads, box.maxX, box.minY, box.minZ, dark);
    vertex(quads, box.maxX, box.minY, box.maxZ, dark);
  }

  private void sides(VertexConsumer quads, AABB box, Paint paint) {
    Paint alongX = paint.shaded(SIDE_Z);
    vertex(quads, box.minX, box.maxY, box.maxZ, alongX);
    vertex(quads, box.minX, box.minY, box.maxZ, alongX);
    vertex(quads, box.maxX, box.minY, box.maxZ, alongX);
    vertex(quads, box.maxX, box.maxY, box.maxZ, alongX);
    vertex(quads, box.maxX, box.maxY, box.minZ, alongX);
    vertex(quads, box.maxX, box.minY, box.minZ, alongX);
    vertex(quads, box.minX, box.minY, box.minZ, alongX);
    vertex(quads, box.minX, box.maxY, box.minZ, alongX);
    Paint alongZ = paint.shaded(SIDE_X);
    vertex(quads, box.maxX, box.maxY, box.maxZ, alongZ);
    vertex(quads, box.maxX, box.minY, box.maxZ, alongZ);
    vertex(quads, box.maxX, box.minY, box.minZ, alongZ);
    vertex(quads, box.maxX, box.maxY, box.minZ, alongZ);
    vertex(quads, box.minX, box.maxY, box.minZ, alongZ);
    vertex(quads, box.minX, box.minY, box.minZ, alongZ);
    vertex(quads, box.minX, box.minY, box.maxZ, alongZ);
    vertex(quads, box.minX, box.maxY, box.maxZ, alongZ);
  }

  private void vertex(VertexConsumer quads, double x, double y, double z, Paint paint) {
    quads
        .addVertex(pose, (float) x, (float) y, (float) z)
        .setColor(paint.red(), paint.green(), paint.blue(), paint.alpha());
  }

  private static RenderType type(
      String name,
      RenderStateShard.DepthTestStateShard depth,
      RenderStateShard.WriteMaskStateShard write) {
    return RenderType.create(
        name,
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        BUFFER_SIZE,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(depth)
            .setWriteMaskState(write)
            .createCompositeState(false));
  }
}
