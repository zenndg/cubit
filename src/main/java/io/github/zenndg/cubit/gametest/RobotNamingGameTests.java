package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotNaming;
import io.github.zenndg.cubit.entity.RobotTablet;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Every Cubit has a name: the standard one on its first placement and on load, the window of the
 * name once, and the Rename of the Tablet going back to the standard (answers 1874 to 1880).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RobotNamingGameTests {
  private static final String ARENA = "arena";
  private static final BlockPos FLOOR = new BlockPos(3, 1, 2);
  private static final String GIVEN = "Rex";
  private static final String LATER = "Max";
  private static final String NO_NAME = "";
  private static final String NAME_KEY = "CustomName";
  private static final String WRONG_STANDARD = "A new Cubit has the standard name";
  private static final String NOT_NAMED = "The window should name the Cubit";
  private static final String NAMED_TWICE = "The window answers only once";
  private static final String NAMED_BY_OTHER = "Only who placed it answers the window";
  private static final String NAMELESS = "A Cubit loaded without a name gets the standard one";

  private RobotNamingGameTests() {}

  @GameTest(template = ARENA)
  public static void aNewCubitHasTheStandardName(GameTestHelper helper) {
    RobotEntity robot =
        GameTestRobots.placeNewRobot(helper, GameTestRobots.onlinePlayerAt(helper, FLOOR), FLOOR);
    helper.assertValueEqual(nameOf(robot), standardOf(robot), WRONG_STANDARD);
    helper.succeed();
  }

  /** The window names it once, and a second answer changes nothing (answer 1879). */
  @GameTest(template = ARENA)
  public static void theWindowNamesTheCubitOnce(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);

    RobotNaming.answered(owner, robot.getUUID(), GIVEN);
    helper.assertValueEqual(nameOf(robot), GIVEN, NOT_NAMED);
    RobotNaming.answered(owner, robot.getUUID(), LATER);
    helper.assertValueEqual(nameOf(robot), GIVEN, NAMED_TWICE);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void anEmptyAnswerKeepsTheStandardName(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);

    RobotNaming.answered(owner, robot.getUUID(), NO_NAME);
    helper.assertValueEqual(nameOf(robot), standardOf(robot), WRONG_STANDARD);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void onlyWhoPlacedItAnswersTheWindow(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);

    RobotNaming.answered(GameTestRobots.onlinePlayerAt(helper, FLOOR), robot.getUUID(), GIVEN);
    helper.assertValueEqual(nameOf(robot), standardOf(robot), NAMED_BY_OTHER);
    helper.succeed();
  }

  /** A Cubit of an old save, without a name, gets the standard one on load (answer 1877). */
  @GameTest(template = ARENA)
  public static void aNamelessSavedCubitGetsTheStandardName(GameTestHelper helper) {
    RobotEntity robot =
        GameTestRobots.placeNewRobot(helper, GameTestRobots.onlinePlayerAt(helper, FLOOR), FLOOR);
    CompoundTag saved = robot.saveWithoutId(new CompoundTag());
    saved.remove(NAME_KEY);

    RobotEntity loaded = GameTestRobots.loadRobot(helper, saved);
    helper.assertValueEqual(nameOf(loaded), standardOf(loaded), NAMELESS);
    helper.succeed();
  }

  /** An empty Rename on the Tablet goes back to the standard name (answer 1878). */
  @GameTest(template = ARENA)
  public static void anEmptyRenameGoesBackToTheStandardName(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayerAt(helper, FLOOR);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    RobotTablet.rename(owner, robot.getUUID(), GIVEN);

    RobotTablet.rename(owner, robot.getUUID(), NO_NAME);
    helper.assertValueEqual(nameOf(robot), standardOf(robot), WRONG_STANDARD);
    helper.succeed();
  }

  private static String nameOf(RobotEntity robot) {
    return robot.getName().getString();
  }

  private static String standardOf(RobotEntity robot) {
    return RobotName.standard(robot.getUUID()).value();
  }
}
