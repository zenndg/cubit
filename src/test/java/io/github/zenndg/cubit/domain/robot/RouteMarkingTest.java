package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RouteMarkingTest {
  private static final int MAX = 2;
  private static final int SURFACE = 64;
  private static final int ONE = 1;
  private static final int FIVE = 5;
  private static final int NINE = 9;
  private static final String WORLD = "minecraft:overworld";
  private static final RoutePoint A = new RoutePoint(WORLD, ONE, SURFACE, ONE);
  private static final RoutePoint B = new RoutePoint(WORLD, FIVE, SURFACE, ONE);
  private static final RoutePoint C = new RoutePoint(WORLD, NINE, SURFACE, ONE);

  private static RouteMarking.Click valid(RoutePoint point) {
    return new RouteMarking.Click(true, true, true, point);
  }

  @Test
  void aValidClickAddsThePointAtTheEnd() {
    RouteMarking.Marked marked = RouteMarking.click(PatrolRoute.EMPTY.add(A, MAX), valid(B), MAX);
    assertEquals(RouteMarking.Result.ADDED, marked.result());
    assertEquals(List.of(A, B), marked.route().points());
  }

  @Test
  void aClickOnAMarkedPointUnmarksItAndKeepsTheOrder() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX + 1).add(B, MAX + 1).add(C, MAX + 1);
    RouteMarking.Marked marked = RouteMarking.click(route, valid(B), MAX + 1);
    assertEquals(RouteMarking.Result.REMOVED, marked.result());
    assertEquals(List.of(A, C), marked.route().points());
  }

  @Test
  void aMarkedPointIsUnmarkedEvenOutsideTheArea() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX);
    RouteMarking.Click outside = new RouteMarking.Click(true, false, true, A);
    assertEquals(RouteMarking.Result.REMOVED, RouteMarking.click(route, outside, MAX).result());
  }

  @Test
  void withoutAnAreaNothingIsMarked() {
    RouteMarking.Click noArea = new RouteMarking.Click(false, false, true, A);
    RouteMarking.Marked marked = RouteMarking.click(PatrolRoute.EMPTY, noArea, MAX);
    assertEquals(RouteMarking.Result.NO_AREA, marked.result());
    assertSame(PatrolRoute.EMPTY, marked.route());
  }

  @Test
  void outsideTheAreaNothingIsMarked() {
    RouteMarking.Click outside = new RouteMarking.Click(true, false, true, A);
    assertEquals(
        RouteMarking.Result.OUTSIDE_AREA,
        RouteMarking.click(PatrolRoute.EMPTY, outside, MAX).result());
  }

  @Test
  void aBlockThatIsNotSolidIsRefused() {
    RouteMarking.Click soft = new RouteMarking.Click(true, true, false, A);
    assertEquals(
        RouteMarking.Result.NOT_SOLID, RouteMarking.click(PatrolRoute.EMPTY, soft, MAX).result());
  }

  @Test
  void aFullRouteRefusesANewPoint() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    RouteMarking.Marked marked = RouteMarking.click(route, valid(C), MAX);
    assertEquals(RouteMarking.Result.FULL, marked.result());
    assertSame(route, marked.route());
  }

  @Test
  void shiftOnAMarkedPointTakesThatOneOut() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    assertEquals(List.of(B), RouteMarking.unmark(route, Optional.of(A)).orElseThrow().points());
  }

  @Test
  void shiftElsewhereTakesTheLastOneOut() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    assertEquals(List.of(A), RouteMarking.unmark(route, Optional.of(C)).orElseThrow().points());
    assertEquals(List.of(A), RouteMarking.unmark(route, Optional.empty()).orElseThrow().points());
  }

  @Test
  void shiftOnAnEmptyRouteHasNothingToTakeOut() {
    assertTrue(RouteMarking.unmark(PatrolRoute.EMPTY, Optional.of(A)).isEmpty());
  }
}
