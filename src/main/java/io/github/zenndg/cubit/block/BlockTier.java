package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The tier of a block and its numbers, kept in the block as `Tier`. A block without it, placed
 * before the tiers, is a Base one (answer 1373).
 */
final class BlockTier<S> {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String TIER_KEY = "Tier";
  private static final String UNKNOWN_TIER = "The block at {} has an unknown tier {}. Using Base.";

  private final Function<RobotTier, S> specs;
  private RobotTier tier = RobotTier.BASE;

  BlockTier(Function<RobotTier, S> specs) {
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
