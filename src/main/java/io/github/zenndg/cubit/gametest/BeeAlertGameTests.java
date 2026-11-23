package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Husk;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Why a test that hurts a bee needs one without alerts: the common one makes the calm bee of
 * another test chase someone, and a Patroller attacks it then (answer 1702). In a batch of its own,
 * because it alerts on purpose.
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class BeeAlertGameTests {
  private static final String ARENA = "arena";
  private static final String BATCH = "bee_alert";
  private static final int TIMEOUT = 200;
  private static final int SOME_TICKS = 20;
  private static final float DAMAGE = 1f;
  private static final BlockPos HUSK = new BlockPos(1, 2, 2);
  private static final BlockPos CALM = new BlockPos(3, 2, 2);
  private static final BlockPos HURT = new BlockPos(5, 2, 2);
  private static final String ALERTED = "A bee without alerts should not call the others";
  private static final String NOT_ALERTED = "A common hurt bee should call the others";

  private BeeAlertGameTests() {}

  private static void hurt(Mob bee, Husk attacker) {
    bee.hurt(bee.damageSources().mobAttack(attacker), DAMAGE);
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT, batch = BATCH)
  public static void onlyTheCommonBeeAlertsTheOthers(GameTestHelper helper) {
    Husk husk = helper.spawnWithNoFreeWill(EntityType.HUSK, HUSK);
    Mob calm = helper.spawnWithNoFreeWill(EntityType.BEE, CALM);
    hurt(GameTestRobots.spawnWithoutAlerts(helper, EntityType.BEE, HURT), husk);

    helper
        .startSequence()
        .thenExecuteAfter(
            SOME_TICKS,
            () -> {
              helper.assertTrue(calm.getTarget() == null, ALERTED);
              hurt(helper.spawnWithNoFreeWill(EntityType.BEE, HURT), husk);
            })
        .thenExecuteAfter(
            SOME_TICKS, () -> helper.assertTrue(calm.getTarget() == husk, NOT_ALERTED))
        .thenSucceed();
  }
}
