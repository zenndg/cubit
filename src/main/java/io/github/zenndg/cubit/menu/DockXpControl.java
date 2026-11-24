package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.world.entity.player.Player;

/** What the XP tab of a dock reads and changes on the server, which checks the owner (1837). */
public interface DockXpControl {
  /** On the client, where the real values come from the server. */
  DockXpControl NONE =
      new DockXpControl() {
        @Override
        public XpTank tank() {
          return new XpTank(0, 0);
        }

        @Override
        public DockXpMode mode() {
          return DockXpMode.INITIAL;
        }

        @Override
        public Energy energyMade() {
          return Energy.ZERO;
        }

        @Override
        public boolean choose(Player player, DockXpMode mode) {
          return false;
        }

        @Override
        public boolean send(Player player) {
          return false;
        }

        @Override
        public boolean receive(Player player) {
          return false;
        }
      };

  XpTank tank();

  DockXpMode mode();

  /** What the XP made since the dock exists, only to read (answer 2109). */
  Energy energyMade();

  boolean choose(Player player, DockXpMode mode);

  /** A level of the player goes to the tank, as much as there is and fits (answer 2072). */
  boolean send(Player player);

  /** A level comes from the tank to the player, as much as there is (answer 2072). */
  boolean receive(Player player);
}
