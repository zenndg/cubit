package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.xp.XpEnergyTotal;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

/** The XP of a Dock and in its item, a contract with the saves (answer 1838). */
public record SavedDockXp(int fluid, DockXpMode mode, XpEnergyTotal energyMade) {
  public static final SavedDockXp EMPTY =
      new SavedDockXp(0, DockXpMode.INITIAL, XpEnergyTotal.ZERO);

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TANK_FIELD = "xp_tank";
  private static final String MODE_FIELD = "xp_mode";
  private static final String MADE_FIELD = "xp_energy_made";
  private static final String INVALID = "Invalid {} in the XP of a dock: {}. Using {}.";

  /** An item from before the modes has only the tank (answer 1832). */
  public static final Codec<SavedDockXp> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Codec.INT.optionalFieldOf(TANK_FIELD, 0).forGetter(SavedDockXp::fluid),
                  Codec.STRING
                      .optionalFieldOf(MODE_FIELD, DockXpMode.INITIAL.key())
                      .forGetter(saved -> saved.mode().key()),
                  Codec.LONG
                      .optionalFieldOf(MADE_FIELD, 0L)
                      .forGetter(saved -> saved.energyMade().made().amount()))
              .apply(instance, SavedDockXp::safe));

  public static final StreamCodec<ByteBuf, SavedDockXp> STREAM_CODEC =
      ByteBufCodecs.fromCodec(CODEC);

  /** From what is saved, where an invalid value becomes a safe one, with a warning (rule 17). */
  public static SavedDockXp safe(int fluid, String mode, long energyMade) {
    DockXpMode known =
        DockXpMode.fromKey(mode)
            .orElseGet(() -> {
              LOGGER.warn(INVALID, MODE_FIELD, mode, DockXpMode.INITIAL.key());
              return DockXpMode.INITIAL;
            });
    long made = positive(MADE_FIELD, energyMade);
    return new SavedDockXp(
        (int) positive(TANK_FIELD, fluid), known, new XpEnergyTotal(new Energy(made)));
  }

  private static long positive(String field, long value) {
    if (value < 0) {
      LOGGER.warn(INVALID, field, value, 0);
    }
    return Math.max(0, value);
  }
}
