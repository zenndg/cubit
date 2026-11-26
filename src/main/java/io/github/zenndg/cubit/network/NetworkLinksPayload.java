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
 * What the Network app shows over the blocks of the chosen Cubit: its antennas, with the channel,
 * its Docks and chests, and the Docks that only get energy (answers 2592 and 2595).
 */
public record NetworkLinksPayload(List<Mark> marks) implements CustomPacketPayload {
  private static final String PATH = "network_links";

  /** What an arrow is over. */
  public enum Kind {
    ANTENNA,
    DOCK,
    CHEST,
    FED
  }

  /**
   * One arrow: the block, what it is, a name, a detail and a number. An antenna sends its name and
   * the key of its channel, a Dock its name and the name of the Cubit, a chest the name of the
   * Cubit and its free slots, and a Dock with only energy its name. The arrow goes over its height
   * in blocks, as the floating name.
   */
  public record Mark(BlockPos pos, Kind kind, String name, String detail, int number, int tall) {
    static final StreamCodec<RegistryFriendlyByteBuf, Mark> CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            Mark::pos,
            ByteBufCodecs.VAR_INT.map(index -> Kind.values()[index], Kind::ordinal),
            Mark::kind,
            ByteBufCodecs.STRING_UTF8,
            Mark::name,
            ByteBufCodecs.STRING_UTF8,
            Mark::detail,
            ByteBufCodecs.VAR_INT,
            Mark::number,
            ByteBufCodecs.VAR_INT,
            Mark::tall,
            Mark::new);
  }

  public static final NetworkLinksPayload NONE = new NetworkLinksPayload(List.of());

  public static final Type<NetworkLinksPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, NetworkLinksPayload> STREAM_CODEC =
      StreamCodec.composite(
          Mark.CODEC.apply(ByteBufCodecs.list()),
          NetworkLinksPayload::marks,
          NetworkLinksPayload::new);

  public NetworkLinksPayload {
    marks = List.copyOf(marks);
  }

  @Override
  public Type<NetworkLinksPayload> type() {
    return TYPE;
  }
}
