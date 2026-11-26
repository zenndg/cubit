package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.block.AntennaPlacing;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.menu.AntennaScreenKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import snownee.jade.api.ITooltip;

/**
 * The lines of the Signal Antenna in Jade, short: its channel, its signal and the switch of its
 * chunk (answer 2285). It has no tier, so it shows none.
 */
final class AntennaJadeLines {
  private static final String ANTENNA = "Antenna";
  private static final String CHANNEL = "Channel";
  private static final String SIGNAL = "Signal";
  private static final String MINUTES = "Minutes";
  private static final String ENABLED = "Enabled";
  private static final String CHUNK = "Chunk";

  private AntennaJadeLines() {}

  static void write(CompoundTag data, SignalAntennaBlockEntity antenna) {
    data.putBoolean(ANTENNA, true);
    antenna.link().channel().ifPresent(channel -> data.putString(CHANNEL, channel.key()));
    data.putInt(SIGNAL, antenna.signal().ordinal());
    data.putInt(MINUTES, antenna.minutesLeft());
    data.putBoolean(ENABLED, antenna.enabled());
    data.putBoolean(CHUNK, antenna.chunkLoading());
  }

  static boolean isAntenna(CompoundTag data) {
    return data.getBoolean(ANTENNA);
  }

  static void append(ITooltip tooltip, CompoundTag data) {
    AntennaChannel.of(data.getString(CHANNEL))
        .ifPresent(channel ->
            tooltip.add(Component.translatable(JadeKeys.CHANNEL, AntennaPlacing.colorOf(channel))));
    tooltip.add(signal(data));
    tooltip.add(
        Component.translatable(data.getBoolean(CHUNK) ? JadeKeys.CHUNK_ON : JadeKeys.CHUNK_OFF));
  }

  /** Never a state the antenna does not have (rule 17). */
  private static Component signal(CompoundTag data) {
    if (!data.getBoolean(ENABLED)) {
      return Component.translatable(AntennaScreenKeys.OFF);
    }
    AntennaSignal.State[] states = AntennaSignal.State.values();
    return switch (states[Mth.clamp(data.getInt(SIGNAL), 0, states.length - 1)]) {
      case LINKED -> Component.translatable(AntennaScreenKeys.LINKED);
      case NO_SIGNAL ->
          Component.translatable(AntennaScreenKeys.NO_SIGNAL_LEFT, data.getInt(MINUTES));
      case STOPPED -> Component.translatable(AntennaScreenKeys.STOPPED_LINE);
    };
  }
}
