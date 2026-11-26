package io.github.zenndg.cubit.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** A dock goes up with a Module, as the Cubit does (answers 1377 and 1378). */
public final class DockUpgrade {
  public static final String UPGRADED_KEY = "notice.cubit.dock_upgraded";
  public static final String NEEDS_TIER_KEY = "notice.cubit.dock_needs_tier";
  public static final String ALREADY_TIER_KEY = "notice.cubit.dock_already_tier";
  private static final ModuleUpgrade.Texts TEXTS =
      new ModuleUpgrade.Texts(UPGRADED_KEY, NEEDS_TIER_KEY, ALREADY_TIER_KEY);

  private DockUpgrade() {}

  static void apply(InductionDockBlockEntity dock, Player player, ItemStack stack) {
    ModuleUpgrade.apply(dock.tier(), dock::upgradeTo, TEXTS, player, stack);
  }
}
