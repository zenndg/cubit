package io.github.zenndg.cubit.domain.dock;

import io.github.zenndg.cubit.domain.robot.RobotName;
import java.util.Optional;
import java.util.UUID;

/**
 * The name of a Dock: the one the owner gave, with the rules of the name of the Cubit, or the
 * standard "Dock" and the first eight digits of its own id, so a Dock is never nameless (answers
 * 1867 to 1869).
 */
public record DockName(String value) {
  private static final String PREFIX = "Dock ";
  private static final int ID_DIGITS = 8;
  private static final String INVALID = "A Dock name is not blank and has up to 50 characters: ";

  public DockName {
    if (value == null || value.isBlank() || value.length() > RobotName.MAX_LENGTH) {
      throw new IllegalArgumentException(INVALID + value);
    }
  }

  public static DockName standard(UUID id) {
    return new DockName(PREFIX + id.toString().substring(0, ID_DIGITS));
  }

  /** The given name, or the standard one when there is none or it is empty (answer 1868). */
  public static DockName of(UUID id, Optional<RobotName> given) {
    return kept(given).map(DockName::new).orElseGet(() -> standard(id));
  }

  /** What is worth saving of a typed name: nothing when it is empty or does not fit. */
  public static Optional<String> kept(Optional<RobotName> typed) {
    return typed.filter(name -> !name.removes()).map(RobotName::value);
  }
}
