package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * What the client of the owner draws with the Area app: the drawing halfway, with the limits of its
 * Cubit, and the area of the chosen Cubit (answers 2240 and 2358 to 2360). The server sends it on
 * each change, never on every tick.
 */
public record AreaMarkPayload(Optional<Drawing> drawing, Optional<Area> area)
    implements CustomPacketPayload {
  private static final String PATH = "area_mark";

  /** The base and the height the tier allows, and the numbers of the drawing. */
  public record Limits(int size, int height, int minPoints, int maxPoints, int reach) {
    static final StreamCodec<RegistryFriendlyByteBuf, Limits> CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Limits::size,
            ByteBufCodecs.VAR_INT,
            Limits::height,
            ByteBufCodecs.VAR_INT,
            Limits::minPoints,
            ByteBufCodecs.VAR_INT,
            Limits::maxPoints,
            ByteBufCodecs.VAR_INT,
            Limits::reach,
            Limits::new);
  }

  /** The marked points, in order, and whether they close. */
  public record Drawing(List<BlockPos> points, boolean closed, Limits limits) {
    static final StreamCodec<RegistryFriendlyByteBuf, Drawing> CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Drawing::points,
            ByteBufCodecs.BOOL,
            Drawing::closed,
            Limits.CODEC,
            Drawing::limits,
            Drawing::new);

    public Drawing {
      points = List.copyOf(points);
    }
  }

  /** The corners of an area, at its lowest blocks, and its highest height. */
  public record Area(List<BlockPos> corners, int high) {
    static final StreamCodec<RegistryFriendlyByteBuf, Area> CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Area::corners,
            ByteBufCodecs.VAR_INT,
            Area::high,
            Area::new);

    public Area {
      corners = List.copyOf(corners);
    }
  }

  public static final AreaMarkPayload NOTHING =
      new AreaMarkPayload(Optional.empty(), Optional.empty());

  public static final Type<AreaMarkPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, AreaMarkPayload> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.optional(Drawing.CODEC),
          AreaMarkPayload::drawing,
          ByteBufCodecs.optional(Area.CODEC),
          AreaMarkPayload::area,
          AreaMarkPayload::new);

  @Override
  public Type<AreaMarkPayload> type() {
    return TYPE;
  }
}
