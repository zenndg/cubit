package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The tier of a block and its numbers, kept in the block as `Tier` and in its item as a component.
 * A block without it, placed before the tiers, is a Base one (answer 1373).
 */
final class BlockTier<S> {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TIER_KEY = "Tier";
  private static final String UNKNOWN_TIER = "The block at {} has an unknown tier {}. Using Base.";

  private final Supplier<DataComponentType<RobotTier>> component;
  private final Function<RobotTier, S> specs;
  private RobotTier tier = RobotTier.BASE;

  BlockTier(Supplier<DataComponentType<RobotTier>> component, Function<RobotTier, S> specs) {
    this.component = component;
    this.specs = specs;
  }

  RobotTier tier() {
    return tier;
  }

  void set(RobotTier tier) {
    this.tier = tier;
  }

  S spec() {
    return specs.apply(tier);
  }

  void save(CompoundTag tag) {
    tag.putString(TIER_KEY, tier.key());
  }

  void load(CompoundTag tag, BlockPos pos) {
    tier = read(tag.getString(TIER_KEY), pos);
  }

  void collect(DataComponentMap.Builder components) {
    components.set(component.get(), tier);
  }

  private static RobotTier read(String key, BlockPos pos) {
    if (key.isEmpty()) {
      return RobotTier.BASE;
    }
    return RobotTier.fromKey(key)
        .orElseGet(() -> {
          LOGGER.warn(UNKNOWN_TIER, pos, key);
          return RobotTier.BASE;
        });
  }
}
