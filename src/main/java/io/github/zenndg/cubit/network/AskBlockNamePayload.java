package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The window of the name of a block placed for the first time, with its standard name already in
 * the field (answer 2422). Only the server sends it.
 */
public record AskBlockNamePayload(BlockPos pos, BlockName.Kind kind, String name)
    implements CustomPacketPayload {
  private static final String PATH = "ask_block_name";
  private static final int MAX_NAME = 256;

  public static final Type<AskBlockNamePayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, AskBlockNamePayload> STREAM_CODEC =
      StreamCodec.composite(
          BlockPos.STREAM_CODEC,
          AskBlockNamePayload::pos,
          ByteBufCodecs.VAR_INT.map(AskBlockNamePayload::kindOf, BlockName.Kind::ordinal),
          AskBlockNamePayload::kind,
          ByteBufCodecs.stringUtf8(MAX_NAME),
          AskBlockNamePayload::name,
          AskBlockNamePayload::new);

  /** An unknown kind only changes the title of the window (rule 17). */
  private static BlockName.Kind kindOf(int index) {
    BlockName.Kind[] kinds = BlockName.Kind.values();
    return index >= 0 && index < kinds.length ? kinds[index] : BlockName.Kind.DOCK;
  }

  @Override
  public Type<AskBlockNamePayload> type() {
    return TYPE;
  }
}
