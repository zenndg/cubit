package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The pistol and the blade of the Galactic Patroller in one tier (answer 1577). */
final class PatrollerWeaponKeys {
  private static final String PISTOL_DAMAGE = "patroller_pistol_damage";
  private static final String PISTOL_RANGE = "patroller_pistol_range";
  private static final String PISTOL_INTERVAL = "patroller_pistol_interval_ticks";
  private static final String PISTOL_COST = "patroller_pistol_cost";
  private static final String SLOWNESS_LEVEL = "patroller_slowness_level";
  private static final String BLADE_DAMAGE = "patroller_blade_damage";
  private static final String BLADE_INTERVAL = "patroller_blade_interval_ticks";
  private static final String BLADE_KNOCKBACK = "patroller_blade_knockback";
  private static final String BLADE_REACH = "patroller_blade_reach";
  private static final double MIN_REACH = 1;
  private static final double MAX_REACH = 6;
  private static final int MIN_ZERO = 0;
  private static final int MIN_ONE = 1;
  private static final int MAX_DAMAGE = 100;
  private static final int MAX_BLOCKS = 64;
  private static final int MAX_TICKS = 1200;
  private static final int MAX_LEVEL = 5;

  private final ModConfigSpec.IntValue pistolDamage;
  private final ModConfigSpec.IntValue pistolRange;
  private final ModConfigSpec.IntValue pistolInterval;
  private final ModConfigSpec.IntValue pistolCost;
  private final ModConfigSpec.IntValue slownessLevel;
  private final ModConfigSpec.IntValue bladeDamage;
  private final ModConfigSpec.IntValue bladeInterval;
  private final ModConfigSpec.IntValue bladeKnockback;
  private final ModConfigSpec.DoubleValue bladeReach;

  PatrollerWeaponKeys(ModConfigSpec.Builder builder, PatrollerSpec.Weapons defaults) {
    pistolDamage =
        builder.defineInRange(PISTOL_DAMAGE, defaults.pistolDamage(), MIN_ZERO, MAX_DAMAGE);
    pistolRange = builder.defineInRange(PISTOL_RANGE, defaults.pistolRange(), MIN_ONE, MAX_BLOCKS);
    pistolInterval =
        builder.defineInRange(PISTOL_INTERVAL, defaults.pistolIntervalTicks(), MIN_ONE, MAX_TICKS);
    pistolCost =
        builder.defineInRange(
            PISTOL_COST, (int) defaults.shotCost().amount(), MIN_ZERO, Integer.MAX_VALUE);
    slownessLevel =
        builder.defineInRange(SLOWNESS_LEVEL, defaults.slownessLevel(), MIN_ONE, MAX_LEVEL);
    bladeDamage = builder.defineInRange(BLADE_DAMAGE, defaults.bladeDamage(), MIN_ZERO, MAX_DAMAGE);
    bladeInterval =
        builder.defineInRange(BLADE_INTERVAL, defaults.bladeIntervalTicks(), MIN_ONE, MAX_TICKS);
    bladeKnockback =
        builder.defineInRange(BLADE_KNOCKBACK, defaults.bladeKnockback(), MIN_ZERO, MAX_LEVEL);
    bladeReach = builder.defineInRange(BLADE_REACH, defaults.bladeReach(), MIN_REACH, MAX_REACH);
  }

  PatrollerSpec.Weapons read() {
    return new PatrollerSpec.Weapons(
        pistolDamage.get(),
        pistolRange.get(),
        pistolInterval.get(),
        new Energy(pistolCost.get()),
        slownessLevel.get(),
        bladeDamage.get(),
        bladeInterval.get(),
        bladeKnockback.get(),
        bladeReach.get());
  }
}
