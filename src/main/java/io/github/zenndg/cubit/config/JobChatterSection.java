package io.github.zenndg.cubit.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The config keys of the phrases of the jobs, beside the chatter. The names are a contract with the
 * pack (answers 1585 and 1586).
 */
public final class JobChatterSection {
  private static final String CHANCE = "job_chatter_chance";
  private static final int CHANCE_DEFAULT = 20;
  private static final int PERCENT_MIN = 0;
  private static final int PERCENT_MAX = 100;
  private static final String INTERVAL = "job_chatter_interval_ticks";
  private static final int INTERVAL_DEFAULT = 2400;
  private static final int TICKS_MIN = 1;
  private static final int TICKS_MAX = 72000;
  private static final String RANGE = "job_chatter_range";
  private static final int RANGE_DEFAULT = 32;
  private static final int RANGE_MIN = 1;
  private static final int RANGE_MAX = 128;

  private final ModConfigSpec.IntValue chancePercent;
  private final ModConfigSpec.IntValue intervalTicks;
  private final ModConfigSpec.IntValue rangeBlocks;

  JobChatterSection(ModConfigSpec.Builder builder) {
    chancePercent = builder.defineInRange(CHANCE, CHANCE_DEFAULT, PERCENT_MIN, PERCENT_MAX);
    intervalTicks = builder.defineInRange(INTERVAL, INTERVAL_DEFAULT, TICKS_MIN, TICKS_MAX);
    rangeBlocks = builder.defineInRange(RANGE, RANGE_DEFAULT, RANGE_MIN, RANGE_MAX);
  }

  /** The chance of a phrase of the work, in percent (answer 1586). */
  public int chancePercent() {
    return chancePercent.get();
  }

  public int intervalTicks() {
    return intervalTicks.get();
  }

  /** How far the owner may be to hear the work (answer 1585). */
  public int rangeBlocks() {
    return rangeBlocks.get();
  }
}
