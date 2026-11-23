package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.InductionDockTopBlock;
import io.github.zenndg.cubit.domain.dock.DockPanelState;
import io.github.zenndg.cubit.domain.dock.DockSunState;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The third block of the Dock, where the panel goes: placed with the dock, gone with it, taken by a
 * dock from before it when the space is empty, and solid only with the panel deployed (answers 1892
 * and 1896 to 1898).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockTopGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int BLOCKS_TO_THE_TOP = 2;
  private static final BlockPos TOP = DockScene.DOCK.above(BLOCKS_TO_THE_TOP);

  /** The 3 s of sun, the 6 s of the arms and a reading of the sky, with room to spare. */
  private static final int DEPLOYED_TICKS = 360;

  /** More than one reading of the sky, which takes the third block back. */
  private static final int A_READING_TICKS = 120;

  private static final String NOT_STORED = "Without room, the panel should stay in the cabinet";
  private static final String NOT_NO_ROOM = "The Solar tab should tell there is no room";
  private static final String NO_PLATE = "The deployed panel should be solid";

  private DockTopGameTests() {}

  @GameTest(template = ARENA)
  public static void placingTheDockSetsItsThirdBlock(GameTestHelper helper) {
    place(helper);

    helper.assertBlockPresent(CubitBlocks.INDUCTION_DOCK_TOP.get(), TOP);
    helper.assertBlockProperty(TOP, InductionDockTopBlock.DEPLOYED, false);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theDockNeedsThreeFreeBlocks(GameTestHelper helper) {
    helper.setBlock(TOP, Blocks.STONE);

    GameTestRobots.use(
        helper,
        DockScene.ownerBeside(helper),
        new ItemStack(CubitItems.INDUCTION_DOCK.get()),
        DockScene.FLOOR);

    helper.assertBlockNotPresent(CubitBlocks.INDUCTION_DOCK.get(), DockScene.DOCK);
    helper.assertBlockPresent(Blocks.STONE, TOP);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theThirdBlockGoesWithTheDock(GameTestHelper helper) {
    place(helper);

    helper.destroyBlock(DockScene.DOCK);

    helper.assertBlockNotPresent(CubitBlocks.INDUCTION_DOCK_TOP.get(), TOP);
    helper.succeed();
  }

  /** A dock from before the third block takes it when the space is empty (answer 1897). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anOldDockTakesItsThirdBlock(GameTestHelper helper) {
    place(helper);
    helper.setBlock(TOP, Blocks.AIR);

    helper.runAfterDelay(
        A_READING_TICKS,
        () -> {
          helper.assertBlockPresent(CubitBlocks.INDUCTION_DOCK_TOP.get(), TOP);
          helper.succeed();
        });
  }

  /** With the space taken, the dock works without its panel and clears nothing (1897). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void anOldDockWithoutRoomKeepsThePanelIn(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);
    helper.setBlock(TOP, Blocks.STONE);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.STORED, NOT_STORED);
          helper.assertValueEqual(dock.solar().state(), DockSunState.NO_ROOM, NOT_NO_ROOM);
          helper.assertBlockPresent(Blocks.STONE, TOP);
          helper.succeed();
        });
  }

  /** Only the deployed panel stands as a thin plate in the third block (answer 1898). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = SolarGameTests.DAYLIGHT_BATCH)
  public static void theDeployedPanelIsSolid(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper);

    helper.runAfterDelay(
        DEPLOYED_TICKS,
        () -> {
          helper.assertValueEqual(dock.solar().panel(), DockPanelState.DEPLOYED, NO_PLATE);
          helper.assertBlockProperty(TOP, InductionDockTopBlock.DEPLOYED, true);
          helper.succeed();
        });
  }

  private static InductionDockBlockEntity place(GameTestHelper helper) {
    GameTestRobots.use(
        helper,
        DockScene.ownerBeside(helper),
        new ItemStack(CubitItems.INDUCTION_DOCK.get()),
        DockScene.FLOOR);
    return helper.getBlockEntity(DockScene.DOCK);
  }
}
