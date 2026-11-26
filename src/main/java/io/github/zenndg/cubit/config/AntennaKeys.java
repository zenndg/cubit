package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaSpec;
import io.github.zenndg.cubit.domain.network.SignalDistance;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The keys of the Signal Antenna and of the cost of its distance, in the section of the Network
 * (answers 2221, 2273, 2310, 2323, 2326 and 2435). The names are a contract (answer 2334).
 */
public final class AntennaKeys {
  private static final String CAPACITY = "antenna_energy_capacity";
  private static final int CAPACITY_DEFAULT = 600_000;
  private static final String INPUT = "antenna_input_per_tick";
  private static final int INPUT_DEFAULT = 5_000;
  private static final String STANDBY = "antenna_standby_per_tick";
  private static final int STANDBY_DEFAULT = 1;
  private static final String UPS = "antenna_ups_percent";
  private static final int UPS_DEFAULT = 50;
  private static final String SIGNAL_LOSS = "signal_loss_ticks";
  private static final int SIGNAL_LOSS_DEFAULT = 24_000;
  private static final String FREE = "distance_free_blocks";
  private static final int FREE_DEFAULT = 100;
  private static final String STEP = "distance_step_blocks";
  private static final int STEP_DEFAULT = 500;
  private static final String DISTANCE_COST = "distance_cost";
  private static final int DISTANCE_COST_DEFAULT = 1;
  private static final int NONE = 0;
  private static final int ONE = 1;

  private final ModConfigSpec.IntValue capacity;
  private final ModConfigSpec.IntValue input;
  private final ModConfigSpec.IntValue standby;
  private final ModConfigSpec.IntValue ups;
  private final ModConfigSpec.IntValue signalLoss;
  private final ModConfigSpec.IntValue free;
  private final ModConfigSpec.IntValue step;
  private final ModConfigSpec.IntValue distanceCost;

  AntennaKeys(ModConfigSpec.Builder builder) {
    capacity = builder.defineInRange(CAPACITY, CAPACITY_DEFAULT, ONE, Integer.MAX_VALUE);
    input = builder.defineInRange(INPUT, INPUT_DEFAULT, NONE, Integer.MAX_VALUE);
    standby = builder.defineInRange(STANDBY, STANDBY_DEFAULT, NONE, Integer.MAX_VALUE);
    ups = builder.defineInRange(UPS, UPS_DEFAULT, NONE, UpsReserve.MAX);
    signalLoss = builder.defineInRange(SIGNAL_LOSS, SIGNAL_LOSS_DEFAULT, ONE, Integer.MAX_VALUE);
    free = builder.defineInRange(FREE, FREE_DEFAULT, NONE, Integer.MAX_VALUE);
    step = builder.defineInRange(STEP, STEP_DEFAULT, ONE, Integer.MAX_VALUE);
    distanceCost =
        builder.defineInRange(DISTANCE_COST, DISTANCE_COST_DEFAULT, NONE, Integer.MAX_VALUE);
  }

  public AntennaSpec spec() {
    return new AntennaSpec(
        new Energy(capacity.get()),
        new Energy(input.get()),
        new Energy(standby.get()),
        UpsReserve.of(ups.get()),
        signalLoss.get());
  }

  public SignalDistance distance() {
    return new SignalDistance(free.get(), step.get(), new Energy(distanceCost.get()));
  }
}
