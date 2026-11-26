package io.github.zenndg.cubit.domain.ownership;

import io.github.zenndg.cubit.domain.robot.RobotName;
import java.util.Optional;
import java.util.UUID;

/**
 * The name of a block with an owner, as a Dock: the one the owner gave, with the rules of the name
 * of the Cubit, or the standard one, its kind and the first eight digits of its own id, so it is
 * never nameless (answers 1867 to 1869).
 */
public record BlockName(String value) {
  private static final String DOCK_PREFIX = "Dock ";
  private static final String SERVER_PREFIX = "Server ";
  private static final String RELAY_PREFIX = "Relay ";
  private static final String ANTENNA_PREFIX = "Antenna ";
  private static final int ID_DIGITS = 8;
  private static final String INVALID = "A block name is not blank and has up to 50 characters: ";

  public BlockName {
    if (value == null || value.isBlank() || value.length() > RobotName.MAX_LENGTH) {
      throw new IllegalArgumentException(INVALID + value);
    }
  }

  public static BlockName standard(Kind kind, UUID id) {
    return new BlockName(kind.prefix + id.toString().substring(0, ID_DIGITS));
  }

  /** The given name, or the standard one when there is none or it is empty (answer 1868). */
  public static BlockName of(Kind kind, UUID id, Optional<RobotName> given) {
    return kept(given).map(BlockName::new).orElseGet(() -> standard(kind, id));
  }

  /** What is worth saving of a typed name: nothing when it is empty or does not fit. */
  public static Optional<String> kept(Optional<RobotName> typed) {
    return typed.filter(name -> !name.removes()).map(RobotName::value);
  }

  /** What the standard name starts with. */
  public enum Kind {
    DOCK(DOCK_PREFIX),
    SERVER(SERVER_PREFIX),
    RELAY(RELAY_PREFIX),
    ANTENNA(ANTENNA_PREFIX);

    private final String prefix;

    Kind(String prefix) {
      this.prefix = prefix;
    }
  }
}
