package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

/**
 * What a client shows of a Signal Antenna: its channel, for the tint and the wall, whether it is on
 * and has signal, for the LED and the dish, and when it was placed, so its mast rises only then
 * (answers 2542, 2556 and 2557). It lives in common code, but only the client fills it.
 */
public final class AntennaClientView {
  private static final String SIGNAL_KEY = "SignalState";
  private static final String ENABLED_KEY = "Enabled";
  private static final String CHANNEL_KEY = "Channel";
  private static final String WALL_KEY = "ShowWall";
  private static final String PLACED_KEY = "Placed";

  /** The mast rises in 2.5 s (answer 2557). */
  private static final long RAISE_TICKS = 50;

  private static final long NEVER = Long.MIN_VALUE;

  private AntennaSignal.State signal = AntennaSignal.State.NO_SIGNAL;
  private boolean enabled = true;
  private Optional<AntennaChannel> channel = Optional.empty();
  private boolean wall;
  private long placed = NEVER;

  /** On the server, what travels; a missing value keeps the one the client had. */
  static void write(
      CompoundTag tag, SignalAntennaBlockEntity antenna, long placedAt, boolean showWall) {
    tag.putInt(SIGNAL_KEY, antenna.signal().ordinal());
    tag.putBoolean(ENABLED_KEY, antenna.enabled());
    antenna.link().channel().ifPresent(shown -> tag.putString(CHANNEL_KEY, shown.key()));
    tag.putBoolean(WALL_KEY, showWall);
    tag.putLong(PLACED_KEY, placedAt);
  }

  /** On the client, never a state the antenna does not have (rule 17). */
  void read(CompoundTag tag) {
    if (!tag.contains(SIGNAL_KEY)) {
      return;
    }
    AntennaSignal.State[] states = AntennaSignal.State.values();
    signal = states[Mth.clamp(tag.getInt(SIGNAL_KEY), 0, states.length - 1)];
    enabled = tag.getBoolean(ENABLED_KEY);
    channel = AntennaChannel.of(tag.getString(CHANNEL_KEY));
    wall = tag.getBoolean(WALL_KEY);
    placed = tag.getLong(PLACED_KEY);
  }

  /** On and with signal: the LED is lit and the dish is locked. */
  public boolean linked() {
    return enabled && signal == AntennaSignal.State.LINKED;
  }

  /** Off or stopped, the dish droops. */
  public boolean stopped() {
    return !enabled || signal == AntennaSignal.State.STOPPED;
  }

  public Optional<AntennaChannel> channel() {
    return channel;
  }

  public boolean showsWall() {
    return wall;
  }

  /** Whether the mast is still rising, by the time of the world. */
  public boolean raising(long gameTime) {
    return placed != NEVER && gameTime - placed < RAISE_TICKS;
  }
}
