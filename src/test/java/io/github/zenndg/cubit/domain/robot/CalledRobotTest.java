package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CalledRobotTest {
  private static final UUID BOLT = UUID.fromString("7c41e0aa-0000-4000-8000-000000000001");
  private static final UUID REX = UUID.fromString("e03a77c4-0000-4000-8000-000000000002");
  private static final UUID TWIN = UUID.fromString("7c41e0aa-0000-4000-8000-000000000003");
  private static final String BOLT_ID = "7c41e0aa";
  private static final String BOLT_ID_UPPER = "7C41E0AA";
  private static final String TOO_SHORT = "7c41e0";
  private static final String REX_NAME = "Rex";
  private static final String BOLT_NAME = "Bolt";
  private static final String BOLT_LOWER = "bolt";
  private static final String LAST_NAME = "Zed";
  private static final String PREFIX = "Cubit ";

  private static RobotListing listing(UUID id, String name) {
    return new RobotListing(id, name, false, true, true, 0);
  }

  @Test
  void theListIsByName() {
    RobotListing rex = listing(REX, REX_NAME);
    RobotListing bolt = listing(BOLT, BOLT_LOWER);
    assertEquals(List.of(bolt, rex), RobotListings.byName(List.of(rex, bolt)));
  }

  @Test
  void theShortIdIsTheOneOfTheStandardName() {
    assertEquals(BOLT_ID, RobotName.shortId(BOLT));
    assertEquals(PREFIX + BOLT_ID, RobotName.standard(BOLT).value());
  }

  @Test
  void aShortIdCallsItsCubitInAnyCase() {
    RobotListing rex = listing(REX, REX_NAME);
    RobotListing bolt = listing(BOLT, BOLT_NAME);
    assertEquals(Optional.of(bolt), RobotListings.called(List.of(rex, bolt), BOLT_ID_UPPER));
    assertEquals(Optional.empty(), RobotListings.called(List.of(rex, bolt), TOO_SHORT));
  }

  @Test
  void withTheSameShortIdTheFirstByNameComes() {
    RobotListing twin = listing(TWIN, LAST_NAME);
    RobotListing bolt = listing(BOLT, BOLT_NAME);
    assertEquals(Optional.of(bolt), RobotListings.called(List.of(twin, bolt), BOLT_ID));
  }
}
