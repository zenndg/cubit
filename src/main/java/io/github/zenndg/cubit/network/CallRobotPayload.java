package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * `Follow` of the Tablet on a Cubit in an unloaded chunk. The client only asks, and the server
 * checks the owner, the wait and the search (rule 20 and answers 2463 and 2501).
 */
public record CallRobotPayload(UUID robot) implements CustomPacketPayload {
  private static final String PATH = "call_robot";

  public static final Type<CallRobotPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, CallRobotPayload> STREAM_CODEC =
      StreamCodec.composite(UUIDUtil.STREAM_CODEC, CallRobotPayload::robot, CallRobotPayload::new);

  /** On the client. */
  public static void send(UUID robot) {
    PacketDistributor.sendToServer(new CallRobotPayload(robot));
  }

  @Override
  public Type<CallRobotPayload> type() {
    return TYPE;
  }
}
