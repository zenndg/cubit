package io.github.zenndg.cubit.domain.energy;

/**
 * A solar panel that folds, of a Dock or of a Solar Relay (answers 1852 to 1866 and 2338): after a
 * while with sun in a row it opens, after the same while without sun, also at night, it folds, and
 * a motion runs to its end whatever the sky does. Only a panel fully open generates. The caller
 * gives the sky of each tick.
 *
 * @param ticks the sky in a row toward a change, or how far the motion went
 */
public record FoldingPanel(PanelState state, int ticks) {
  public static final FoldingPanel STORED = new FoldingPanel(PanelState.STORED, 0);
  private static final String INVALID = "Invalid timings of the panel: ";
  private static final String SEPARATOR = ", ";

  /** How long the sky must hold before a change, and how long the motion takes. */
  public record Timings(int delayTicks, int motionTicks) {
    public Timings {
      if (delayTicks < 0 || motionTicks < 0) {
        throw new IllegalArgumentException(INVALID + delayTicks + SEPARATOR + motionTicks);
      }
    }
  }

  /** Only the end of a motion is saved, so a load shows the panel already there (answer 1864). */
  public static FoldingPanel saved(boolean deployed) {
    return new FoldingPanel(deployed ? PanelState.DEPLOYED : PanelState.STORED, 0);
  }

  public FoldingPanel tick(boolean sun, Timings timings) {
    return switch (state) {
      case STORED -> wait(sun, timings.delayTicks(), PanelState.DEPLOYING);
      case DEPLOYED -> wait(!sun, timings.delayTicks(), PanelState.RETRACTING);
      case DEPLOYING -> move(timings.motionTicks(), PanelState.DEPLOYED);
      case RETRACTING -> move(timings.motionTicks(), PanelState.STORED);
    };
  }

  public boolean generates() {
    return state == PanelState.DEPLOYED;
  }

  /** Where a motion ends, which is what the save keeps. */
  public boolean deployedAtTheEnd() {
    return state == PanelState.DEPLOYING || state == PanelState.DEPLOYED;
  }

  private FoldingPanel wait(boolean wanted, int delay, PanelState next) {
    if (!wanted) {
      return new FoldingPanel(state, 0);
    }
    int held = ticks + 1;
    return held >= delay ? new FoldingPanel(next, 0) : new FoldingPanel(state, held);
  }

  private FoldingPanel move(int motion, PanelState end) {
    int moved = ticks + 1;
    return moved >= motion ? new FoldingPanel(end, 0) : new FoldingPanel(state, moved);
  }
}
