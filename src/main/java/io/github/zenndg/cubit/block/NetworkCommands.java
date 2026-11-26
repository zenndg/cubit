package io.github.zenndg.cubit.block;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.registry.CubitBlocks;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * {@code /cubit network list} shows the chunks the Network holds or keeps waiting, by player, and
 * {@code /cubit network release} lets go of all of them or of the ones of a player, switching the
 * chunk of each block off, so it does not take it back (answers 2286 and 2578). Only operators.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class NetworkCommands {
  public static final String NONE_KEY = "command.cubit.network.none";
  public static final String TITLE_KEY = "command.cubit.network.title";
  public static final String PLAYER_KEY = "command.cubit.network.player";
  public static final String ENTRY_KEY = "command.cubit.network.entry";
  public static final String HOLDING_KEY = "command.cubit.network.holding";
  public static final String WAITING_KEY = "command.cubit.network.waiting";
  public static final String RELEASED_KEY = "command.cubit.network.released";

  private static final String ROOT_LITERAL = "cubit";
  private static final String NETWORK_LITERAL = "network";
  private static final String LIST_LITERAL = "list";
  private static final String RELEASE_LITERAL = "release";
  private static final String PLAYER_ARGUMENT = "player";
  private static final int OPERATOR_LEVEL = 2;
  private static final int NONE = 0;
  private static final int ID_DIGITS = 8;

  private NetworkCommands() {}

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    event
        .getDispatcher()
        .register(
            Commands.literal(ROOT_LITERAL)
                .then(
                    Commands.literal(NETWORK_LITERAL)
                        .requires(source -> source.hasPermission(OPERATOR_LEVEL))
                        .then(
                            Commands.literal(LIST_LITERAL)
                                .executes(context -> list(context.getSource())))
                        .then(
                            Commands.literal(RELEASE_LITERAL)
                                .executes(context -> release(context.getSource(), Optional.empty()))
                                .then(
                                    Commands.argument(
                                            PLAYER_ARGUMENT, GameProfileArgument.gameProfile())
                                        .executes(NetworkCommands::releaseOf)))));
  }

  private static int list(CommandSourceStack source) {
    MinecraftServer server = source.getServer();
    NetworkData data = NetworkData.of(server);
    List<ChunkRoster.Entry<GlobalPos>> entries = data.chunks().entries();
    if (entries.isEmpty()) {
      source.sendSuccess(() -> Component.translatable(NONE_KEY), false);
      return NONE;
    }
    source.sendSuccess(() -> Component.translatable(TITLE_KEY, entries.size()), false);
    Map<UUID, List<ChunkRoster.Entry<GlobalPos>>> byOwner =
        entries.stream().collect(Collectors.groupingBy(ChunkRoster.Entry::owner));
    byOwner.forEach((owner, held) -> {
      source.sendSuccess(
          () -> Component.translatable(PLAYER_KEY, nameOf(server, owner), held.size()), false);
      held.forEach(entry -> source.sendSuccess(() -> line(data, entry), false));
    });
    return entries.size();
  }

  private static Component line(NetworkData data, ChunkRoster.Entry<GlobalPos> entry) {
    GlobalPos place = entry.place();
    return Component.translatable(
            ENTRY_KEY,
            kindOf(data.roster(), place),
            place.dimension().location().toString(),
            place.pos().getX(),
            place.pos().getY(),
            place.pos().getZ(),
            Component.translatable(entry.waiting() ? WAITING_KEY : HOLDING_KEY))
        .withStyle(ChatFormatting.GRAY);
  }

  /** The kind of the block, by the data of the world, which knows it also unloaded. */
  private static Component kindOf(NetworkRoster<GlobalPos> roster, GlobalPos place) {
    boolean antenna = roster.antennas().stream().anyMatch(each -> each.place().equals(place));
    return antenna
        ? CubitBlocks.SIGNAL_ANTENNA.get().getName()
        : CubitBlocks.NETWORK_SERVER.get().getName();
  }

  private static Component nameOf(MinecraftServer server, UUID owner) {
    String name =
        Optional.ofNullable(server.getProfileCache())
            .flatMap(cache -> cache.get(owner))
            .map(GameProfile::getName)
            .orElseGet(() -> owner.toString().substring(0, ID_DIGITS).toLowerCase(Locale.ROOT));
    return Component.literal(name).withStyle(ChatFormatting.AQUA);
  }

  private static int releaseOf(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    Collection<GameProfile> profiles =
        GameProfileArgument.getGameProfiles(context, PLAYER_ARGUMENT);
    int released = 0;
    for (GameProfile profile : profiles) {
      released += release(context.getSource(), Optional.of(profile.getId()));
    }
    return released;
  }

  private static int release(CommandSourceStack source, Optional<UUID> owner) {
    int count = release(source.getServer(), owner);
    source.sendSuccess(() -> Component.translatable(RELEASED_KEY, count), true);
    return count;
  }

  /**
   * Each chunk of a player, or of all, is let go of, and the switch of its block goes off, now or
   * when it loads. How many were released.
   */
  public static int release(MinecraftServer server, Optional<UUID> owner) {
    NetworkData data = NetworkData.of(server);
    List<GlobalPos> places = data.chunks().placesOf(owner);
    for (GlobalPos place : places) {
      data.chunks().release(place);
      NetworkTickets.set(server, place, false);
      switchOff(server, data, place);
    }
    data.setDirty();
    return places.size();
  }

  private static void switchOff(MinecraftServer server, NetworkData data, GlobalPos place) {
    ServerLevel level = server.getLevel(place.dimension());
    if (level != null
        && level.isLoaded(place.pos())
        && level.getBlockEntity(place.pos()) instanceof NetworkBlockEntity block) {
      block.switchChunkOff();
    } else {
      data.markReleased(place);
    }
  }
}
