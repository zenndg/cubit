package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The name the player typed for a block, on its screen. The server names only the block of the
 * screen the player has open, which only its owner opens within reach (answer 1868 and rule 20).
 */
public record RenameBlockPayload(int containerId, String name) implements CustomPacketPayload {
  private static final String PATH = "rename_block";

  /** Room for spaces around the 50 characters, which the server cuts, and nothing more. */
  private static final int MAX_TYPED = 256;

  public static final Type<RenameBlockPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, RenameBlockPayload> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          RenameBlockPayload::containerId,
          ByteBufCodecs.stringUtf8(MAX_TYPED),
          RenameBlockPayload::name,
          RenameBlockPayload::new);

  @Override
  public Type<RenameBlockPayload> type() {
    return TYPE;
  }
}
