package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.RobotName;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DockNameTest {
  private static final UUID ID = UUID.fromString("3f9a2c1b-0000-4000-8000-000000000000");
  private static final String STANDARD = "Dock 3f9a2c1b";
  private static final String GIVEN = "Garden Dock";
  private static final String BLANK = "   ";

  @Test
  void theStandardNameIsDockAndTheFirstEightDigits() {
    assertEquals(STANDARD, DockName.standard(ID).value());
  }

  @Test
  void aGivenNameIsShown() {
    assertEquals(GIVEN, DockName.of(ID, Optional.of(new RobotName(GIVEN))).value());
  }

  /** An empty name goes back to the standard one, so a dock always has a name (answer 1868). */
  @Test
  void noNameOrAnEmptyOneShowsTheStandard() {
    assertEquals(STANDARD, DockName.of(ID, Optional.empty()).value());
    assertEquals(STANDARD, DockName.of(ID, RobotName.typed(BLANK)).value());
  }

  @Test
  void anEmptyTypedNameIsNotKept() {
    assertTrue(DockName.kept(RobotName.typed(BLANK)).isEmpty());
    assertEquals(GIVEN, DockName.kept(RobotName.typed(GIVEN)).orElseThrow());
  }

  @Test
  void aBlankDockNameIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new DockName(BLANK));
  }
}
