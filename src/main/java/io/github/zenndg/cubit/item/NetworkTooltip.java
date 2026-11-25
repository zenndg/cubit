package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * The tooltip of a collected block of the Network, with lines only for what it keeps: the server
 * has its tier, its energy and its items, and the relay only its energy (answer 2416).
 */
final class NetworkTooltip {
  private NetworkTooltip() {}

  static void appendServer(ItemStack stack, List<Component> lines) {
    if (!stack.has(CubitDataComponents.NETWORK_STATE.get())) {
      return;
    }
    SavedNetwork saved = SavedNetwork.of(stack);
    lines.add(Component.translatable(EngineeringDeskItem.TIER_KEY, TierLook.name(saved.tier())));
    appendEnergy(saved, lines);
    int items = saved.items().stream().mapToInt(SavedBigStack::amount).sum();
    if (items > 0) {
      lines.add(
          Component.translatable(EngineeringDeskItem.ITEMS_KEY, items)
              .withStyle(ChatFormatting.GRAY));
    }
  }

  static void appendRelay(ItemStack stack, List<Component> lines) {
    appendEnergy(SavedNetwork.of(stack), lines);
  }

  private static void appendEnergy(SavedNetwork saved, List<Component> lines) {
    if (saved.energy() > 0) {
      lines.add(
          Component.translatable(
                  EngineeringDeskItem.ENERGY_KEY, EnergyFormat.compact(new Energy(saved.energy())))
              .withStyle(ChatFormatting.GRAY));
    }
  }
}
