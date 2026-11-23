package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Every Cubit has a name (answers 1874 to 1880). Without one, it is Cubit and the first eight
 * characters of its id. On its first placement, the window of the name opens for who placed it, and
 * the greeting waits for the answer, to say the chosen name.
 */
public final class RobotNaming {
  /** The robots placed for the first time, by who placed them, waiting for the window. */
  private static final Map<UUID, UUID> WAITING = new HashMap<>();

  /** As far as the player reaches a block of the game, to answer the window. */
  private static final double REACH = 16;

  private RobotNaming() {}

  /** A nameless Cubit, as one from an old save, gets the standard name, once (answer 1877). */
  public static void nameIfNameless(RobotEntity robot) {
    if (!robot.hasCustomName()) {
      robot.setCustomName(Component.literal(RobotName.standard(robot.getUUID()).value()));
    }
  }

  /** On the first placement only, the window opens, and the greeting waits for it (1879, 1880). */
  public static void placed(RobotEntity robot, Player player) {
    nameIfNameless(robot);
    if (!RobotIntro.markFirstPlacement(robot) || !(player instanceof ServerPlayer server)) {
      return;
    }
    WAITING.put(robot.getUUID(), server.getUUID());
    CubitNetwork.askName(server, robot.getUUID(), robot.getName().getString());
  }

  /**
   * The answer of the window, once per placement, only from who placed it and within reach. A name
   * that fits names the Cubit; an empty one keeps the standard name. Then it greets (1879, 1880).
   */
  public static void answered(ServerPlayer player, UUID id, String typed) {
    if (!player.getUUID().equals(WAITING.get(id))) {
      return;
    }
    WAITING.remove(id);
    if (!(player.serverLevel().getEntity(id) instanceof RobotEntity robot)
        || robot.distanceTo(player) > REACH) {
      return;
    }
    Optional<RobotName> name = RobotName.typed(typed).filter(given -> !given.removes());
    name.ifPresent(given -> robot.setCustomName(Component.literal(given.value())));
    RobotIntro.greet(robot, player);
  }
}
