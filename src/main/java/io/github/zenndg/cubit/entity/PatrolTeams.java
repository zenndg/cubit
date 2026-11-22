package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.PatrolTeam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

/**
 * The armed Galactic Patrollers, so each one finds its team: the others of the same owner, in the
 * same dimension, with areas that touch or within the call (answers 1643, 1644 and 1707). It lives
 * only in memory, on the server, and a Patroller that leaves the world, collected, unloaded or
 * dead, leaves it.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class PatrolTeams {
  private static final Map<UUID, RobotEntity> ARMED = new HashMap<>();

  private PatrolTeams() {}

  static void update(RobotEntity robot, boolean armed) {
    if (armed) {
      ARMED.put(robot.getUUID(), robot);
    } else {
      ARMED.remove(robot.getUUID());
    }
  }

  /** The others of its team, never itself, all loaded in the same level. */
  static List<RobotEntity> mates(RobotEntity robot) {
    Optional<PatrolTeam.Member> self = member(robot);
    if (self.isEmpty()) {
      return List.of();
    }
    PatrolTeam team = CubitMod.CONFIG.patrol().team();
    return ARMED.values().stream()
        .filter(other -> other != robot && other.isAlive() && other.level() == robot.level())
        .filter(other -> member(other).filter(mate -> team.together(self.get(), mate)).isPresent())
        .toList();
  }

  private static Optional<PatrolTeam.Member> member(RobotEntity robot) {
    return robot
        .ownerId()
        .map(owner ->
            new PatrolTeam.Member(
                owner,
                RobotUnload.dimensionOf(robot.level()),
                robot.getX(),
                robot.getY(),
                robot.getZ(),
                robot.workArea().here()));
  }

  /** Only on the server, which keeps the team; the client shares the map in one game. */
  @SubscribeEvent
  public static void onLeave(EntityLeaveLevelEvent event) {
    if (!event.getLevel().isClientSide() && event.getEntity() instanceof RobotEntity robot) {
      ARMED.remove(robot.getUUID());
    }
  }
}
