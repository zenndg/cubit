package io.github.zenndg.cubit.domain.network;

/**
 * What the front of a Network Server shows: whether it has energy, whether energy passes, for the
 * fans, whether it has its relay, the bars of the two columns and the open channels (answers 2329,
 * 2338 and 2375).
 */
public record ServerLook(
    boolean powered, boolean flowing, boolean linked, Columns columns, int channels) {
  public static final ServerLook OFF = new ServerLook(false, false, false, new Columns(0, 0), 0);
  private static final String BAD_CHANNELS = "A server shows from 0 to 16 channels: ";

  public ServerLook {
    if (channels < 0 || channels > ServerSpec.MAX_CHANNELS) {
      throw new IllegalArgumentException(BAD_CHANNELS + channels);
    }
  }

  /** The bars lit of the energy and of the storage, each from 0 to 10. */
  public record Columns(int energy, int items) {
    private static final String BAD_BARS = "A column has from 0 to 10 bars: ";

    public Columns {
      check(energy);
      check(items);
    }

    private static void check(int bars) {
      if (bars < 0 || bars > ServerFront.BARS) {
        throw new IllegalArgumentException(BAD_BARS + bars);
      }
    }
  }
}
