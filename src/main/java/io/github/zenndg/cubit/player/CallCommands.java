package io.github.zenndg.cubit.player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.domain.behavior.RobotScreenStatus;
import io.github.zenndg.cubit.domain.robot.RobotListing;
import io.github.zenndg.cubit.domain.robot.RobotListings;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.menu.RobotStatusTexts;
import io.github.zenndg.cubit.menu.ScreenEnums;
import io.github.zenndg.cubit.menu.TabletDimensions;
import io.github.zenndg.cubit.network.RobotListPayload;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * {@code /cubit list} shows the Cubits of the player, and {@code /cubit follow} calls one by the
 * first eight characters of its id. Each player only reaches their own, so they need no permission
 * (answers 2453 to 2455).
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class CallCommands {
  public static final String TITLE_KEY = "command.cubit.list.title";
  public static final String NONE_KEY = "command.cubit.list.none";
  public static final String ENTRY_KEY = "command.cubit.list.entry";
  public static final String WHERE_KEY = "command.cubit.list.where";
  public static final String UNLOADED_KEY = "command.cubit.list.unloaded";
  public static final String NO_MATCH_KEY = "command.cubit.follow.no_match";

  private static final String ROOT_LITERAL = "cubit";
  private static final String LIST_LITERAL = "list";
  private static final String FOLLOW_LITERAL = "follow";
  private static final String ID_ARGUMENT = "id";
  private static final String FOLLOW_COMMAND = "/cubit follow ";
  private static final int NONE = 0;

  private CallCommands() {}

  @SubscribeEvent
  public static void onRegisterCommands(RegisterCommandsEvent event) {
    event
        .getDispatcher()
        .register(
            Commands.literal(ROOT_LITERAL)
                .then(Commands.literal(LIST_LITERAL).executes(context -> list(context.getSource())))
                .then(
                    Commands.literal(FOLLOW_LITERAL)
                        .then(
                            Commands.argument(ID_ARGUMENT, StringArgumentType.word())
                                .suggests(CallCommands::suggest)
                                .executes(CallCommands::follow))));
  }

  private static int list(CommandSourceStack source) throws CommandSyntaxException {
    ServerPlayer player = source.getPlayerOrException();
    List<RobotListPayload.Entry> entries = entries(player);
    if (entries.isEmpty()) {
      source.sendFailure(Component.translatable(NONE_KEY));
      return NONE;
    }
    source.sendSuccess(() -> Component.translatable(TITLE_KEY, entries.size()), false);
    for (RobotListPayload.Entry entry : entries) {
      source.sendSuccess(() -> line(entry), false);
      source.sendSuccess(() -> where(entry), false);
    }
    return entries.size();
  }

  private static MutableComponent line(RobotListPayload.Entry entry) {
    String id = RobotName.shortId(entry.robot());
    return Component.translatable(
            ENTRY_KEY, Component.literal(id).withStyle(ChatFormatting.AQUA), nameOf(entry))
        .withStyle(style ->
            style.withClickEvent(
                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, FOLLOW_COMMAND + id)));
  }

  private static MutableComponent where(RobotListPayload.Entry entry) {
    Component state =
        entry.where().loaded()
            ? RobotStatusTexts.status(
                ScreenEnums.of(RobotScreenStatus.values(), entry.status()),
                entry.work().jobValue(),
                entry.work().reasonValue())
            : Component.translatable(UNLOADED_KEY);
    return Component.translatable(WHERE_KEY, state, TabletDimensions.name(entry.dimension()))
        .withStyle(ChatFormatting.GRAY);
  }

  private static Component nameOf(RobotListPayload.Entry entry) {
    return Component.literal(listing(entry).name());
  }

  private static int follow(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayerOrException();
    String id = StringArgumentType.getString(context, ID_ARGUMENT);
    Optional<RobotListing> called = RobotListings.called(listings(player), id);
    if (called.isEmpty()) {
      source.sendFailure(Component.translatable(NO_MATCH_KEY, id));
      return NONE;
    }
    CallResult result = RobotCalls.call(player, called.get().id());
    Component text = RobotCalls.textOf(player, result);
    if (!result.comes()) {
      source.sendFailure(text);
      return NONE;
    }
    source.sendSuccess(() -> text, false);
    return Command.SINGLE_SUCCESS;
  }

  private static CompletableFuture<Suggestions> suggest(
      CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    ServerPlayer player = context.getSource().getPlayer();
    if (player != null) {
      entries(player).stream()
          .filter(
              entry -> RobotName.shortId(entry.robot()).startsWith(builder.getRemainingLowerCase()))
          .forEach(entry -> builder.suggest(RobotName.shortId(entry.robot()), nameOf(entry)));
    }
    return builder.buildFuture();
  }

  /** The list of the Tablet, by name. */
  private static List<RobotListPayload.Entry> entries(ServerPlayer player) {
    List<RobotListPayload.Entry> all = TabletSelection.entriesFor(player);
    return RobotListings.byName(all.stream().map(CallCommands::listing).toList()).stream()
        .flatMap(listing -> all.stream().filter(entry -> entry.robot().equals(listing.id())))
        .toList();
  }

  private static List<RobotListing> listings(ServerPlayer player) {
    return TabletSelection.entriesFor(player).stream().map(CallCommands::listing).toList();
  }

  /** A Cubit without a name goes by its standard one, with the same eight characters. */
  private static RobotListing listing(RobotListPayload.Entry entry) {
    String name = entry.name().isEmpty() ? RobotName.standard(entry.robot()).value() : entry.name();
    return new RobotListing(
        entry.robot(),
        name,
        entry.where().following(),
        true,
        entry.where().loaded(),
        entry.where().distance());
  }
}
