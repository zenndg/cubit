package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.domain.xp.XpSettings;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.world.entity.player.Player;

/** What the XP tab reads and changes on the server, which checks the owner (answers 1830, 2070). */
public interface XpControl {
  /** On the client, where the real values come from the server. */
  XpControl NONE =
      new XpControl() {
        @Override
        public XpTank tank() {
          return new XpTank(0, 0);
        }

        @Override
        public XpSettings settings() {
          return XpSettings.INITIAL;
        }

        @Override
        public boolean choose(Player player, XpMode mode) {
          return false;
        }

        @Override
        public boolean toggleExcess(Player player) {
          return false;
        }

        @Override
        public boolean trades() {
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

  XpSettings settings();

  boolean choose(Player player, XpMode mode);

  boolean toggleExcess(Player player);

  /** Whether the Cubit trades XP with its owner now: only an active one (answer 2077). */
  boolean trades();

  /** A level of the player goes to the tank, as much as there is and fits (answer 2076). */
  boolean send(Player player);

  /** A level comes from the tank to the player, as much as there is (answer 2076). */
  boolean receive(Player player);
}
