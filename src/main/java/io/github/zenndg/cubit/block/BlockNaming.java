package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.network.BlockNamedPayload;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The window of the name of a new Dock, Network Server or Solar Relay, as the one of the Cubit: it
 * opens for who places it, only with a new item, because a broken block keeps its id in the item,
 * and never for an item already named on an anvil (answers 2420 and 2422).
 */
public final class BlockNaming {
  /** The blocks placed for the first time, by who placed them, waiting for the window. */
  private static final Map<GlobalPos, UUID> WAITING = new HashMap<>();

  private BlockNaming() {}

  static void placed(Level level, BlockPos pos, LivingEntity placer, ItemStack stack) {
    if (!(placer instanceof ServerPlayer player)
        || !(level.getBlockEntity(pos) instanceof NamedBlock named)
        || stack.has(named.idComponent())
        || stack.has(DataComponents.CUSTOM_NAME)) {
      return;
    }
    WAITING.put(GlobalPos.of(level.dimension(), pos), player.getUUID());
    BlockIdentity identity = named.identity();
    CubitNetwork.askBlockName(player, pos, identity.kind(), identity.name().value());
  }

  /**
   * The answer of the window, once per placement, only from who placed the block, who still owns it
   * and reaches it. An empty name keeps the standard one (rule 20).
   */
  public static void answered(ServerPlayer player, BlockNamedPayload answer) {
    GlobalPos place = GlobalPos.of(player.level().dimension(), answer.pos());
    if (!WAITING.remove(place, player.getUUID()) || !reaches(player, answer.pos())) {
      return;
    }
    if (player.level().getBlockEntity(answer.pos()) instanceof NamedBlock named
        && named.isOwner(player)) {
      RobotName.typed(answer.name())
          .filter(name -> !name.removes())
          .ifPresent(name -> named.identity().rename(name.value()));
    }
  }

  private static boolean reaches(ServerPlayer player, BlockPos pos) {
    return player.level().isLoaded(pos)
        && player.canInteractWithBlock(pos, Container.DEFAULT_DISTANCE_BUFFER);
  }
}
