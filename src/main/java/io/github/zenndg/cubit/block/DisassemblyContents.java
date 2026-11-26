package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.item.EnergySlotContents;
import io.github.zenndg.cubit.item.SavedDockXp;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Whether an item keeps items, XP, a card or torches, which a desk never takes apart, so nothing is
 * lost without the player knowing (answer 2524): the inventory and the slots of a Cubit, the
 * storage of a dock and of a Network Server, and the XP of a Cubit and of a dock. The energy, the
 * name and the color are lost, with the warning on the screen.
 */
final class DisassemblyContents {
  private static final List<Supplier<DataComponentType<ItemContainerContents>>> ITEMS =
      List.of(
          CubitDataComponents.ROBOT_INVENTORY,
          CubitDataComponents.TORCH_SLOT,
          CubitDataComponents.CARD_SLOT,
          CubitDataComponents.FERTILIZER_SLOT,
          CubitDataComponents.DOCK_STORAGE);

  private DisassemblyContents() {}

  static boolean has(ItemStack stack) {
    return holdsItems(stack) || holdsXp(stack);
  }

  private static boolean holdsItems(ItemStack stack) {
    for (Supplier<DataComponentType<ItemContainerContents>> type : ITEMS) {
      if (DeskSaves.hasItems(stack.get(type.get()))) {
        return true;
      }
    }
    EnergySlotContents energySlot = stack.get(CubitDataComponents.ENERGY_SLOT.get());
    SavedNetwork network = stack.get(CubitDataComponents.NETWORK_STATE.get());
    return (energySlot != null && DeskSaves.hasItems(energySlot.item()))
        || (network != null && !network.items().isEmpty());
  }

  private static boolean holdsXp(ItemStack stack) {
    RobotState robot = stack.get(CubitDataComponents.ROBOT_STATE.get());
    SavedDockXp dock = stack.get(CubitDataComponents.DOCK_XP.get());
    return (robot != null && robot.xp().fluid() > 0) || (dock != null && dock.fluid() > 0);
  }
}
