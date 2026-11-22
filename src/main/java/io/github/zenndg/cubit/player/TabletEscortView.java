package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.network.EscortMarksPayload;
import io.github.zenndg.cubit.registry.CubitEntities;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * The ones escorted by each Galactic Patroller of the player, while the Escort app is open, in the
 * dimension of the player and loaded (answers 1661 and rule 22).
 */
final class TabletEscortView {
  private TabletEscortView() {}

  static EscortMarksPayload of(ServerPlayer player) {
    if (!escorting(player)) {
      return EscortMarksPayload.NONE;
    }
    return new EscortMarksPayload(
        player
            .serverLevel()
            .getEntities(
                CubitEntities.GENERAL_ROBOT.get(),
                robot -> Ownership.owns(robot.ownerId(), player.getUUID()))
            .stream()
            .map(RobotEntity.class::cast)
            .flatMap(robot -> robot.workArea().work().escort().escortedHere().stream())
            .map(LivingEntity::getId)
            .distinct()
            .toList());
  }

  private static boolean escorting(ServerPlayer player) {
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack held = player.getItemInHand(hand);
      if (held.is(CubitItems.TABLET.get()) && TabletItem.appOf(held) == TabletApp.ESCORT) {
        return true;
      }
    }
    return false;
  }
}
