package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.domain.xp.PlayerXp;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/**
 * A level between a player and a tank, counted before and after, so when another mod cancels or
 * changes the trade, only what the player really won or lost moves (detail 9 and rule 13).
 */
public final class PlayerXpTrade {
  private static final float VOLUME = 0.2f;
  private static final float PITCH = 1;

  private PlayerXpTrade() {}

  /** The tank after the player sent it a level. */
  public static XpTank send(Player player, XpTank tank, XpRate rate) {
    PlayerXp before = read(player);
    int points = Math.min(before.toLevelDown(), tank.room(rate));
    if (points == 0) {
      return tank;
    }
    long lost = before.total() - give(player, -points).total();
    XpTank.Moved filled = tank.fill(Math.clamp(lost, 0, Integer.MAX_VALUE), rate);
    return filled.points() > 0 ? done(player, filled.tank()) : tank;
  }

  /** The tank after the player took a level from it. */
  public static XpTank receive(Player player, XpTank tank, XpRate rate) {
    PlayerXp before = read(player);
    int points = Math.min(before.toNextLevel(), tank.points(rate));
    if (points == 0) {
      return tank;
    }
    long won = give(player, points).total() - before.total();
    XpTank.Moved drained = tank.drain(Math.clamp(won, 0, Integer.MAX_VALUE), rate);
    return drained.points() > 0 ? done(player, drained.tank()) : tank;
  }

  private static PlayerXp read(Player player) {
    return PlayerXp.of(player.experienceLevel, player.experienceProgress);
  }

  /** Through the game, so other mods see it, and then in whole points, without its fractions. */
  private static PlayerXp give(Player player, int points) {
    player.giveExperiencePoints(points);
    PlayerXp after = read(player);
    if (player instanceof ServerPlayer server) {
      server.setExperienceLevels(after.level());
      server.setExperiencePoints(after.pointsInto());
    }
    return after;
  }

  private static XpTank done(Player player, XpTank tank) {
    player
        .level()
        .playSound(
            null,
            player.blockPosition(),
            SoundEvents.EXPERIENCE_ORB_PICKUP,
            SoundSource.PLAYERS,
            VOLUME,
            PITCH);
    return tank;
  }
}
