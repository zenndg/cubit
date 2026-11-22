package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.tier.CookSpec;
import io.github.zenndg.cubit.domain.tier.FarmerSpec;
import io.github.zenndg.cubit.domain.tier.FisherSpec;
import io.github.zenndg.cubit.domain.tier.JobSpec;
import io.github.zenndg.cubit.domain.tier.OrganizerSpec;
import java.util.EnumSet;
import java.util.Set;

/** The jobs of each tier and the numbers of each card, the defaults of the config. */
final class JobDefaults {
  /**
   * The jobs of each tier, each one adding to the one below. No job waits for the Ultimate, which
   * takes them all and only improves each card (answers 1145 and 1598).
   */
  private static final Set<RobotJob> ADVANCED_JOB_SET =
      EnumSet.of(RobotJob.FISHER, RobotJob.FARMER, RobotJob.RANCHER);

  private static final Set<RobotJob> ELITE_JOB_SET =
      union(
          ADVANCED_JOB_SET,
          EnumSet.of(
              RobotJob.ORGANIZER,
              RobotJob.COOK,
              RobotJob.PATROLLER,
              RobotJob.MINER,
              RobotJob.BUILDER,
              RobotJob.MECHANIC,
              RobotJob.ALCHEMIST));

  /** Lure and Luck of the Sea from the Elite, and 300 FE a fish on every tier (1185, 1190). */
  private static final Energy FISHER_CATCH_COST = new Energy(300);

  private static final FisherSpec ADVANCED_FISHER = new FisherSpec(0, 0, FISHER_CATCH_COST);
  private static final FisherSpec ELITE_FISHER = new FisherSpec(2, 2, FISHER_CATCH_COST);
  private static final FisherSpec ULTIMATE_FISHER = new FisherSpec(3, 3, FISHER_CATCH_COST);

  /** 50, 75 and 100 FE an action, faster on each tier; fertilizer on all (1226, 1248). */
  private static final FarmerSpec ADVANCED_FARMER =
      new FarmerSpec(new Energy(50), 20, false, true, 0);

  private static final FarmerSpec ELITE_FARMER = new FarmerSpec(new Energy(75), 12, true, true, 0);
  private static final FarmerSpec ULTIMATE_FARMER =
      new FarmerSpec(new Energy(100), 6, true, true, 2);

  /** 9 and 18 chests, 8 and 16 marks, 50 FE a stack, faster on the Ultimate (answer 1300). */
  private static final Energy ORGANIZER_ACTION_COST = new Energy(50);

  private static final OrganizerSpec ELITE_ORGANIZER =
      new OrganizerSpec(9, 8, ORGANIZER_ACTION_COST, 12, 100);
  private static final OrganizerSpec ULTIMATE_ORGANIZER =
      new OrganizerSpec(18, 16, ORGANIZER_ACTION_COST, 6, 40);

  /** 4 and 8 ovens, 50 FE a stack, faster on the Ultimate (answer 1326). */
  private static final Energy COOK_ACTION_COST = new Energy(50);

  private static final CookSpec ELITE_COOK = new CookSpec(4, COOK_ACTION_COST, 12, 100);
  private static final CookSpec ULTIMATE_COOK = new CookSpec(8, COOK_ACTION_COST, 6, 40);
  static final JobSpec ADVANCED =
      new JobSpec(
          ADVANCED_JOB_SET, ADVANCED_FISHER, ADVANCED_FARMER, OrganizerSpec.NONE, CookSpec.NONE);
  static final JobSpec ELITE =
      new JobSpec(ELITE_JOB_SET, ELITE_FISHER, ELITE_FARMER, ELITE_ORGANIZER, ELITE_COOK);
  static final JobSpec ULTIMATE =
      new JobSpec(
          ELITE_JOB_SET, ULTIMATE_FISHER, ULTIMATE_FARMER, ULTIMATE_ORGANIZER, ULTIMATE_COOK);

  private JobDefaults() {}

  private static Set<RobotJob> union(Set<RobotJob> lower, Set<RobotJob> added) {
    Set<RobotJob> all = EnumSet.copyOf(lower);
    all.addAll(added);
    return all;
  }
}
