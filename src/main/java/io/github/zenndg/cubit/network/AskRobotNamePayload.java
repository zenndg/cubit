package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The window of the name of a Cubit placed for the first time, with its standard name already in
 * the field (answers 1875 and 1879). Only the server sends it.
 */
public record AskRobotNamePayload(UUID robot, String name) implements CustomPacketPayload {
  private static final String PATH = "ask_robot_name";

  /** Room for spaces around the 50 characters, which the server cuts, and nothing more. */
  private static final int MAX_TYPED = 256;

  public static final Type<AskRobotNamePayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, AskRobotNamePayload> STREAM_CODEC =
      StreamCodec.composite(
          UUIDUtil.STREAM_CODEC,
          AskRobotNamePayload::robot,
          ByteBufCodecs.stringUtf8(MAX_TYPED),
          AskRobotNamePayload::name,
          AskRobotNamePayload::new);

  @Override
  public Type<AskRobotNamePayload> type() {
    return TYPE;
  }
}
