package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.HoldTime;
import io.github.zenndg.cubit.menu.AntennaScreenKeys;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.SignalAntennaMenu;
import net.minecraft.network.chat.Component;

/**
 * The words and colors of the state of a Signal Antenna on its screen: on the title, the signal,
 * the chunk and how long its reserve holds, as the approved preview (answers 2548 and 2559).
 */
final class AntennaTexts {
  private AntennaTexts() {}

  static Component status(SignalAntennaMenu menu) {
    if (!menu.enabled()) {
      return Component.translatable(AntennaScreenKeys.OFF);
    }
    return Component.translatable(
        switch (menu.signal()) {
          case LINKED -> AntennaScreenKeys.LINKED;
          case NO_SIGNAL -> AntennaScreenKeys.NO_SIGNAL;
          case STOPPED -> AntennaScreenKeys.STOPPED;
        });
  }

  static int color(SignalAntennaMenu menu) {
    if (!menu.enabled()) {
      return ScreenPainter.STATUS_GRAY;
    }
    return switch (menu.signal()) {
      case LINKED -> ScreenPainter.STATUS_GREEN;
      case NO_SIGNAL -> ScreenPainter.STATUS_ORANGE;
      case STOPPED -> ScreenPainter.STATUS_RED;
    };
  }

  static Component signal(SignalAntennaMenu menu) {
    if (!menu.enabled()) {
      return Component.translatable(NetworkKeys.NONE);
    }
    return switch (menu.signal()) {
      case LINKED -> Component.translatable(AntennaScreenKeys.LINKED);
      case NO_SIGNAL ->
          Component.translatable(AntennaScreenKeys.NO_SIGNAL_LEFT, menu.minutesLeft());
      case STOPPED -> Component.translatable(AntennaScreenKeys.STOPPED_LINE);
    };
  }

  static Component chunk(SignalAntennaMenu menu) {
    if (!menu.enabled()) {
      return Component.translatable(AntennaScreenKeys.ANTENNA_OFF);
    }
    ChunkState state = menu.chunkState();
    SignalAntennaMenu.AntennaNumbers numbers = menu.numbers();
    return switch (state) {
      case HOLDING ->
          numbers.perPlayer() == 0
              ? Component.translatable(NetworkKeys.HOLDING_WITHOUT_LIMIT, menu.chunksHeld())
              : Component.translatable(
                  NetworkKeys.chunk(state), menu.chunksHeld(), numbers.perPlayer());
      case NO_ENERGY ->
          Component.translatable(
              NetworkKeys.chunk(state), EnergyFormat.compact(new Energy(numbers.resume())));
      case PLAYER_LIMIT -> Component.translatable(NetworkKeys.chunk(state), numbers.perPlayer());
      case NO_SIGNAL -> Component.translatable(NetworkKeys.chunk(state), numbers.lossMinutes());
      default -> Component.translatable(NetworkKeys.chunk(state));
    };
  }

  static int chunkColor(SignalAntennaMenu menu) {
    if (!menu.enabled()) {
      return ScreenPainter.DETAIL;
    }
    return switch (menu.chunkState()) {
      case HOLDING, SHARED -> ScreenPainter.STATUS_GREEN;
      case NO_ENERGY, NO_SIGNAL -> ScreenPainter.STATUS_RED;
      case PLAYER_LIMIT, WORLD_LIMIT, OWNER_OFFLINE -> ScreenPainter.STATUS_ORANGE;
      case LOCKED, SWITCHED_OFF -> ScreenPainter.DETAIL;
    };
  }

  /** How long the reserve pays what the antenna spends now (answer 2221). */
  static Component holds(SignalAntennaMenu menu) {
    Energy price = menu.spending();
    if (price.isZero()) {
      return Component.translatable(NetworkKeys.NONE);
    }
    HoldTime time = HoldTime.of(menu.ups().holdTicks(menu.capacity(), price));
    return time.hours() == 0
        ? Component.translatable(NetworkKeys.HOLDS_MINUTES, time.minutes())
        : Component.translatable(NetworkKeys.HOLDS_HOURS, time.hours(), time.minutes());
  }
}
