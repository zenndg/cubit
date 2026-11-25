package io.github.zenndg.cubit.domain.robot;

import java.util.Optional;
import java.util.UUID;

/**
 * The name the owner gives a Cubit on the Tablet, with the rules of the name tag of the game: up to
 * 50 characters, without the spaces at the ends, and an empty name removes the name (answers 1015
 * to 1017).
 */
public record RobotName(String value) {
  /** The limit of the anvil, so the Tablet names as the name tag does. */
  public static final int MAX_LENGTH = 50;

  private static final String STANDARD_PREFIX = "Cubit ";
  private static final int ID_DIGITS = 8;
  private static final String INVALID_MESSAGE = "A name is cut and has up to 50 characters: ";

  public RobotName {
    if (value == null || !value.strip().equals(value) || value.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(INVALID_MESSAGE + value);
    }
  }

  /** What the player typed, or nothing when it does not fit. */
  public static Optional<RobotName> typed(String text) {
    String cut = text.strip();
    return cut.length() > MAX_LENGTH ? Optional.empty() : Optional.of(new RobotName(cut));
  }

  /** The name of a Cubit without one: Cubit and the first eight characters of its id (1874). */
  public static RobotName standard(UUID id) {
    return new RobotName(STANDARD_PREFIX + shortId(id));
  }

  /** The first eight characters of the id, which call a Cubit by a command (answer 2453). */
  public static String shortId(UUID id) {
    return id.toString().substring(0, ID_DIGITS);
  }

  public boolean removes() {
    return value.isEmpty();
  }
}
