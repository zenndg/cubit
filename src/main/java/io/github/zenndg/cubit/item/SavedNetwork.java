package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

/**
 * What a block of the Network keeps in its item, a contract with the saves (answers 2404 and 2405).
 * The Solar Relay only keeps its energy. Without a reserve, the one of the config applies.
 */
public record SavedNetwork(
    RobotTier tier,
    long energy,
    List<SavedBigStack> items,
    Optional<UpsReserve> ups,
    boolean chunkLoading) {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TIER_FIELD = "tier";
  private static final String ENERGY_FIELD = "energy";
  private static final String ITEMS_FIELD = "items";
  private static final String UPS_FIELD = "ups";
  private static final String CHUNK_FIELD = "chunk_loading";
  private static final String BROKEN_ITEM = "Skipping a broken item of a Network Server: {}";
  private static final String UNKNOWN_TIER = "Unknown tier {} in a Network item. Using Base.";

  public static final SavedNetwork EMPTY = onlyEnergy(0);

  private static final Codec<RobotTier> TIER =
      Codec.STRING.xmap(SavedNetwork::tierOf, RobotTier::key);
  private static final Codec<UpsReserve> UPS = Codec.INT.xmap(UpsReserve::of, UpsReserve::percent);

  /** One broken item never takes the others with it (rule 17). */
  private static final Codec<List<SavedBigStack>> ITEMS =
      SavedBigStack.CODEC
          .listOf()
          .promotePartial(error -> LOGGER.warn(BROKEN_ITEM, error))
          .xmap(List::copyOf, items -> items);

  public static final Codec<SavedNetwork> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  TIER.optionalFieldOf(TIER_FIELD, RobotTier.BASE).forGetter(SavedNetwork::tier),
                  Codec.LONG.optionalFieldOf(ENERGY_FIELD, 0L).forGetter(SavedNetwork::energy),
                  ITEMS.optionalFieldOf(ITEMS_FIELD, List.of()).forGetter(SavedNetwork::items),
                  UPS.optionalFieldOf(UPS_FIELD).forGetter(SavedNetwork::ups),
                  Codec.BOOL
                      .optionalFieldOf(CHUNK_FIELD, true)
                      .forGetter(SavedNetwork::chunkLoading))
              .apply(instance, SavedNetwork::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, SavedNetwork> STREAM_CODEC =
      ByteBufCodecs.fromCodecWithRegistries(CODEC);

  public static SavedNetwork onlyEnergy(long energy) {
    return new SavedNetwork(RobotTier.BASE, energy, List.of(), Optional.empty(), true);
  }

  /** What the item keeps, or a new block. */
  public static SavedNetwork of(ItemStack stack) {
    return stack.getOrDefault(CubitDataComponents.NETWORK_STATE.get(), EMPTY);
  }

  public boolean holdsAnything() {
    return energy > 0 || !items.isEmpty();
  }

  private static RobotTier tierOf(String key) {
    return RobotTier.fromKey(key)
        .orElseGet(() -> {
          LOGGER.warn(UNKNOWN_TIER, key);
          return RobotTier.BASE;
        });
  }
}
