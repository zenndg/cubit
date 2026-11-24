package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.CubitConfig;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** The line of the XP in the item of the Cubit and in the Damaged Cubit (answer 1849). */
final class XpTooltip {
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
    lines.add(
        Component.translatable(XpKeys.TOOLTIP, grouped(state.xp().fluid()), grouped(capacity))
            .withStyle(ChatFormatting.GRAY));
  }

  private static String grouped(int number) {
    return String.format(Locale.ROOT, GROUPED, number);
  }
}
