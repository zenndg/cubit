package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.util.FastColor;

/**
 * Draws through another buffer source with every vertex tinted, for the hologram (answer 1509).
 * Solid and cut out blocks move to the translucent sheet, so their alpha shows. Items drawn by
 * their own renderer keep their type, and on them only the color shows.
 */
final class TintedBuffers implements MultiBufferSource {
  private final MultiBufferSource source;
  private final int argb;

  TintedBuffers(MultiBufferSource source, int argb) {
    this.source = source;
    this.argb = argb;
  }

  @Override
  public VertexConsumer getBuffer(RenderType type) {
    return new Tinted(source, translucent(type), argb);
  }

  private static RenderType translucent(RenderType type) {
    boolean blockSheet =
        type == Sheets.solidBlockSheet()
            || type == Sheets.cutoutBlockSheet()
            || type == RenderType.solid()
            || type == RenderType.cutout()
            || type == RenderType.cutoutMipped();
    return blockSheet ? Sheets.translucentCullBlockSheet() : type;
  }

  /**
   * The buffer is asked again at each vertex: a layer of an item, as the lights of the Network
   * Server, may draw with another type in the middle, which closes the buffer of the body. The game
   * gives back the same one while nothing changed, and a new one after.
   */
  private static final class Tinted implements VertexConsumer {
    private final MultiBufferSource source;
    private final RenderType type;
    private final int argb;
    private VertexConsumer target;

    Tinted(MultiBufferSource source, RenderType type, int argb) {
      this.source = source;
      this.type = type;
      this.argb = argb;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
      target = source.getBuffer(type);
      target.addVertex(x, y, z);
      return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      int tinted = FastColor.ARGB32.multiply(FastColor.ARGB32.color(alpha, red, green, blue), argb);
      target.setColor(
          FastColor.ARGB32.red(tinted),
          FastColor.ARGB32.green(tinted),
          FastColor.ARGB32.blue(tinted),
          FastColor.ARGB32.alpha(tinted));
      return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
      target.setUv(u, v);
      return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
      target.setUv1(u, v);
      return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
      target.setUv2(u, v);
      return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
      target.setNormal(x, y, z);
      return this;
    }
  }
}
