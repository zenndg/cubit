package io.github.zenndg.cubit.domain.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PatrolRouteTest {
  private static final int MAX = 2;
  private static final int ROOM = 3;
  private static final int SURFACE = 64;
  private static final int ONE = 1;
  private static final int FIVE = 5;
  private static final int NINE = 9;
  private static final String WORLD = "minecraft:overworld";
  private static final String NETHER = "minecraft:the_nether";
  private static final RoutePoint A = new RoutePoint(WORLD, ONE, SURFACE, ONE);
  private static final RoutePoint B = new RoutePoint(WORLD, FIVE, SURFACE, ONE);
  private static final RoutePoint C = new RoutePoint(NETHER, NINE, SURFACE, ONE);

  @Test
  void keepsTheOrderOfTheClicks() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    assertEquals(List.of(A, B), route.points());
  }

  @Test
  void aPointIsNeverMarkedTwice() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX);
    assertSame(route, route.add(A, MAX));
  }

  @Test
  void aFullRouteRefusesAPoint() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    assertSame(route, route.add(C, MAX));
  }

  @Test
  void removingAPointKeepsTheOrderOfTheOthers() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, ROOM).add(B, ROOM).add(C, ROOM);
    assertEquals(List.of(A, C), route.remove(B).points());
    assertEquals(List.of(A, B), route.removeLast().points());
    assertTrue(PatrolRoute.EMPTY.removeLast().isEmpty());
  }

  @Test
  void walksInALoop() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX).add(B, MAX);
    assertEquals(1, route.nextIndex(0));
    assertEquals(0, route.nextIndex(1));
    assertEquals(0, PatrolRoute.EMPTY.nextIndex(0));
  }

  @Test
  void aCubitWalksOnlyThePointsOfItsDimension() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, ROOM).add(C, ROOM).add(B, ROOM);
    assertEquals(List.of(A, B), route.in(WORLD).points());
  }

  @Test
  void readsAPointByItsPlace() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, MAX);
    assertEquals(Optional.of(A), route.at(0));
    assertTrue(route.at(1).isEmpty());
    assertTrue(route.at(-1).isEmpty());
  }

  @Test
  void theNearestPointIsTheOneClosestToWhereItStands() {
    PatrolRoute route = PatrolRoute.EMPTY.add(A, ROOM).add(B, ROOM);
    assertEquals(1, route.nearestTo(NINE, SURFACE, ONE));
    assertEquals(0, route.nearestTo(0, SURFACE, ONE));
  }

  @Test
  void anEmptyRouteHasTheFirstAsTheNearest() {
    assertEquals(0, PatrolRoute.EMPTY.nearestTo(NINE, SURFACE, ONE));
  }
}
