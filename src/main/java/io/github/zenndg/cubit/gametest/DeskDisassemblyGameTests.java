package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DisassemblyRecipes;
import io.github.zenndg.cubit.block.DisassemblyWork;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Taking items apart on the Engineering Desk: a unit at once, the refusal of an item with contents,
 * the Modules of a tier, the pause with a full output, the tier and one work at a time (answers
 * 2523 to 2535).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskDisassemblyGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 400;
  private static final int ONE_UNIT = 80;
  private static final int SOME_TICKS = 20;
  private static final int FULL_STACK = 64;
  private static final long ENERGY = 50_000;
  private static final long COST_OF_ONE = 3_500;
  private static final String WRONG_REFUSAL = "Wrong reason of the desk";
  private static final String WRONG_OUTPUT = "Wrong items in the output";
  private static final String WRONG_SLOT = "Wrong item left in the slot";
  private static final String WRONG_ENERGY = "Wrong energy after taking apart";
  private static final String WRONG_STATUS = "Wrong status of the desk";
  private static final String NO_MODULES = "The Modules of the tier did not come back";
  private static final String BOTH_RUN = "An order started during a disassembly";

  private DeskDisassemblyGameTests() {}

  private static EngineeringDeskBlockEntity place(
      GameTestHelper helper, RobotTier tier, long energy) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack item = new ItemStack(CubitItems.ENGINEERING_DESK.get());
    item.set(CubitDataComponents.DESK_TIER.get(), tier);
    item.set(CubitDataComponents.DESK_ENERGY.get(), energy);
    BlockPos floor = EngineeringDeskGameTests.DESK.below();
    GameTestRobots.use(helper, owner, item, floor);
    return helper.getBlockEntity(EngineeringDeskGameTests.DESK);
  }

  private static void put(EngineeringDeskBlockEntity desk, ItemStack stack) {
    desk.disassembly().slot().setItem(0, stack);
  }

  private static DisassemblyRefusal start(GameTestHelper helper, EngineeringDeskBlockEntity desk) {
    return DisassemblyWork.start(desk, helper.getLevel());
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aTabletGivesBackItsPanelAndPaysOneCraft(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ELITE, ENERGY);
    put(desk, new ItemStack(CubitItems.TABLET.get()));
    helper.assertTrue(start(helper, desk) == DisassemblyRefusal.NONE, WRONG_REFUSAL);
    helper.runAfterDelay(
        ONE_UNIT,
        () -> {
          Item panel = CubitItems.DISPLAY_PANEL.get();
          helper.assertTrue(desk.output().countItem(panel) == 1, WRONG_OUTPUT);
          helper.assertTrue(desk.disassembly().item().isEmpty(), WRONG_SLOT);
          helper.assertTrue(desk.energy().amount() == ENERGY - COST_OF_ONE, WRONG_ENERGY);
          helper.assertTrue(!desk.disassembly().isRunning(), WRONG_STATUS);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aCubitWithItemsIsRefusedAndKept(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ELITE, ENERGY);
    ItemStack robot = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    robot.set(
        CubitDataComponents.ROBOT_INVENTORY.get(),
        ItemContainerContents.fromItems(List.of(new ItemStack(Items.DIRT))));
    put(desk, robot);
    helper.assertTrue(start(helper, desk) == DisassemblyRefusal.HAS_CONTENTS, WRONG_REFUSAL);
    helper.assertTrue(!desk.disassembly().isRunning(), WRONG_STATUS);
    helper.assertTrue(desk.disassembly().item().is(robot.getItem()), WRONG_SLOT);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void anEliteDockGivesBackItsModules(GameTestHelper helper) {
    ItemStack dock = new ItemStack(CubitItems.INDUCTION_DOCK.get());
    dock.set(CubitDataComponents.DOCK_TIER.get(), RobotTier.ELITE);
    List<ItemStack> parts = DisassemblyRecipes.plan(helper.getLevel(), dock).orElseThrow().parts();
    helper.assertTrue(
        parts.stream().anyMatch(part -> part.is(CubitItems.ADVANCED_MODULE.get()))
            && parts.stream().anyMatch(part -> part.is(CubitItems.ELITE_MODULE.get()))
            && parts.stream().noneMatch(part -> part.is(CubitItems.ULTIMATE_MODULE.get())),
        NO_MODULES);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFullOutputPausesAndKeepsTheItem(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ELITE, ENERGY);
    for (int slot = 0; slot < desk.slots(); slot++) {
      desk.output().setItem(slot, new ItemStack(Items.DIRT, FULL_STACK));
    }
    put(desk, new ItemStack(CubitItems.TABLET.get()));
    start(helper, desk);
    helper.runAfterDelay(
        SOME_TICKS,
        () -> {
          helper.assertTrue(desk.status() == DeskStatus.OUTPUT_FULL, WRONG_STATUS);
          helper.assertTrue(!desk.disassembly().item().isEmpty(), WRONG_SLOT);
          helper.assertTrue(desk.energy().amount() == ENERGY, WRONG_ENERGY);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aBaseDeskDoesNotTakeApart(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.BASE, ENERGY);
    put(desk, new ItemStack(CubitItems.TABLET.get()));
    helper.assertTrue(start(helper, desk) == DisassemblyRefusal.LOCKED_TIER, WRONG_REFUSAL);
    helper.succeed();
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aDisassemblyHoldsTheOrder(GameTestHelper helper) {
    EngineeringDeskBlockEntity desk = place(helper, RobotTier.ELITE, ENERGY);
    put(desk, new ItemStack(CubitItems.TABLET.get()));
    start(helper, desk);
    desk.choose(EngineeringDeskGameTests.TOOLBOX);
    desk.adjustOrder(1);
    helper.assertTrue(!desk.order().isActive(), BOTH_RUN);
    helper.succeed();
  }
}
