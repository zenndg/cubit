package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.network.AntennaChannel;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;

/**
 * The colors of a channel on the antenna: the band and the ring in the dye of the game, and their
 * thread dark on the light colors and white on the dark ones, as the accepted texture (answer
 * 2556), and the wall of its chunk, translucent (answer 2542).
 */
final class ChannelTint {
  private static final int DARK_THREAD = FastColor.ARGB32.color(255, 12, 12, 14);
  private static final int LIGHT_THREAD = FastColor.ARGB32.color(255, 236, 238, 240);
  private static final double RED_WEIGHT = 0.299;
  private static final double GREEN_WEIGHT = 0.587;
  private static final double BLUE_WEIGHT = 0.114;
  private static final double LIGHT = 140;
  private static final float WALL_ALPHA = 0.22f;
  private static final float WHOLE = 255f;

  private ChannelTint() {}

  static int band(AntennaChannel channel) {
    return FastColor.ARGB32.opaque(dye(channel));
  }

  static int thread(AntennaChannel channel) {
    int color = dye(channel);
    double light =
        RED_WEIGHT * FastColor.ARGB32.red(color)
            + GREEN_WEIGHT * FastColor.ARGB32.green(color)
            + BLUE_WEIGHT * FastColor.ARGB32.blue(color);
    return light > LIGHT ? DARK_THREAD : LIGHT_THREAD;
  }

  static AreaLinePainter.Paint wall(AntennaChannel channel) {
    int color = dye(channel);
    return new AreaLinePainter.Paint(
        FastColor.ARGB32.red(color) / WHOLE,
        FastColor.ARGB32.green(color) / WHOLE,
        FastColor.ARGB32.blue(color) / WHOLE,
        WALL_ALPHA);
  }

  private static int dye(AntennaChannel channel) {
    return DyeColor.byName(channel.key(), DyeColor.WHITE).getTextureDiffuseColor();
  }
}
