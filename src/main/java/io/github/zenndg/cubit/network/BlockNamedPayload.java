package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The answer of the window of the name of a block: the typed name, or nothing to keep the standard
 * one. The server takes it only once, from who placed it, within reach (answer 2422 and rule 20).
 */
public record BlockNamedPayload(BlockPos pos, String name) implements CustomPacketPayload {
  private static final String PATH = "block_named";

  /** Room for spaces around the 50 characters, which the server cuts, and nothing more. */
  private static final int MAX_TYPED = 256;

  public static final Type<BlockNamedPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, BlockNamedPayload> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          BlockNamedPayload::pos,
          ByteBufCodecs.stringUtf8(MAX_TYPED),
          BlockNamedPayload::name,
          BlockNamedPayload::new);

  @Override
  public Type<BlockNamedPayload> type() {
    return TYPE;
  }
}
