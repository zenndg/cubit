package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.CubitConfig;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

/** The line of the XP in the items of the Cubit and the Dock, in Jade and on the Tablet (1849). */
public final class XpTooltip {
  private static final String GROUPED = "%,d";

  private XpTooltip() {}

  static void add(ItemStack stack, List<Component> lines) {
    RobotState state = stack.get(CubitDataComponents.ROBOT_STATE.get());
    if (state == null) {
      return;
    }
    int capacity =
        CubitMod.CONFIG
            .tierCatalog()
            .spec(CubitConfig.GENERAL_ROBOT, state.tier())
            .xp()
            .tankCapacity();
    lines.add(line(new XpTank(state.xp().fluid(), capacity)).withStyle(ChatFormatting.GRAY));
  }

  /** The capacity is the one of the tier of the Dock (answer 1849). */
  static void addDock(ItemStack stack, List<Component> lines) {
    SavedDockXp saved = stack.get(CubitDataComponents.DOCK_XP.get());
    if (saved == null) {
      return;
    }
    int capacity =
        CubitMod.CONFIG.dockCatalog().dock(TierLook.dockTierOf(stack)).xp().tankCapacity();
    lines.add(line(new XpTank(saved.fluid(), capacity)).withStyle(ChatFormatting.GRAY));
  }

  public static MutableComponent line(XpTank tank) {
    return Component.translatable(XpKeys.TOOLTIP, grouped(tank.fluid()), grouped(tank.capacity()));
  }

  private static String grouped(int number) {
    return String.format(Locale.ROOT, GROUPED, number);
  }
}
