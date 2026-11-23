package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.gametest.framework.AfterBatch;
import net.minecraft.gametest.framework.BeforeBatch;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The moonlight on the panel of the Dock: only a tier with a rate for the night stays at work, and
 * the rain stops it as by day (answers 1943 to 1949). The default batch starts at midnight.
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockMoonGameTests {
  private static final String ARENA = "arena";
  private static final String RAIN_BATCH = "moon_rain";
  private static final String DUSK_BATCH = "moon_dusk";
  private static final int TIMEOUT = 600;
  private static final long NOON = 6000;
  private static final long MIDNIGHT = 18000;
  private static final int WEATHER_TICKS = 12000;
  private static final float FULL_RAIN = 1f;
  private static final float NO_RAIN = 0f;

  /** The 3 s of the delay and the 6 s of the arms, with room to spare. */
  private static final int DEPLOYED_TICKS = 360;

  /** More than the 3 s of the delay, so the arms would have left the tower. */
  private static final int AFTER_DUSK_TICKS = 100;

  private static final long ULTIMATE_NIGHT = 350;
  private static final String NOT_DEPLOYED = "The panel should be on the tower";
  private static final String NOT_STORED = "The panel should stay in the cabinet";
  private static final String NOT_MOONLIGHT = "The panel should be under the moon";
  private static final String WRONG_REASON = "The panel should stop for the right reason";
  private static final String NOT_GENERATED = "The panel should generate under the moon";
  private static final String GENERATED = "The panel should not generate";
  private static final String WRONG_MOST = "The most should be the one of the night";

  private DockMoonGameTests() {}

  @BeforeBatch(batch = RAIN_BATCH)
  public static void startInTheRainAtMidnight(ServerLevel level) {
    level.setDayTime(MIDNIGHT);
    level.setWeatherParameters(0, WEATHER_TICKS, true, false);
    level.setRainLevel(FULL_RAIN);
  }

  @AfterBatch(batch = RAIN_BATCH)
  public static void clearTheRain(ServerLevel level) {
    level.setWeatherParameters(WEATHER_TICKS, 0, false, false);
    level.setRainLevel(NO_RAIN);
  }

  @BeforeBatch(batch = DUSK_BATCH)
  public static void startAtNoon(ServerLevel level) {
    level.setDayTime(NOON);
  }

  @AfterBatch(batch = DUSK_BATCH)
  public static void endAtMidnight(ServerLevel level) {
    level.setDayTime(MIDNIGHT);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anUltimateDockGeneratesAtNight(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper, RobotTier.ULTIMATE);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYED, NOT_DEPLOYED);
          assertMoonlight(helper, dock);
          helper.assertValueEqual(dock.solar().max().amount(), ULTIMATE_NIGHT, WRONG_MOST);
          helper.assertTrue(dock.energy().amount() > 0, NOT_GENERATED);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anEliteDockKeepsThePanelInAtNight(GameTestHelper helper) {
    assertStoredAfterAWhile(helper, place(helper, RobotTier.ELITE), DockSunState.NIGHT);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = RAIN_BATCH)
  public static void theRainKeepsThePanelInAtNight(GameTestHelper helper) {
    assertStoredAfterAWhile(helper, place(helper, RobotTier.ULTIMATE), DockSunState.RAIN);
  }

  /** The panel stays on the tower at dusk, without closing and opening again (answer 1943). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = DUSK_BATCH)
  public static void theDuskLeavesThePanelOnTheTower(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper, RobotTier.ULTIMATE);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYED, NOT_DEPLOYED);
          helper.getLevel().setDayTime(MIDNIGHT);
          helper.runAfterDelay(
              AFTER_DUSK_TICKS,
              () -> {
                helper.assertValueEqual(
                    dock.solar().panel(), DockPanelState.DEPLOYED, NOT_DEPLOYED);
                assertMoonlight(helper, dock);
                helper.succeed();
              });
        });
  }

  private static InductionDockBlockEntity place(GameTestHelper helper, RobotTier tier) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    ItemStack item = new ItemStack(CubitItems.INDUCTION_DOCK.get());
    item.set(CubitDataComponents.DOCK_TIER.get(), tier);
    GameTestRobots.use(helper, owner, item, DockScene.FLOOR);
    return helper.getBlockEntity(DockScene.DOCK);
  }

  private static void assertMoonlight(GameTestHelper helper, InductionDockBlockEntity dock) {
    helper.assertValueEqual(dock.solar().state(), DockSunState.MOONLIGHT, NOT_MOONLIGHT);
    helper.assertValueEqual(dock.solar().given().amount(), ULTIMATE_NIGHT, NOT_GENERATED);
  }

  private static void assertStoredAfterAWhile(
      GameTestHelper helper, InductionDockBlockEntity dock, DockSunState reason) {
    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.STORED, NOT_STORED);
          helper.assertValueEqual(dock.solar().state(), reason, WRONG_REASON);
          helper.assertValueEqual(dock.energy(), Energy.ZERO, GENERATED);
          helper.succeed();
        });
  }
}
