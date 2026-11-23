package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The solar panel of every Dock: it leaves the cabinet after the sky holds, only a panel fully on
 * the tower generates, and a roof keeps it in (answers 1852 to 1866).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockPanelGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;

  /** The 3 s of sun in a row of the config, plus some ticks for the first reading of the sky. */
  private static final int DELAY_TICKS = 70;

  /** The delay and the 6 s of the arms, with room to spare. */
  private static final int DEPLOYED_TICKS = 360;

  private static final long BASE_SOLAR = 25;
  private static final String NOT_DEPLOYED = "The panel should be on the tower";
  private static final String NOT_STORED = "The panel should stay in the cabinet";
  private static final String NOT_DEPLOYING = "The arms should be taking the panel out";
  private static final String NOT_GENERATED = "The panel should generate under the sun";
  private static final String GENERATED = "The panel should not generate";

  private DockPanelGameTests() {}

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void anyDockDeploysThePanelAndGeneratesUnderTheSun(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYED, NOT_DEPLOYED);
          helper.assertValueEqual(dock.solar().given().amount(), BASE_SOLAR, NOT_GENERATED);
          helper.assertTrue(dock.energy().amount() > 0, NOT_GENERATED);
          helper.succeed();
        });
  }

  /** Only a panel fully on the tower generates, so the arms on their way give nothing (1856). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void aPanelOnItsWayDoesNotGenerate(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);

    helper.runAfterDelay(
        DELAY_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYING, NOT_DEPLOYING);
          helper.assertValueEqual(dock.energy(), Energy.ZERO, GENERATED);
          helper.succeed();
        });
  }

  /** An opaque block above the Dock keeps the panel in the cabinet (answer 1853). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void aRoofKeepsThePanelIn(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);
    helper.setBlock(DockScene.DOCK.above(2), Blocks.STONE);

    assertStoredAfterAWhile(helper, dock);
  }

  /** Light that passes through glass still counts, as for the panel of the Cubit (1853). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void glassAboveLetsThePanelOut(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);
    helper.setBlock(DockScene.DOCK.above(2), Blocks.GLASS);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYED, NOT_DEPLOYED);
          helper.assertTrue(dock.energy().amount() > 0, NOT_GENERATED);
          helper.succeed();
        });
  }

  /** The default batch starts at midnight, when the panel stays in the cabinet (1862). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void atNightThePanelStaysIn(GameTestHelper helper) {
    assertStoredAfterAWhile(helper, place(helper));
  }

  private static InductionDockBlockEntity place(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    GameTestRobots.use(
        helper, owner, new ItemStack(CubitItems.INDUCTION_DOCK.get()), DockScene.FLOOR);
    return helper.getBlockEntity(DockScene.DOCK);
  }

  private static void assertStoredAfterAWhile(
      GameTestHelper helper, InductionDockBlockEntity dock) {
    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.STORED, NOT_STORED);
          helper.assertValueEqual(dock.energy(), Energy.ZERO, GENERATED);
          helper.succeed();
        });
  }
}
