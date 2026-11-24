package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;

/** The XP of a Dock in its item, a contract with the saves (answer 1838). */
public record SavedDockXp(int fluid) {
  public static final SavedDockXp EMPTY = new SavedDockXp(0);

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TANK_FIELD = "xp_tank";
  private static final String NEGATIVE = "Negative XP fluid in a dock: {}. Using zero.";

  public static final Codec<SavedDockXp> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(Codec.INT.optionalFieldOf(TANK_FIELD, 0).forGetter(SavedDockXp::fluid))
              .apply(instance, SavedDockXp::new));
  public static final StreamCodec<ByteBuf, SavedDockXp> STREAM_CODEC =
      ByteBufCodecs.fromCodec(CODEC);

  /** An invalid amount becomes zero, with a warning (rule 17). */
  public SavedDockXp {
    if (fluid < 0) {
      LOGGER.warn(NEGATIVE, fluid);
      fluid = 0;
    }
  }
}
