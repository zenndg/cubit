package io.github.zenndg.cubit.domain.robot;

/**
 * When a player last saw the link of the introduction, in game ticks. Only the link waits for the
 * cooldown, so a player who places many Cubits in a row gets one link and not a chat full of them
 * (answers 1677 and 1681).
 */
public record IntroLink(long lastShown) {
  public static final long NEVER_SHOWN = -1;
  public static final IntroLink NEVER = new IntroLink(NEVER_SHOWN);
  private static final String SECURE_SCHEME = "https://";
  private static final String INVALID_TIME_MESSAGE = "Invalid tick of the introduction link: ";
  private static final String INVALID_COOLDOWN_MESSAGE = "Invalid cooldown of the link: ";

  public IntroLink {
    if (lastShown < NEVER_SHOWN) {
      throw new IllegalArgumentException(INVALID_TIME_MESSAGE + lastShown);
    }
  }

  /** A time before the last link, as after the world changed, shows it again. */
  public boolean isDue(long now, long cooldownTicks) {
    if (cooldownTicks < 0) {
      throw new IllegalArgumentException(INVALID_COOLDOWN_MESSAGE + cooldownTicks);
    }
    return lastShown == NEVER_SHOWN || now < lastShown || now - lastShown >= cooldownTicks;
  }

  public IntroLink shownAt(long now) {
    if (now < 0) {
      throw new IllegalArgumentException(INVALID_TIME_MESSAGE + now);
    }
    return new IntroLink(now);
  }

  /** Only a secure page, with something after the scheme and no spaces (answer 1674). */
  public static boolean isSafeAddress(String address) {
    return address.startsWith(SECURE_SCHEME)
        && address.length() > SECURE_SCHEME.length()
        && address.chars().noneMatch(Character::isWhitespace);
  }
}
