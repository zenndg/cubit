package io.github.zenndg.cubit.domain.xp;

/** The notice of a full tank: once each time it fills, and again after it lowers (answer 1840). */
public record XpNotice(boolean told) {
  public static final XpNotice START = new XpNotice(false);

  public boolean tells(boolean full) {
    return full && !told;
  }

  public XpNotice update(boolean full) {
    return new XpNotice(full);
  }
}
