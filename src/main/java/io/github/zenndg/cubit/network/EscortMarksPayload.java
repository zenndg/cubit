package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.CubitMod;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * The ones the Galactic Patrollers of the player escort, by their entity ids, for the arrows of the
 * Escort app, which follow them as they move (answer 1661). Empty when the Escort app is not open.
 */
public record EscortMarksPayload(List<Integer> entityIds) implements CustomPacketPayload {
  private static final String PATH = "escort_marks";

  public static final EscortMarksPayload NONE = new EscortMarksPayload(List.of());

  public static final Type<EscortMarksPayload> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PATH));
  public static final StreamCodec<RegistryFriendlyByteBuf, EscortMarksPayload> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()),
          EscortMarksPayload::entityIds,
          EscortMarksPayload::new);

  public EscortMarksPayload {
    entityIds = List.copyOf(entityIds);
  }

  @Override
  public Type<EscortMarksPayload> type() {
    return TYPE;
  }
}
