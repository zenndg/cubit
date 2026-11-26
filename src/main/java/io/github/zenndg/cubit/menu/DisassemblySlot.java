package io.github.zenndg.cubit.menu;

import java.util.function.BooleanSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The slot of the Disassemble tab: only on that tab, only for an item that comes apart, and never
 * while it runs or a plan runs (answers 2527 and 2535, details 2 and 3).
 */
final class DisassemblySlot extends Slot {
  private final DisassemblyMenuPart disassembly;
  private final CascadeMenuPart cascade;
  private final Level level;
  private final BooleanSupplier shown;

  DisassemblySlot(
      DisassemblyMenuPart disassembly,
      CascadeMenuPart cascade,
      Level level,
      BooleanSupplier shown) {
    super(
        disassembly.slot(),
        0,
        EngineeringDeskMenu.DISASSEMBLY_X,
        EngineeringDeskMenu.DISASSEMBLY_Y);
    this.disassembly = disassembly;
    this.cascade = cascade;
    this.level = level;
    this.shown = shown;
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return !cascade.locks() && disassembly.accepts(level, stack);
  }

  @Override
  public boolean mayPickup(Player taker) {
    return !disassembly.running() && super.mayPickup(taker);
  }

  @Override
  public boolean isActive() {
    return shown.getAsBoolean();
  }
}
