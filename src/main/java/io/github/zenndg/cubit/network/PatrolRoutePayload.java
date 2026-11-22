package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The route of the Galactic Patroller chosen in the Tablet, in the dimension of the player, for the
 * numbered arrows and the dashed line of the Mark app: each block and its number in the order of
 * the route (answers 1580 and 1612). Empty when the Mark app is not open.
 */
public record PatrolRoutePayload(List<Point> points) implements CustomPacketPayload {
  private static final String PATH = "patrol_route";

  /** One point of the route. */
  public record Point(BlockPos pos, int number) {
    static final StreamCodec<RegistryFriendlyByteBuf, Point> CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, Point::pos, ByteBufCodecs.VAR_INT, Point::number, Point::new);
  }

  public static final PatrolRoutePayload NONE = new PatrolRoutePayload(List.of());

  public static final Type<PatrolRoutePayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, PatrolRoutePayload> STREAM_CODEC =
      StreamCodec.composite(
          Point.CODEC.apply(ByteBufCodecs.list()),
          PatrolRoutePayload::points,
          PatrolRoutePayload::new);

  public PatrolRoutePayload {
    points = List.copyOf(points);
  }

  @Override
  public Type<PatrolRoutePayload> type() {
    return TYPE;
  }
}
