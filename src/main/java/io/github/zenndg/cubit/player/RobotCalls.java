package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.CallResult;
import io.github.zenndg.cubit.domain.behavior.CallSearch;
import io.github.zenndg.cubit.domain.behavior.CallSpec;
import io.github.zenndg.cubit.entity.RobotArrival;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotFollowCall;
import io.github.zenndg.cubit.entity.RobotRespawn;
import io.github.zenndg.cubit.entity.RobotTablet;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Calls a Cubit of a player from anywhere, for `Follow`, for the command and after a respawn. One
 * in an unloaded chunk is searched where its snapshot says: only its chunk loads, for some seconds,
 * with a ticket the game forgets by itself and never saves. The Cubit found there moves, and none
 * is ever made from the snapshot (rule 22 and answers 2456, 2458, 2464 and 2485).
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class RobotCalls {
  private static final String KEY = "notice.cubit.call.";
  private static final String TICKET_NAME = "cubit:call";

  /** Longer than any search, so a ticket never outlives a server that forgot to remove it. */
  private static final int TICKET_LIFE =
      (CallSpec.MAX_TIMEOUT_SECONDS + 1) * CallSpec.TICKS_PER_SECOND;

  /** The chunk itself, loaded but without ticking. */
  private static final int ONLY_THE_CHUNK = 0;

  private static final TicketType<UUID> TICKET =
      TicketType.create(TICKET_NAME, UUID::compareTo, TICKET_LIFE);
  private static final Map<UUID, Place> SEARCHES = new HashMap<>();
  private static final Map<UUID, Long> LAST_CALLS = new HashMap<>();

  /** A search of a player, and the chunk it holds. */
  private record Place(CallSearch search, ServerLevel level, ChunkPos chunk) {
    Place tick() {
      return new Place(search.tick(), level, chunk);
    }
  }

  private RobotCalls() {}

  public static String keyOf(CallResult result) {
    return KEY + result.name().toLowerCase(Locale.ROOT);
  }

  /** The text of a result, with the seconds to wait where it has them. */
  public static Component textOf(ServerPlayer player, CallResult result) {
    return Component.translatable(keyOf(result), secondsLeft(player));
  }

  /** The screen of the Tablet shows no text of its own, so the owner reads a notice. */
  public static CallResult callFromTablet(ServerPlayer player, UUID robot) {
    CallResult result = call(player, robot);
    if (!result.comes()) {
      CubitNetwork.sendNotice(player, textOf(player, result));
    }
    return result;
  }

  public static CallResult call(ServerPlayer player, UUID robot) {
    if (SEARCHES.containsKey(player.getUUID())) {
      return CallResult.BUSY;
    }
    if (secondsLeft(player) > 0) {
      return CallResult.WAIT;
    }
    CallResult result =
        RobotTablet.find(player, robot)
            .map(found -> callLoaded(player, found))
            .orElseGet(() -> search(player, robot, CallSearch.Cause.CALL));
    if (result.comes()) {
      LAST_CALLS.put(player.getUUID(), now(player.server));
    }
    return result;
  }

  private static CallResult callLoaded(ServerPlayer player, RobotEntity robot) {
    return new RobotFollowCall(robot).goal().ready()
        ? RobotArrival.called(robot, player)
        : CallResult.HERE;
  }

  /** The Cubit that followed, after a respawn, without the wait between calls (answer 2460). */
  static void searchAfterRespawn(ServerPlayer owner, UUID robot) {
    cancel(owner.getUUID());
    search(owner, robot, CallSearch.Cause.RESPAWN);
  }

  private static CallResult search(ServerPlayer player, UUID robot, CallSearch.Cause cause) {
    Optional<RobotSnapshot> seen =
        RobotSnapshots.of(player.server).of(player.getUUID()).stream()
            .filter(snapshot -> snapshot.robot().equals(robot))
            .findFirst();
    ServerLevel level =
        seen.map(snapshot -> player.server.getLevel(snapshot.dimension())).orElse(null);
    if (level == null) {
      return CallResult.NOT_FOUND;
    }
    CallSearch search = new CallSearch(robot, cause, CubitMod.CONFIG.calls().timeoutTicks());
    Place place = new Place(search, level, new ChunkPos(seen.get().pos()));
    SEARCHES.put(player.getUUID(), place);
    level.getChunkSource().addRegionTicket(TICKET, place.chunk(), ONLY_THE_CHUNK, player.getUUID());
    return CallResult.SEARCHING;
  }

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    for (UUID id : List.copyOf(SEARCHES.keySet())) {
      advance(event.getServer(), id);
    }
  }

  private static void advance(MinecraftServer server, UUID id) {
    ServerPlayer player = server.getPlayerList().getPlayer(id);
    if (player == null || player.isDeadOrDying()) {
      cancel(id);
      return;
    }
    Place place = SEARCHES.get(id).tick();
    Optional<RobotEntity> found = RobotTablet.find(player, place.search().robot());
    if (found.isPresent()) {
      cancel(id);
      arrive(player, found.get(), place.search().cause());
    } else if (place.search().isOver()) {
      cancel(id);
      CubitNetwork.sendNotice(player, textOf(player, CallResult.NOT_FOUND));
    } else {
      SEARCHES.put(id, place);
    }
  }

  private static void arrive(ServerPlayer player, RobotEntity robot, CallSearch.Cause cause) {
    if (cause == CallSearch.Cause.RESPAWN) {
      RobotRespawn.bringTo(player, robot);
      RobotRespawn.tellDeathPoint(player);
      return;
    }
    CallResult result = RobotArrival.called(robot, player);
    if (!result.comes()) {
      CubitNetwork.sendNotice(player, textOf(player, result));
    }
  }

  /** The search of a player ends, and its chunk may unload. */
  private static void cancel(UUID player) {
    Place place = SEARCHES.remove(player);
    if (place != null) {
      place
          .level()
          .getChunkSource()
          .removeRegionTicket(TICKET, place.chunk(), ONLY_THE_CHUNK, player);
    }
  }

  private static int secondsLeft(ServerPlayer player) {
    Long last = LAST_CALLS.get(player.getUUID());
    return last == null ? 0 : CubitMod.CONFIG.calls().secondsLeft(last, now(player.server));
  }

  private static long now(MinecraftServer server) {
    return server.overworld().getGameTime();
  }

  @SubscribeEvent
  public static void onDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      cancel(player.getUUID());
    }
  }

  @SubscribeEvent
  public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    cancel(event.getEntity().getUUID());
  }

  /** The next world of the same game starts clean. */
  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    SEARCHES.clear();
    LAST_CALLS.clear();
  }
}
