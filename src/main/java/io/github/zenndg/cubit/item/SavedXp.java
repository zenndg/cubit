package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.domain.xp.XpSettings;
import io.github.zenndg.cubit.domain.xp.XpState;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The XP of a Cubit in a save, in the item and in the entity, under names that are a contract. An
 * invalid value becomes a safe one and goes to the log (rule 17), and without the names the tank is
 * empty, in the store (answer 1829).
 */
public final class SavedXp {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TANK_FIELD = "xp_tank";
  private static final String MODE_FIELD = "xp_mode";
  private static final String EXCESS_FIELD = "xp_delete_excess";
  private static final String TANK_KEY = "XpTank";
  private static final String MODE_KEY = "XpMode";
  private static final String EXCESS_KEY = "XpDeleteExcess";
  private static final String NEGATIVE = "Negative XP fluid in a save: {}. Using zero.";
  private static final String UNKNOWN = "Unknown XP mode {}. Using store.";

  private static final Codec<Integer> FLUID_CODEC = Codec.INT.xmap(SavedXp::safeFluid, it -> it);
  private static final Codec<XpMode> MODE_CODEC = Codec.STRING.xmap(SavedXp::mode, XpMode::key);

  static final MapCodec<XpState> CODEC =
      RecordCodecBuilder.mapCodec(instance ->
          instance
              .group(
                  FLUID_CODEC.optionalFieldOf(TANK_FIELD, 0).forGetter(XpState::fluid),
                  MODE_CODEC
                      .optionalFieldOf(MODE_FIELD, XpSettings.INITIAL.mode())
                      .forGetter(state -> state.settings().mode()),
                  Codec.BOOL
                      .optionalFieldOf(EXCESS_FIELD, XpSettings.INITIAL.deleteExcess())
                      .forGetter(state -> state.settings().deleteExcess()))
              .apply(
                  instance,
                  (fluid, mode, excess) -> new XpState(fluid, new XpSettings(mode, excess))));

  private SavedXp() {}

  public static void write(CompoundTag tag, XpState state) {
    tag.putInt(TANK_KEY, state.fluid());
    tag.putString(MODE_KEY, state.settings().mode().key());
    tag.putBoolean(EXCESS_KEY, state.settings().deleteExcess());
  }

  public static XpState read(CompoundTag tag) {
    XpMode mode =
        tag.contains(MODE_KEY) ? mode(tag.getString(MODE_KEY)) : XpSettings.INITIAL.mode();
    return new XpState(
        safeFluid(tag.getInt(TANK_KEY)), new XpSettings(mode, tag.getBoolean(EXCESS_KEY)));
  }

  private static int safeFluid(int saved) {
    if (saved < 0) {
      LOGGER.warn(NEGATIVE, saved);
    }
    return Math.max(0, saved);
  }

  private static XpMode mode(String key) {
    return XpMode.fromKey(key)
        .orElseGet(() -> {
          LOGGER.warn(UNKNOWN, key);
          return XpSettings.INITIAL.mode();
        });
  }
}
