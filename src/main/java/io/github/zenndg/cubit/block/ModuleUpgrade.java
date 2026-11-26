package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.domain.tier.TierUpgrade;
import io.github.zenndg.cubit.item.ModuleItem;
import io.github.zenndg.cubit.item.TierLook;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * What the owner does to a block of the mod with a Module: the Module of the next tier raises it
 * and is used up, as in the Cubit, and one that does not fit stays in the hand with a reason (1377
 * and 1378).
 */
public final class ModuleUpgrade {
  private ModuleUpgrade() {}

  static void apply(
      RobotTier current, Consumer<RobotTier> raise, Texts texts, Player player, ItemStack stack) {
    ModuleItem module = (ModuleItem) stack.getItem();
    TierUpgrade upgrade = TierUpgrade.of(current, module.tier());
    if (upgrade.consumesModule()) {
      if (player.containerMenu != player.inventoryMenu) {
        player.closeContainer();
      }
      raise.accept(module.tier());
      stack.consume(1, player);
    }
    player.displayClientMessage(answer(texts, upgrade, current, module.tier()), true);
  }

  private static Component answer(
      Texts texts, TierUpgrade upgrade, RobotTier current, RobotTier target) {
    return switch (upgrade) {
      case UPGRADES -> Component.translatable(texts.upgraded(), TierLook.name(target));
      case NEEDS_PREVIOUS_TIER ->
          Component.translatable(texts.needsTier(), TierLook.name(target.previous().orElseThrow()));
      case ALREADY_THERE -> Component.translatable(texts.alreadyTier(), TierLook.name(current));
    };
  }

  /** The notices of one block: raised, missing the tier before, and already there. */
  record Texts(String upgraded, String needsTier, String alreadyTier) {}
}
