package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class RobotNameTest {
  private static final String STANDARD_ID = "7c41e0aa-0000-4000-8000-000000000000";
  private static final String STANDARD_NAME = "Cubit 7c41e0aa";

  private static final String NAME = "Bolt";
  private static final String PADDED = "  Bolt  ";
  private static final String BLANK = "   ";
  private static final String LETTER = "a";

  @Test
  void theSpacesAtTheEndsAreCut() {
    assertEquals(NAME, RobotName.typed(PADDED).orElseThrow().value());
  }

  @Test
  void aBlankNameRemovesTheName() {
    RobotName name = RobotName.typed(BLANK).orElseThrow();
    assertTrue(name.removes());
  }

  @Test
  void aNameFitsUpToTheLimitOfTheNameTag() {
    String longest = LETTER.repeat(RobotName.MAX_LENGTH);
    assertEquals(longest, RobotName.typed(longest).orElseThrow().value());
    assertFalse(RobotName.typed(longest).orElseThrow().removes());
  }

  @Test
  void aLongerNameIsRefused() {
    assertTrue(RobotName.typed(LETTER.repeat(RobotName.MAX_LENGTH + 1)).isEmpty());
  }

  @Test
  void theSpacesDoNotCountForTheLimit() {
    String padded = BLANK + LETTER.repeat(RobotName.MAX_LENGTH) + BLANK;
    assertTrue(RobotName.typed(padded).isPresent());
  }

  @Test
  void aNameIsAlwaysCut() {
    assertThrows(IllegalArgumentException.class, () -> new RobotName(PADDED));
    assertThrows(IllegalArgumentException.class, () -> new RobotName(null));
  }

  /** A Cubit is never nameless: Cubit and the first eight characters of its id (1874). */
  @Test
  void theStandardNameIsCubitAndTheFirstEightCharacters() {
    UUID id = UUID.fromString(STANDARD_ID);
    assertEquals(STANDARD_NAME, RobotName.standard(id).value());
  }
}
