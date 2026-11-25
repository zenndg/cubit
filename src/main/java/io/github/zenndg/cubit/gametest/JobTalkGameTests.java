package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.ChatRules;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotFace;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The phrases of the work: the owner nearby hears them, with the chance and the interval, and the
 * switch of the chatter silences them (answers 1585 and 1586).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class JobTalkGameTests {
  private static final String ARENA = "arena";
  private static final int FLOOR_Y = 1;
  private static final int MIDDLE_Z = 2;
  private static final int ROBOT_X = 6;
  private static final int LUCKY = 0;
  private static final int UNLUCKY = 99;
  private static final double FAR_AWAY = 64;
  private static final BlockPos FLOOR = new BlockPos(ROBOT_X, FLOOR_Y, MIDDLE_Z);
  private static final String NOT_CALM = "The face of the Cubit should be calm";
  private static final String NOT_SAID = "The Cubit should have spoken of its work";
  private static final String SAID = "The Cubit should not have spoken of its work";

  private JobTalkGameTests() {}

  @GameTest(template = ARENA)
  public static void theOwnerNearbyHearsTheWork(GameTestHelper helper) {
    talkAfterAMoment(helper, LUCKY, false, false, true);
  }

  @GameTest(template = ARENA)
  public static void anUnluckyDrawSaysNothing(GameTestHelper helper) {
    talkAfterAMoment(helper, UNLUCKY, false, false, false);
  }

  @GameTest(template = ARENA)
  public static void anOwnerFarAwayHearsNothing(GameTestHelper helper) {
    talkAfterAMoment(helper, LUCKY, true, false, false);
  }

  @GameTest(template = ARENA)
  public static void theSwitchSilencesTheWork(GameTestHelper helper) {
    talkAfterAMoment(helper, LUCKY, false, true, false);
  }

  /**
   * A placed robot needs some ticks before its face is calm. The owner moves away only then, as the
   * robot follows and would jump to a far owner.
   */
  private static void talkAfterAMoment(
      GameTestHelper helper, int roll, boolean far, boolean switchedOff, boolean expected) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    RobotEntity robot = GameTestRobots.placeNewRobot(helper, owner, FLOOR);
    if (switchedOff) {
      robot.chatter().toggle();
    }
    helper
        .startSequence()
        .thenWaitUntil(
            () -> helper.assertTrue(ChatRules.isCalm(RobotFace.expression(robot)), NOT_CALM))
        .thenExecute(() -> {
          if (far) {
            owner.setPos(owner.getX() + FAR_AWAY, owner.getY(), owner.getZ());
          }
          robot.workArea().work().talk().onWork(JobTalkTopic.FISHER_CATCH, roll);
          boolean said = robot.workArea().work().talk().isHolding();
          helper.assertTrue(said == expected, expected ? NOT_SAID : SAID);
        })
        .thenSucceed();
  }
}
