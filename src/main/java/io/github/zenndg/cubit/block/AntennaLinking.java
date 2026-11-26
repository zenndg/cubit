package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A click with the item of the antenna, or a stack of them, on a Network Server of its owner links
 * the whole stack to it, with the name the Server has now. On a Server of another player, nothing
 * changes, and the reason is shown (Delivery 4, detail 13, and answers 2566 and 2567).
 */
public final class AntennaLinking {
  public static final String LINKED_KEY = "notice.cubit.antenna_linked";

  private AntennaLinking() {}

  /** The Server at that place, by either half, or none. */
  public static NetworkServerBlockEntity serverAt(BlockGetter level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    return state.getBlock() instanceof NetworkServerBlock
            && NetworkBlock.networkAt(level, pos, state) instanceof NetworkServerBlockEntity server
        ? server
        : null;
  }

  /** On the server only. */
  public static void link(NetworkServerBlockEntity server, Player player, ItemStack stack) {
    if (!server.isOwner(player)) {
      player.displayClientMessage(Component.translatable(AntennaPlacing.OTHER_OWNER_KEY), true);
      return;
    }
    String name = server.identity().name().value();
    SavedNetwork saved = SavedNetwork.of(stack);
    stack.set(
        CubitDataComponents.NETWORK_STATE.get(),
        saved.withLink(saved.link().linkedTo(server.identity().id(), name)));
    player.displayClientMessage(Component.translatable(LINKED_KEY, name), true);
    ServerShow.play(player.level(), server.getBlockPos(), CubitSounds.ANTENNA_LINK.get());
  }
}
