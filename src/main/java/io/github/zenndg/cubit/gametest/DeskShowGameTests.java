package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskLookProperty;
import io.github.zenndg.cubit.block.EngineeringDeskBlock;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * What the Engineering Desk shows around it: the status of the block state and the light of the
 * lamp half (answers 1508, 1513 and 1514).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskShowGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int SOME_TICKS = 20;
  private static final int ONE_CRAFT = 130;
  private static final int CRAFTING_LIGHT = 7;
  private static final int NO_LIGHT = 0;
  private static final long ENERGY = 50_000;
  private static final String BLOCK_KEY = "Name";
  private static final String PART_KEY = "Properties";
  private static final String WRONG_LIGHT = "Wrong light of the desk";
  private static final String NOT_IDLE = "A desk saved without a status is not idle";

  private DeskShowGameTests() {}

  private static EngineeringDeskBlockEntity crafting(GameTestHelper helper, long energy) {
    EngineeringDeskBlockEntity desk = EngineeringDeskGameTests.place(helper, energy);
    EngineeringDeskGameTests.fillToolbox(desk, 1);
    desk.choose(EngineeringDeskGameTests.TOOLBOX);
    desk.adjustOrder(1);
    return desk;
  }

  private static int light(GameTestHelper helper, BlockPos pos) {
    return helper.getBlockState(pos).getLightEmission(helper.getLevel(), helper.absolutePos(pos));
  }

  private static void assertLook(GameTestHelper helper, DeskLookProperty look) {
    BlockPos desk = EngineeringDeskGameTests.DESK;
    helper.assertBlockProperty(desk, EngineeringDeskBlock.LOOK, look);
    helper.assertBlockProperty(
        EngineeringDeskGameTests.rightHalf(helper), EngineeringDeskBlock.LOOK, look);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void craftingLightsOnlyTheLampHalf(GameTestHelper helper) {
    crafting(helper, ENERGY);
    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          assertLook(helper, DeskLookProperty.CRAFTING);
          helper.assertTrue(
              light(helper, EngineeringDeskGameTests.DESK) == CRAFTING_LIGHT, WRONG_LIGHT);
          helper.assertTrue(
              light(helper, EngineeringDeskGameTests.rightHalf(helper)) == NO_LIGHT, WRONG_LIGHT);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aPauseShowsWithoutLight(GameTestHelper helper) {
    crafting(helper, 0);
    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          assertLook(helper, DeskLookProperty.PAUSED);
          helper.assertTrue(light(helper, EngineeringDeskGameTests.DESK) == NO_LIGHT, WRONG_LIGHT);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void theEndOfTheOrderParksTheDesk(GameTestHelper helper) {
    crafting(helper, ENERGY);
    helper.runAfterDelay(
        ONE_CRAFT,
        () -> {
          assertLook(helper, DeskLookProperty.IDLE);
          helper.assertTrue(light(helper, EngineeringDeskGameTests.DESK) == NO_LIGHT, WRONG_LIGHT);
          helper.succeed();
        });
  }

  /** A world saved before the status reads the desk as parked (rule 18). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aDeskSavedWithoutAStatusIsIdle(GameTestHelper helper) {
    BlockState saved =
        CubitBlocks.ENGINEERING_DESK
            .get()
            .defaultBlockState()
            .setValue(EngineeringDeskBlock.LOOK, DeskLookProperty.CRAFTING);
    CompoundTag tag = NbtUtils.writeBlockState(saved);
    tag.getCompound(PART_KEY).remove(EngineeringDeskBlock.LOOK.getName());
    BlockState loaded =
        NbtUtils.readBlockState(helper.getLevel().holderLookup(Registries.BLOCK), tag);
    helper.assertTrue(
        loaded.getValue(EngineeringDeskBlock.LOOK) == DeskLookProperty.IDLE
            && tag.contains(BLOCK_KEY),
        NOT_IDLE);
    helper.succeed();
  }
}
