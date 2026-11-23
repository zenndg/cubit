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
 * The answer of the window of the name: the typed name, or nothing to keep the standard one. The
 * server takes it only once, from who placed the Cubit, within reach (answers 1879 and 1880, rule
 * 20).
 */
public record RobotNamedPayload(UUID robot, String name) implements CustomPacketPayload {
  private static final String PATH = "robot_named";

  /** Room for spaces around the 50 characters, which the server cuts, and nothing more. */
  private static final int MAX_TYPED = 256;

  public static final Type<RobotNamedPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, RobotNamedPayload> STREAM_CODEC =
      StreamCodec.composite(
          UUIDUtil.STREAM_CODEC,
          RobotNamedPayload::robot,
          ByteBufCodecs.stringUtf8(MAX_TYPED),
          RobotNamedPayload::name,
          RobotNamedPayload::new);

  @Override
  public Type<RobotNamedPayload> type() {
    return TYPE;
  }
}
