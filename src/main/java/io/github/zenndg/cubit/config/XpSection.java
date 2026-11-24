package io.github.zenndg.cubit.config;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.xp.XpRate;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;

/**
 * The XP of every tier: the fluid of a point, the jobs that give XP and the chance of the Farmer
 * (answers 1817, 1820 and 1823). The names are a contract with the pack.
 */
public final class XpSection {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String RATE = "xp_fluid_per_point";
  private static final int RATE_DEFAULT = 20;
  private static final int RATE_MIN = 1;
  private static final int RATE_MAX = 1000;
  private static final String JOBS = "xp_jobs";
  private static final String NO_JOB = "";
  private static final String INVALID_JOB = "Ignoring an invalid name in xp_jobs: {}";
  private static final String FARMER_CHANCE = "farmer_xp_chance";
  private static final int FARMER_CHANCE_DEFAULT = 20;
  private static final int MIN_ZERO = 0;
  private static final List<String> JOBS_DEFAULT =
      List.of(
          RobotJob.PATROLLER.key(),
          RobotJob.FISHER.key(),
          RobotJob.COOK.key(),
          RobotJob.FARMER.key());

  private final ModConfigSpec.IntValue rate;
  private final ModConfigSpec.ConfigValue<List<? extends String>> jobs;
  private final ModConfigSpec.IntValue farmerChance;
  private final AtomicBoolean warned = new AtomicBoolean();

  XpSection(ModConfigSpec.Builder builder) {
    rate = builder.defineInRange(RATE, RATE_DEFAULT, RATE_MIN, RATE_MAX);
    jobs = builder.defineListAllowEmpty(JOBS, JOBS_DEFAULT, () -> NO_JOB, String.class::isInstance);
    farmerChance =
        builder.defineInRange(FARMER_CHANCE, FARMER_CHANCE_DEFAULT, MIN_ZERO, Percentage.MAX);
  }

  public XpRate rate() {
    return new XpRate(rate.get());
  }

  /** An unknown name is skipped and logged once, as in allowed_jobs (detail 7 of the concept). */
  public Set<RobotJob> jobs() {
    Set<RobotJob> found = EnumSet.noneOf(RobotJob.class);
    for (String key : jobs.get()) {
      Optional<RobotJob> job = RobotJob.fromKey(key);
      job.ifPresent(found::add);
      if (job.isEmpty() && warned.compareAndSet(false, true)) {
        LOGGER.warn(INVALID_JOB, key);
      }
    }
    return found;
  }

  public Percentage farmerChance() {
    return new Percentage(farmerChance.get());
  }
}
