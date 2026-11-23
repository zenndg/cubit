package io.github.zenndg.cubit.domain.desk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CascadeRulesTest {
  private static final CascadeCheck READY = new CascadeCheck(true, true, true, true, false, false);
  private static final String OAK = "oak";
  private static final String BIRCH = "birch";
  private static final int MANY = 8;

  /** Green with enough, yellow with some and red with none (answer 2006). */
  @Test
  void theLevelOfAMaterial() {
    assertEquals(MaterialLevel.READY, MaterialLevel.of(MANY, MANY));
    assertEquals(MaterialLevel.PARTIAL, MaterialLevel.of(1, MANY));
    assertEquals(MaterialLevel.MISSING, MaterialLevel.of(0, MANY));
  }

  @Test
  void everythingMustBeReadyToStart() {
    assertEquals(CascadeStart.READY, CascadeStart.decide(READY));
    assertEquals(
        CascadeStart.TIER,
        CascadeStart.decide(new CascadeCheck(false, true, true, true, false, false)));
    assertEquals(
        CascadeStart.NO_PLAN,
        CascadeStart.decide(new CascadeCheck(true, false, true, true, false, false)));
    assertEquals(
        CascadeStart.LOCKED,
        CascadeStart.decide(new CascadeCheck(true, true, false, true, false, false)));
    assertEquals(
        CascadeStart.MISSING,
        CascadeStart.decide(new CascadeCheck(true, true, true, false, false, false)));
    assertEquals(
        CascadeStart.ORDER_ACTIVE,
        CascadeStart.decide(new CascadeCheck(true, true, true, true, true, false)));
    assertEquals(
        CascadeStart.STOCK_LEFT,
        CascadeStart.decide(new CascadeCheck(true, true, true, true, false, true)));
  }

  /** With many items for a mark, the one the input has most, or the first (answer 2033). */
  @Test
  void theMaterialTheInputHasMostIsChosen() {
    List<String> options = List.of(OAK, BIRCH);

    assertEquals(BIRCH, CascadeChoice.of(options, Map.of(OAK, 1, BIRCH, MANY)));
    assertEquals(OAK, CascadeChoice.of(options, Map.of(OAK, MANY, BIRCH, MANY)));
    assertEquals(OAK, CascadeChoice.of(options, Map.of()));
  }
}
