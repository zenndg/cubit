package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** The tier in the color of the collar of each tier, for the Cubit and the desk. */
final class JadeTierLine {
  private JadeTierLine() {}

  static Component of(RobotTier tier) {
    ChatFormatting color =
        switch (tier) {
          case BASE -> ChatFormatting.GRAY;
          case ADVANCED -> ChatFormatting.BLUE;
          case ELITE -> ChatFormatting.LIGHT_PURPLE;
          case ULTIMATE -> ChatFormatting.GOLD;
        };
    Component name = Component.translatable(JadeKeys.tierName(tier)).withStyle(color);
    return Component.translatable(JadeKeys.TIER, name);
  }
}
