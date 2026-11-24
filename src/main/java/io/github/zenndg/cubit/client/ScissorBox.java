package io.github.zenndg.cubit.client;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Cuts what is drawn to a box given in the coordinates of the pose, as the panels draw. */
final class ScissorBox {
  private ScissorBox() {}

  static void run(GuiGraphics graphics, int left, int top, int right, int bottom, Runnable draw) {
    Matrix4f pose = graphics.pose().last().pose();
    Vector3f from = pose.transformPosition(left, top, 0, new Vector3f());
    Vector3f to = pose.transformPosition(right, bottom, 0, new Vector3f());
    graphics.enableScissor(
        Math.round(from.x()), Math.round(from.y()), Math.round(to.x()), Math.round(to.y()));
    draw.run();
    graphics.disableScissor();
  }
}
