package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The name of a Dock: the standard one, the Rename, the name tag and the item (1867 to 1870). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockNameGameTests {
  private static final String ARENA = "arena";
  private static final String PREFIX = "Dock ";
  private static final int ID_DIGITS = 8;
  private static final String GIVEN = "Garden Dock";
  private static final String TAGGED = "Tagged Dock";
  private static final String EMPTY = "  ";
  private static final String TOO_LONG = "x".repeat(RobotName.MAX_LENGTH + 1);
  private static final int MENU = 1;
  private static final int AWAY = 4;
  private static final String WRONG_STANDARD = "The standard name is Dock and eight digits";
  private static final String NOT_RENAMED = "The owner should rename the Dock";
  private static final String NOT_STANDARD = "An empty name goes back to the standard one";
  private static final String RENAMED = "A name too long changes nothing";
  private static final String NOT_KEPT = "The item should keep the id and the name";
  private static final String NOT_USED = "The name tag should be used up";

  private DockNameGameTests() {}

  @GameTest(template = ARENA)
  public static void aNewDockHasTheStandardName(GameTestHelper helper) {
    InductionDockBlockEntity dock = place(helper, DockScene.ownerBeside(helper), DockScene.FLOOR);
    String expected = PREFIX + dock.identity().id().toString().substring(0, ID_DIGITS);
    helper.assertValueEqual(dock.identity().name().value(), expected, WRONG_STANDARD);
    helper.succeed();
  }

  /** The Rename of the screen, with the rules of the Cubit (answer 1868). */
  @GameTest(template = ARENA)
  public static void theOwnerRenamesOnTheScreen(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = place(helper, owner, DockScene.FLOOR);
    String standard = dock.identity().name().value();
    InductionDockMenu menu = InductionDockMenu.onServer(MENU, owner.getInventory(), dock);

    helper.assertTrue(menu.rename(owner, GIVEN), NOT_RENAMED);
    helper.assertValueEqual(dock.identity().name().value(), GIVEN, NOT_RENAMED);
    helper.assertFalse(menu.rename(owner, TOO_LONG), RENAMED);
    helper.assertValueEqual(dock.identity().name().value(), GIVEN, RENAMED);
    menu.rename(owner, EMPTY);
    helper.assertValueEqual(dock.identity().name().value(), standard, NOT_STANDARD);
    helper.succeed();
  }

  /** The item keeps the id and the name, so the Dock comes back the same (answer 1869). */
  @GameTest(template = ARENA)
  public static void theItemKeepsTheIdAndTheName(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = place(helper, owner, DockScene.FLOOR);
    dock.identity().rename(GIVEN);
    ItemStack item = new ItemStack(CubitItems.INDUCTION_DOCK.get());
    item.applyComponents(dock.collectComponents());

    BlockPos elsewhere = DockScene.FLOOR.west(AWAY);
    GameTestRobots.use(helper, owner, item, elsewhere);
    InductionDockBlockEntity again = helper.getBlockEntity(elsewhere.above());

    helper.assertValueEqual(again.identity().id(), dock.identity().id(), NOT_KEPT);
    helper.assertValueEqual(again.identity().name().value(), GIVEN, NOT_KEPT);
    helper.succeed();
  }

  /** The name tag of the game names the Dock too (answer 1868). */
  @GameTest(template = ARENA)
  public static void aNameTagRenamesTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = place(helper, owner, DockScene.FLOOR);
    ItemStack tag = new ItemStack(Items.NAME_TAG);
    tag.set(DataComponents.CUSTOM_NAME, Component.literal(TAGGED));
    owner.setItemInHand(InteractionHand.MAIN_HAND, tag);
    BlockPos at = helper.absolutePos(DockScene.DOCK);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(at), Direction.UP, at, false);

    helper
        .getLevel()
        .getBlockState(at)
        .useItemOn(tag, helper.getLevel(), owner, InteractionHand.MAIN_HAND, hit);

    helper.assertValueEqual(dock.identity().name().value(), TAGGED, NOT_RENAMED);
    helper.assertTrue(owner.getAbilities().instabuild || tag.isEmpty(), NOT_USED);
    helper.succeed();
  }

  private static InductionDockBlockEntity place(
      GameTestHelper helper, ServerPlayer owner, BlockPos floor) {
    GameTestRobots.use(helper, owner, new ItemStack(CubitItems.INDUCTION_DOCK.get()), floor);
    return helper.getBlockEntity(floor.above());
  }
}
