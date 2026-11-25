package io.github.zenndg.cubit.domain.network;

/**
 * The front of the Network Server: columns of ten bars and a lamp for each channel. Without energy
 * everything is off (Delivery 3, detail 11).
 */
public final class ServerFront {
  public static final int BARS = 10;

  private ServerFront() {}

  /** Ten per cent each, with at least one lit above zero, and all of them only when full. */
  public static int bars(long amount, long capacity) {
    if (amount <= 0 || capacity <= 0) {
      return 0;
    }
    if (amount >= capacity) {
      return BARS;
    }
    return Math.max(1, (int) (amount / (capacity / (double) BARS)));
  }

  public static Lamp lamp(int index, int channels, boolean powered) {
    if (index >= channels) {
      return Lamp.CLOSED;
    }
    return powered ? Lamp.FREE : Lamp.OFF;
  }

  /** The state of a lamp. The channels in use come with the antennas. */
  public enum Lamp {
    FREE,
    CLOSED,
    OFF
  }
}
