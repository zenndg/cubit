package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.tier.PatrollerSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The numbers of the card of the Galactic Patroller in one tier, beside its weapons. The names are
 * a contract with the pack (answers 1564, 1577 and 1579).
 */
final class PatrollerKeys {
  private static final String ARMOR = "patroller_armor";
  private static final String SEARCH = "patroller_search_radius";
  private static final String JUMP_HEIGHT = "patroller_jump_height";
  private static final String JUMP_COST = "patroller_jump_cost";
  private static final String ROUTE_POINTS = "patroller_route_points";
  private static final String AREA_SIZE = "patroller_area_size";
  private static final String RETURN_CHARGE = "patroller_return_charge_percent";
  private static final String GAP = "patroller_gap_distance";
  private static final int MAX_GAP = 16;
  private static final int MAX_AREA = 256;
  private static final int MIN_ZERO = 0;
  private static final int MIN_ONE = 1;
  private static final int MAX_ARMOR = 30;
  private static final int MAX_BLOCKS = 64;
  private static final int MAX_POINTS = 128;

  private final PatrollerWeaponKeys weapons;
  private final ModConfigSpec.IntValue armor;
  private final ModConfigSpec.IntValue search;
  private final ModConfigSpec.IntValue jumpHeight;
  private final ModConfigSpec.IntValue jumpCost;
  private final ModConfigSpec.IntValue routePoints;
  private final ModConfigSpec.IntValue areaSize;
  private final ModConfigSpec.IntValue returnCharge;
  private final ModConfigSpec.IntValue gap;

  PatrollerKeys(ModConfigSpec.Builder builder, PatrollerSpec defaults) {
    weapons = new PatrollerWeaponKeys(builder, defaults.weapons());
    armor = builder.defineInRange(ARMOR, defaults.armor(), MIN_ZERO, MAX_ARMOR);
    search = builder.defineInRange(SEARCH, defaults.searchRadius(), MIN_ONE, MAX_BLOCKS);
    jumpHeight = builder.defineInRange(JUMP_HEIGHT, defaults.jump().height(), MIN_ZERO, MAX_BLOCKS);
    jumpCost =
        builder.defineInRange(
            JUMP_COST, (int) defaults.jump().cost().amount(), MIN_ZERO, Integer.MAX_VALUE);
    routePoints = builder.defineInRange(ROUTE_POINTS, defaults.routePoints(), MIN_ZERO, MAX_POINTS);
    areaSize = builder.defineInRange(AREA_SIZE, defaults.areaSize(), MIN_ZERO, MAX_AREA);
    returnCharge =
        builder.defineInRange(
            RETURN_CHARGE, defaults.returnCharge().value(), MIN_ZERO, Percentage.MAX);
    gap = builder.defineInRange(GAP, defaults.jump().gap(), MIN_ZERO, MAX_GAP);
  }

  PatrollerSpec read() {
    return new PatrollerSpec(
        weapons.read(),
        armor.get(),
        search.get(),
        new PatrollerSpec.Jump(jumpHeight.get(), new Energy(jumpCost.get()), gap.get()),
        routePoints.get(),
        areaSize.get(),
        new Percentage(returnCharge.get()));
  }
}
