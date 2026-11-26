package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.InputMeter;
import io.github.zenndg.cubit.domain.network.ServerFront;
import io.github.zenndg.cubit.domain.network.ServerLook;
import io.github.zenndg.cubit.domain.network.ServerPower;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.network.UpsWatch;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * What a Network Server shows and plays: the server decides it, tells the clients when it changes
 * and plays the sounds, so everyone near hears them (answers 2338 and 2339). The first tick after a
 * load only looks, so nothing plays then (Delivery 3, detail 12).
 */
final class ServerShow {
  private static final String LOOK_KEY = "Look";
  private static final String POWERED_KEY = "Powered";
  private static final String FLOWING_KEY = "Flowing";
  private static final String LINKED_KEY = "Linked";
  private static final String ENERGY_KEY = "Energy";
  private static final String ITEMS_KEY = "Items";
  private static final String CHANNELS_KEY = "Channels";
  private static final float VOLUME = 1f;
  private static final float PITCH = 1f;

  private final InputMeter flow = new InputMeter();
  private final ServerClientView view = new ServerClientView();
  private Energy passed = Energy.ZERO;
  private Energy before;
  private UpsWatch watch = UpsWatch.OUTSIDE;
  private ServerLook look = ServerLook.OFF;

  ServerClientView view() {
    return view;
  }

  /** Energy that came in or went to a bank in this tick, for the fans (answer 2375). */
  void passes(Energy energy) {
    passed = passed.plus(energy);
  }

  /** At the end of a tick, true when the clients have something new to show. */
  boolean tick(Level level, BlockPos pos, Reading now) {
    flow.record(passed);
    passed = Energy.ZERO;
    UpsWatch.Step ups = watch.next(now.ups(), now.energy(), now.spec().capacity());
    watch = ups.watch();
    if (before != null) {
      playPower(level, pos, ServerPower.between(before, now.energy()));
      if (ups.warns()) {
        play(level, pos, CubitSounds.SERVER_UPS_WARNING.get());
      }
    }
    before = now.energy();
    ServerLook next = lookOf(now);
    boolean changed = !next.equals(look);
    look = next;
    return changed;
  }

  private ServerLook lookOf(Reading now) {
    ServerSpec spec = now.spec();
    return new ServerLook(
        !now.energy().isZero(),
        !flow.perTick().isZero(),
        now.linked(),
        new ServerLook.Columns(
            ServerFront.energyBars(
                now.energy().amount(), spec.capacity().amount(), now.upkeep().amount()),
            ServerFront.bars(now.usedSlots(), spec.storageSlots())),
        spec.channels());
  }

  private static void playPower(Level level, BlockPos pos, ServerPower power) {
    switch (power) {
      case STARTS -> play(level, pos, CubitSounds.SERVER_STARTUP.get());
      case FALLS -> play(level, pos, CubitSounds.SERVER_POWER_LOSS.get());
      case SAME -> {}
    }
  }

  static void play(Level level, BlockPos pos, SoundEvent sound) {
    level.playSound(null, pos.above(), sound, SoundSource.BLOCKS, VOLUME, PITCH);
  }

  /** Before the first tick there is nothing to show, and a client must not see it start up. */
  void write(CompoundTag tag) {
    if (before == null) {
      return;
    }
    CompoundTag shown = new CompoundTag();
    shown.putBoolean(POWERED_KEY, look.powered());
    shown.putBoolean(FLOWING_KEY, look.flowing());
    shown.putBoolean(LINKED_KEY, look.linked());
    shown.putInt(ENERGY_KEY, look.columns().energy());
    shown.putInt(ITEMS_KEY, look.columns().items());
    shown.putInt(CHANNELS_KEY, look.channels());
    tag.put(LOOK_KEY, shown);
  }

  /** Only what came from the server, and never a number the front does not have (rule 17). */
  void read(CompoundTag tag) {
    if (!tag.contains(LOOK_KEY)) {
      return;
    }
    CompoundTag shown = tag.getCompound(LOOK_KEY);
    view.receive(
        new ServerLook(
            shown.getBoolean(POWERED_KEY),
            shown.getBoolean(FLOWING_KEY),
            shown.getBoolean(LINKED_KEY),
            new ServerLook.Columns(bars(shown, ENERGY_KEY), bars(shown, ITEMS_KEY)),
            Mth.clamp(shown.getInt(CHANNELS_KEY), 0, ServerSpec.MAX_CHANNELS)));
  }

  private static int bars(CompoundTag tag, String key) {
    return Mth.clamp(tag.getInt(key), 0, ServerFront.BARS);
  }

  /** What the server has at the end of a tick. */
  record Reading(
      Energy energy,
      UpsReserve ups,
      ServerSpec spec,
      int usedSlots,
      boolean linked,
      Energy upkeep) {}
}
