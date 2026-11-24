package io.github.zenndg.cubit.domain.xp;

/** The mode of the XP tab and the switch Delete the excess, which only the store uses (1827). */
public record XpSettings(XpMode mode, boolean deleteExcess) {
  /** Nothing is lost without the player choosing it (answer 1829). */
  public static final XpSettings INITIAL = new XpSettings(XpMode.STORE, false);

  public XpSettings withMode(XpMode next) {
    return new XpSettings(next, deleteExcess);
  }

  public XpSettings toggledExcess() {
    return new XpSettings(mode, !deleteExcess);
  }

  /** What does not fit is deleted only in the store, with its switch on (1827 and 2064). */
  boolean deletesWhatDoesNotFit() {
    return mode == XpMode.STORE && deleteExcess;
  }
}
