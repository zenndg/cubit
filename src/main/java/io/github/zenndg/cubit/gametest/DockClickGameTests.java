package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The clicks on the Dock: the right click opens the screen, and the sneaking one, only with both
 * hands empty, calls a Cubit or lets the docked one go (answers 2122 to 2124).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockClickGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 200;
  private static final int SETTLE_TICKS = 5;
  private static final Energy STOCK = new Energy(50_000);
  private static final Energy PLENTY = new Energy(100_000);
  private static final BlockPos ROBOT_FLOOR = DockScene.FLOOR.west(4);
  private static final String NOT_OPENED = "The right click should open the screen";
  private static final String OPENED = "The screen should stay closed";
  private static final String CALLED = "No Cubit should be called";
  private static final String NOT_CALLED = "The sneaking click should call the Cubit";
  private static final String RELEASED = "The right click should not let the Cubit go";
  private static final String NOT_RELEASED = "The sneaking click should let the Cubit go";

  private DockClickGameTests() {}

  private static boolean opened(ServerPlayer player) {
    return player.containerMenu instanceof InductionDockMenu;
  }

  @GameTest(template = ARENA)
  public static void theRightClickOpensTheScreenAndCallsNobody(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    DockScene.click(helper, owner, DockScene.DOCK);

    helper.assertTrue(opened(owner), NOT_OPENED);
    helper.assertFalse(robot.docking().hasTrip(), CALLED);
    owner.closeContainer();
    DockScene.click(helper, owner, DockScene.DOCK.above());
    helper.assertTrue(opened(owner), NOT_OPENED);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theSneakingClickCallsAndOpensNothing(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);

    DockScene.sneakClick(helper, owner, DockScene.DOCK);

    helper.assertFalse(opened(owner), OPENED);
    helper.assertTrue(robot.docking().hasTrip(), NOT_CALLED);
    helper.succeed();
  }

  /** Also with a Cubit in the Dock, the right click only opens the screen (answer 2123). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void onlyTheSneakingClickLetsTheDockedCubitGo(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeOnDock(helper, owner, PLENTY);

    helper.runAfterDelay(
        SETTLE_TICKS,
        () -> {
          DockScene.click(helper, owner, DockScene.DOCK);
          helper.assertTrue(opened(owner), NOT_OPENED);
          helper.assertValueEqual(robot.powerState(), PowerState.HIBERNATING, RELEASED);
          owner.closeContainer();
          DockScene.sneakClick(helper, owner, DockScene.DOCK);
          helper.assertFalse(opened(owner), OPENED);
          helper.assertValueEqual(robot.powerState(), PowerState.ACTIVE, NOT_RELEASED);
          helper.succeed();
        });
  }

  @GameTest(template = ARENA)
  public static void aStrangerOpensNothing(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    ServerPlayer stranger = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);

    DockScene.click(helper, stranger, DockScene.DOCK);
    DockScene.sneakClick(helper, stranger, DockScene.DOCK);

    helper.assertFalse(opened(stranger), OPENED);
    helper.succeed();
  }

  /** Through the game, which keeps the sneaking click of a full hand for the item (2124). */
  @GameTest(template = ARENA)
  public static void theSneakingClickNeedsBothHandsEmpty(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    RobotEntity robot = DockScene.placeRobot(helper, owner, ROBOT_FLOOR, PLENTY);
    owner.setShiftKeyDown(true);

    for (InteractionHand full : InteractionHand.values()) {
      owner.setItemInHand(full, new ItemStack(Items.STICK));
      use(helper, owner);
      helper.assertFalse(robot.docking().hasTrip() || opened(owner), CALLED);
      owner.setItemInHand(full, ItemStack.EMPTY);
    }
    use(helper, owner);
    helper.assertTrue(robot.docking().hasTrip(), NOT_CALLED);
    helper.succeed();
  }

  /** With an item the Dock does not keep, the right click opens the screen, as a chest. */
  @GameTest(template = ARENA)
  public static void theRightClickWithAnotherItemOpensTheScreen(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    DockScene.placeDock(helper, owner, DockScene.FLOOR, STOCK);
    owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));

    use(helper, owner);

    helper.assertTrue(opened(owner), NOT_OPENED);
    helper.succeed();
  }

  private static void use(GameTestHelper helper, ServerPlayer player) {
    BlockPos dock = helper.absolutePos(DockScene.DOCK);
    player.gameMode.useItemOn(
        player,
        helper.getLevel(),
        player.getMainHandItem(),
        InteractionHand.MAIN_HAND,
        new BlockHitResult(Vec3.atCenterOf(dock), Direction.UP, dock, false));
  }
}
