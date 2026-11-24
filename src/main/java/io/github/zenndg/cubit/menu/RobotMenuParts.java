package io.github.zenndg.cubit.menu;

import net.minecraft.world.entity.player.Player;

/** The parts of the robot menu that have buttons of their own, so the menu only picks one. */
record RobotMenuParts(PickupMenuPart pickup, OrganizerMenuPart organizer, SpeedControl speed) {
  static RobotMenuParts of(RobotMenuServer server) {
    return new RobotMenuParts(
        new PickupMenuPart(server.pickup()),
        new OrganizerMenuPart(server.organizer()),
        server.speed());
  }

  static boolean handles(int button) {
    return PickupMenuPart.handles(button)
        || SpeedButtons.handles(button)
        || OrganizerMenuPart.handles(button);
  }

  boolean press(Player player, int button) {
    if (PickupMenuPart.handles(button)) {
      return pickup.press(player, button);
    }
    if (SpeedButtons.handles(button)) {
      return SpeedButtons.press(speed, player, button);
    }
    return organizer.press(player, button);
  }

  /** On the server only, before the menu sends its changes. */
  void refresh() {
    pickup.refresh();
    organizer.refresh();
  }
}
