package io.github.zenndg.cubit.domain.dock;

/**
 * The solar panel of every Dock (answers 1852 to 1866): after a while with sun in a row it leaves
 * the cabinet, after the same while without sun, also at night, it goes back, and a motion runs to
 * its end whatever the sky does. Only a panel fully on the tower generates. The caller gives the
 * sky of each tick.
 *
 * @param ticks the sky in a row toward a change, or how far the motion went
 */
public record DockPanel(DockPanelState state, int ticks) {
  public static final DockPanel STORED = new DockPanel(DockPanelState.STORED, 0);
  private static final String INVALID = "Invalid timings of the Dock panel: ";
  private static final String SEPARATOR = ", ";

  /** How long the sky must hold before a change, and how long the motion of the arms takes. */
  public record Timings(int delayTicks, int motionTicks) {
    public Timings {
      if (delayTicks < 0 || motionTicks < 0) {
        throw new IllegalArgumentException(INVALID + delayTicks + SEPARATOR + motionTicks);
      }
    }
  }

  /** Only the end of a motion is saved, so a load shows the panel already there (answer 1864). */
  public static DockPanel saved(boolean deployed) {
    return new DockPanel(deployed ? DockPanelState.DEPLOYED : DockPanelState.STORED, 0);
  }

  public DockPanel tick(boolean sun, Timings timings) {
    return switch (state) {
      case STORED -> wait(sun, timings.delayTicks(), DockPanelState.DEPLOYING);
      case DEPLOYED -> wait(!sun, timings.delayTicks(), DockPanelState.RETRACTING);
      case DEPLOYING -> move(timings.motionTicks(), DockPanelState.DEPLOYED);
      case RETRACTING -> move(timings.motionTicks(), DockPanelState.STORED);
    };
  }

  public boolean generates() {
    return state == DockPanelState.DEPLOYED;
  }

  /** Where a motion ends, which is what the save keeps. */
  public boolean deployedAtTheEnd() {
    return state == DockPanelState.DEPLOYING || state == DockPanelState.DEPLOYED;
  }

  private DockPanel wait(boolean wanted, int delay, DockPanelState next) {
    if (!wanted) {
      return new DockPanel(state, 0);
    }
    int held = ticks + 1;
    return held >= delay ? new DockPanel(next, 0) : new DockPanel(state, held);
  }

  private DockPanel move(int motion, DockPanelState end) {
    int moved = ticks + 1;
    return moved >= motion ? new DockPanel(end, 0) : new DockPanel(state, moved);
  }
}
