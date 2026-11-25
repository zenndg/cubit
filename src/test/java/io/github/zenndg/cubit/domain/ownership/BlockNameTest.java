package io.github.zenndg.cubit.domain.ownership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.robot.RobotName;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BlockNameTest {
  private static final UUID ID = UUID.fromString("3f9a2c1b-0000-4000-8000-000000000000");
  private static final String STANDARD = "Dock 3f9a2c1b";
  private static final String SERVER = "Server 3f9a2c1b";
  private static final String RELAY = "Relay 3f9a2c1b";
  private static final String GIVEN = "Garden Dock";
  private static final String BLANK = "   ";

  @Test
  void theStandardNameIsDockAndTheFirstEightDigits() {
    assertEquals(STANDARD, BlockName.standard(BlockName.Kind.DOCK, ID).value());
  }

  /** In English in both languages, as the Dock (answer 2401). */
  @Test
  void theBlocksOfTheNetworkHaveTheirOwnStart() {
    assertEquals(SERVER, BlockName.standard(BlockName.Kind.SERVER, ID).value());
    assertEquals(RELAY, BlockName.standard(BlockName.Kind.RELAY, ID).value());
  }

  @Test
  void aGivenNameIsShown() {
    assertEquals(
        GIVEN, BlockName.of(BlockName.Kind.DOCK, ID, Optional.of(new RobotName(GIVEN))).value());
  }

  /** An empty name goes back to the standard one, so a dock always has a name (answer 1868). */
  @Test
  void noNameOrAnEmptyOneShowsTheStandard() {
    assertEquals(STANDARD, BlockName.of(BlockName.Kind.DOCK, ID, Optional.empty()).value());
    assertEquals(STANDARD, BlockName.of(BlockName.Kind.DOCK, ID, RobotName.typed(BLANK)).value());
  }

  @Test
  void anEmptyTypedNameIsNotKept() {
    assertTrue(BlockName.kept(RobotName.typed(BLANK)).isEmpty());
    assertEquals(GIVEN, BlockName.kept(RobotName.typed(GIVEN)).orElseThrow());
  }

  @Test
  void aBlankNameIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new BlockName(BLANK));
  }
}
