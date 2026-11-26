package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.network.ChunkWall;
import io.github.zenndg.cubit.domain.network.WallColors;
import io.github.zenndg.cubit.item.TabletItem;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;

/**
 * The wall around the chunk where the player stands, switched by Shift and a click of the Network
 * app and kept in the Tablet (answers 2608 to 2616). It follows the player: on each new chunk, and
 * when it goes on, it takes its base from the block under the feet and one of six colors, never the
 * last one. The color lives only here, on the client.
 */
final class PlayerChunkWall {
  private static final float ALPHA = 0.22f;
  private static final float WHOLE = 255f;
  private static final int BYTE = 0xFF;
  private static final int RED_SHIFT = 16;
  private static final int GREEN_SHIFT = 8;
  private static final int[] COLORS = {0xEE6FB5, 0xF2C94C, 0x8BD35A, 0x4FA3FF, 0x9966F2, 0xF0F0F0};
  private static final RandomSource RANDOM = RandomSource.create();

  private static ChunkPos chunk;
  private static ChunkWall wall;
  private static int color = WallColors.NONE;

  private PlayerChunkWall() {}

  /** The wall and its paint, while this player shows it. */
  static Optional<Shown> of(Player player) {
    if (!NetworkAppHeld.by(player) || !shownBy(player)) {
      chunk = null;
      color = WallColors.NONE;
      return Optional.empty();
    }
    ChunkPos here = player.chunkPosition();
    if (!here.equals(chunk)) {
      chunk = here;
      BlockPos feet = player.blockPosition();
      wall = ChunkWall.around(feet.getX(), feet.getY(), feet.getZ());
      color = WallColors.next(color, RANDOM.nextInt());
    }
    return Optional.of(new Shown(wall, paint(COLORS[color])));
  }

  private static boolean shownBy(Player player) {
    return shows(player.getMainHandItem()) || shows(player.getOffhandItem());
  }

  private static boolean shows(ItemStack stack) {
    return NetworkAppHeld.inNetworkApp(stack) && TabletItem.showsChunkWall(stack);
  }

  private static AreaLinePainter.Paint paint(int rgb) {
    return new AreaLinePainter.Paint(
        (rgb >> RED_SHIFT & BYTE) / WHOLE,
        (rgb >> GREEN_SHIFT & BYTE) / WHOLE,
        (rgb & BYTE) / WHOLE,
        ALPHA);
  }

  /** A wall and the paint it shows in. */
  record Shown(ChunkWall wall, AreaLinePainter.Paint paint) {}
}
