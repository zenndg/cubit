package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.LinkSpot;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** The blocks of the Network app between the game and the domain, by dimension and place. */
public final class LinkSpots {
  private LinkSpots() {}

  public static LinkSpot of(Level level, BlockPos pos) {
    return new LinkSpot(dimensionOf(level), pos.getX(), pos.getY(), pos.getZ());
  }

  public static BlockPos pos(LinkSpot spot) {
    return new BlockPos(spot.x(), spot.y(), spot.z());
  }

  /** A dimension a save does not know gives nothing (rule 17). */
  public static Optional<GlobalPos> global(LinkSpot spot) {
    ResourceLocation id = ResourceLocation.tryParse(spot.dimension());
    return Optional.ofNullable(id)
        .map(found -> GlobalPos.of(ResourceKey.create(Registries.DIMENSION, found), pos(spot)));
  }

  public static boolean isIn(LinkSpot spot, Level level) {
    return spot.dimension().equals(dimensionOf(level));
  }

  public static String dimensionOf(Level level) {
    return level.dimension().location().toString();
  }
}
