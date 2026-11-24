package io.github.zenndg.cubit.domain.dock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.xp.XpConversion;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DockXpRulesTest {
  private static final XpRate RATE = new XpRate(20);
  private static final int FE = 9;
  private static final XpConversion NINE = new XpConversion(RATE, FE);
  private static final int CAPACITY = 1_000;
  private static final int POINTS_OF_THE_TANK = 50;
  private static final Energy BATTERY = new Energy(300_000);
  private static final DockStock EMPTY = DockStock.of(BATTERY, Energy.ZERO);
  private static final DockStock FULL = DockStock.of(BATTERY, BATTERY);
  private static final int POINTS = 10;
  private static final int ROOM = 4;
  private static final String STORE_CONVERT = "STORE_CONVERT_EXCESS";
  private static final String CONVERT_ALL = "CONVERT_ALL";
  private static final String REMOVED = "store_half_convert_rest";
  private static final String DEFAULT = "store_convert_excess";

  private static XpTank tank(int points) {
    return new XpTank(RATE.fluidOf(points), CAPACITY);
  }

  private static DockStock roomFor(int energy) {
    return DockStock.of(BATTERY, new Energy(BATTERY.amount() - energy));
  }

  private static DockXpRules.Received receive(DockXpMode mode, XpTank tank, DockStock stock) {
    return DockXpRules.receive(mode, tank, stock, POINTS, NINE);
  }

  /** What arrives is kept, converted or deleted, and nothing else (rule 13). */
  @ParameterizedTest
  @EnumSource(DockXpMode.class)
  void everyPointIsKeptConvertedOrDeleted(DockXpMode mode) {
    for (DockStock stock : new DockStock[] {EMPTY, FULL, roomFor(FE * 2 + 1)}) {
      for (int held : new int[] {0, POINTS_OF_THE_TANK - ROOM, POINTS_OF_THE_TANK}) {
        DockXpRules.Received got = receive(mode, tank(held), stock);
        int kept = got.tank().points(RATE) - held;
        assertEquals(POINTS, kept + got.converted() + got.deleted());
        long made = got.stock().energy().charge().amount() - stock.energy().charge().amount();
        assertEquals((long) got.converted() * FE, made);
      }
    }
  }

  @Test
  void deletingAllKeepsOnlyWhatWasStored() {
    DockXpRules.Received got = receive(DockXpMode.DELETE_ALL, tank(ROOM), EMPTY);
    assertEquals(tank(ROOM), got.tank());
    assertEquals(POINTS, got.deleted());
    assertFalse(got.overflowed());
  }

  @Test
  void storingDeletesOrConvertsOnlyTheExcess() {
    XpTank nearly = tank(POINTS_OF_THE_TANK - ROOM);
    DockXpRules.Received deleted = receive(DockXpMode.STORE_DELETE_EXCESS, nearly, EMPTY);
    assertEquals(POINTS - ROOM, deleted.deleted());
    assertEquals(0, deleted.converted());
    DockXpRules.Received converted = receive(DockXpMode.STORE_CONVERT_EXCESS, nearly, EMPTY);
    assertEquals(POINTS - ROOM, converted.converted());
    assertEquals(0, converted.deleted());
    assertTrue(deleted.overflowed() && converted.overflowed());
    assertFalse(receive(DockXpMode.STORE_CONVERT_EXCESS, tank(0), EMPTY).overflowed());
  }

  /** With a full battery the XP waits in the tank while it fits (answer 2061). */
  @ParameterizedTest
  @EnumSource(
      value = DockXpMode.class,
      names = {STORE_CONVERT, CONVERT_ALL})
  void xpIsDeletedOnlyWithTheBatteryAndTheTankFull(DockXpMode mode) {
    assertEquals(0, receive(mode, tank(0), FULL).deleted());
    assertEquals(POINTS, receive(mode, tank(POINTS_OF_THE_TANK), FULL).deleted());
    assertEquals(0, receive(mode, tank(POINTS_OF_THE_TANK), EMPTY).deleted());
  }

  /** A point needs room for all of its energy, so none is lost (detail 11). */
  @Test
  void aBatteryWithoutRoomForAPointCountsAsFull() {
    XpTank full = tank(POINTS_OF_THE_TANK);
    DockXpRules.Received got = receive(DockXpMode.CONVERT_ALL, full, roomFor(FE * 2 + 1));
    assertEquals(2, got.converted());
    assertEquals(POINTS - 2, got.deleted());
    assertEquals(1, got.stock().room().amount());
  }

  /** Only Convert all spends what is stored, at the pace it is given (1935, 2101 and 2104). */
  @ParameterizedTest
  @EnumSource(DockXpMode.class)
  void onlyConvertAllSpendsWhatIsStored(DockXpMode mode) {
    DockXpRules.Converted got = DockXpRules.convert(mode, tank(POINTS), EMPTY, 2, NINE);
    int expected = mode == DockXpMode.CONVERT_ALL ? 2 : 0;
    assertEquals(expected, got.points());
    assertEquals(tank(POINTS - expected), got.tank());
    assertEquals((long) expected * FE, got.stock().energy().charge().amount());
  }

  @Test
  void convertingStopsWithTheTankEmptyOrTheBatteryFull() {
    assertEquals(
        1, DockXpRules.convert(DockXpMode.CONVERT_ALL, tank(1), EMPTY, POINTS, NINE).points());
    assertEquals(
        0, DockXpRules.convert(DockXpMode.CONVERT_ALL, tank(POINTS), FULL, POINTS, NINE).points());
    assertEquals(
        1,
        DockXpRules.convert(DockXpMode.CONVERT_ALL, tank(POINTS), roomFor(FE), POINTS, NINE)
            .points());
  }

  /** A pipe may leave less than a point, which stays in the tank (answer 2102). */
  @Test
  void whatIsShortOfAPointStays() {
    XpTank odd = new XpTank(RATE.fluidPerPoint() + 1, CAPACITY);
    DockXpRules.Converted got =
        DockXpRules.convert(DockXpMode.CONVERT_ALL, odd, EMPTY, POINTS, NINE);
    assertEquals(1, got.points());
    assertEquals(1, got.tank().fluid());
  }

  /** Over a capacity the config made smaller, nothing is spent by the store (answer 2101). */
  @Test
  void aSmallerCapacityKeepsWhatIsStored() {
    XpTank over = new XpTank(CAPACITY * 2, CAPACITY);
    DockXpRules.Received got = receive(DockXpMode.STORE_CONVERT_EXCESS, over, EMPTY);
    assertEquals(over, got.tank());
    assertEquals(POINTS, got.converted());
  }

  @Test
  void theModesAreKnownByTheirKeys() {
    for (DockXpMode mode : DockXpMode.values()) {
      assertEquals(mode, DockXpMode.fromKey(mode.key()).orElseThrow());
    }
    assertTrue(DockXpMode.fromKey(REMOVED).isEmpty());
    assertEquals(DEFAULT, DockXpMode.INITIAL.key());
  }
}
