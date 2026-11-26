package io.github.zenndg.cubit.network;

import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.menu.RenamableMenu;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * The packets of the mod. The client registers what shows a notice and the boxes of the scanners,
 * so this common class never references the client package, which does not exist on a dedicated
 * server.
 */
public final class CubitNetwork {
  private static final String PROTOCOL_VERSION = "1";

  private static Consumer<Component> noticeDisplay = text -> {};
  private static Consumer<OreMarksPayload> oreMarksDisplay = marks -> {};
  private static Consumer<MobMarksPayload> mobMarksDisplay = marks -> {};
  private static Consumer<FocusMarkPayload> focusMarkDisplay = mark -> {};
  private static Consumer<RobotListPayload> robotListDisplay = list -> {};
  private static Consumer<ChosenRobotPayload> chosenRobotDisplay = chosen -> {};
  private static Consumer<AreaMarkPayload> areaMarkDisplay = mark -> {};
  private static Consumer<UnloadChestsPayload> unloadChestsDisplay = chests -> {};
  private static Consumer<OrganizerMarksPayload> organizerMarksDisplay = marks -> {};
  private static Consumer<DeathLogPayload> deathLogDisplay = log -> {};
  private static Consumer<AskRobotNamePayload> askNameDisplay = ask -> {};
  private static Consumer<AskBlockNamePayload> askBlockNameDisplay = ask -> {};
  private static BiConsumer<ServerPlayer, BlockNamedPayload> blockNamed = (player, answer) -> {};
  private static Consumer<DoorMarksPayload> doorMarksDisplay = doors -> {};
  private static Consumer<PatrolRoutePayload> patrolRouteDisplay = route -> {};
  private static Consumer<EscortMarksPayload> escortMarksDisplay = marks -> {};
  private static TabletRequests tabletRequests = TabletRequests.NONE;

  private CubitNetwork() {}

  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
    registrar
        .playToClient(
            NoticePayload.TYPE,
            NoticePayload.STREAM_CODEC,
            (payload, context) -> noticeDisplay.accept(payload.text()))
        .playToClient(
            OreMarksPayload.TYPE,
            OreMarksPayload.STREAM_CODEC,
            (payload, context) -> oreMarksDisplay.accept(payload))
        .playToClient(
            MobMarksPayload.TYPE,
            MobMarksPayload.STREAM_CODEC,
            (payload, context) -> mobMarksDisplay.accept(payload))
        .playToClient(
            FocusMarkPayload.TYPE,
            FocusMarkPayload.STREAM_CODEC,
            (payload, context) -> focusMarkDisplay.accept(payload));
    registerTablet(registrar);
  }

  /** The list and the chosen Cubit go to the client, and the requests of the player come back. */
  private static void registerTablet(PayloadRegistrar registrar) {
    registrar
        .playToClient(
            RobotListPayload.TYPE,
            RobotListPayload.STREAM_CODEC,
            (payload, context) -> robotListDisplay.accept(payload))
        .playToClient(
            ChosenRobotPayload.TYPE,
            ChosenRobotPayload.STREAM_CODEC,
            (payload, context) -> chosenRobotDisplay.accept(payload))
        .playToClient(
            AreaMarkPayload.TYPE,
            AreaMarkPayload.STREAM_CODEC,
            (payload, context) -> areaMarkDisplay.accept(payload))
        .playToClient(
            UnloadChestsPayload.TYPE,
            UnloadChestsPayload.STREAM_CODEC,
            (payload, context) -> unloadChestsDisplay.accept(payload))
        .playToClient(
            OrganizerMarksPayload.TYPE,
            OrganizerMarksPayload.STREAM_CODEC,
            (payload, context) -> organizerMarksDisplay.accept(payload))
        .playToClient(
            DeathLogPayload.TYPE,
            DeathLogPayload.STREAM_CODEC,
            (payload, context) -> deathLogDisplay.accept(payload));
    registerDoorMarks(registrar);
    NetworkLinksChannel.register(registrar);
    registerTabletRequests(registrar);
  }

  /** The doors the player marked, for the arrows of the Doors app (answer 1371). */
  private static void registerDoorMarks(PayloadRegistrar registrar) {
    registrar
        .playToClient(
            DoorMarksPayload.TYPE,
            DoorMarksPayload.STREAM_CODEC,
            (payload, context) -> doorMarksDisplay.accept(payload))
        .playToClient(
            PatrolRoutePayload.TYPE,
            PatrolRoutePayload.STREAM_CODEC,
            (payload, context) -> patrolRouteDisplay.accept(payload))
        .playToClient(
            EscortMarksPayload.TYPE,
            EscortMarksPayload.STREAM_CODEC,
            (payload, context) -> escortMarksDisplay.accept(payload));
  }

  /** What the player asks from the Tablet, handled only for a player of the server. */
  private static void registerTabletRequests(PayloadRegistrar registrar) {
    registrar
        .playToServer(
            ChooseRobotPayload.TYPE,
            ChooseRobotPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(
                    context, player -> tabletRequests.choose().accept(player, payload.robot())))
        .playToServer(
            RobotListRequestPayload.TYPE,
            RobotListRequestPayload.STREAM_CODEC,
            (payload, context) -> onServer(context, tabletRequests.list()))
        .playToServer(
            RenameRobotPayload.TYPE,
            RenameRobotPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(context, player -> tabletRequests.rename().accept(player, payload)))
        .playToServer(
            SwitchTabletAppPayload.TYPE,
            SwitchTabletAppPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(
                    context,
                    player -> tabletRequests.switchApp().accept(player, payload.direction())));
    registerDeathRequests(registrar);
    registerNames(registrar);
  }

  /** The windows of the name of a new Cubit and block, and the Rename of a block (1868, 1879). */
  private static void registerNames(PayloadRegistrar registrar) {
    registrar
        .playToClient(
            AskRobotNamePayload.TYPE,
            AskRobotNamePayload.STREAM_CODEC,
            (payload, context) -> askNameDisplay.accept(payload))
        .playToServer(
            RobotNamedPayload.TYPE,
            RobotNamedPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(context, player -> tabletRequests.named().accept(player, payload)))
        .playToClient(
            AskBlockNamePayload.TYPE,
            AskBlockNamePayload.STREAM_CODEC,
            (payload, context) -> askBlockNameDisplay.accept(payload))
        .playToServer(
            BlockNamedPayload.TYPE,
            BlockNamedPayload.STREAM_CODEC,
            (payload, context) -> onServer(context, player -> blockNamed.accept(player, payload)))
        .playToServer(
            RenameBlockPayload.TYPE,
            RenameBlockPayload.STREAM_CODEC,
            (payload, context) -> onServer(context, player -> renameBlock(player, payload)));
  }

  /** The buttons of the Deaths tab and the call of an unloaded Cubit (answers 1111 and 2463). */
  private static void registerDeathRequests(PayloadRegistrar registrar) {
    registrar
        .playToServer(
            DeathGuidePayload.TYPE,
            DeathGuidePayload.STREAM_CODEC,
            (payload, context) ->
                onServer(context, player -> tabletRequests.guideDeath().accept(player, payload)))
        .playToServer(
            DeleteDeathPayload.TYPE,
            DeleteDeathPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(context, player -> tabletRequests.deleteDeath().accept(player, payload)))
        .playToServer(
            CallRobotPayload.TYPE,
            CallRobotPayload.STREAM_CODEC,
            (payload, context) ->
                onServer(context, player -> tabletRequests.call().accept(player, payload.robot())));
  }

  private static void onServer(IPayloadContext context, Consumer<ServerPlayer> handler) {
    if (context.player() instanceof ServerPlayer player) {
      handler.accept(player);
    }
  }

  /** On the client only: how the list and the chosen Cubit of the Tablet show. */
  public static void setTabletDisplays(
      Consumer<RobotListPayload> list, Consumer<ChosenRobotPayload> chosen) {
    robotListDisplay = list;
    chosenRobotDisplay = chosen;
  }

  /** On the client only: how the marking and the area of the Area app show (answer 1056). */
  public static void setAreaDisplay(Consumer<AreaMarkPayload> area) {
    areaMarkDisplay = area;
  }

  /** On the client only: how the arrows over the marked chests show (answer 1086). */
  public static void setUnloadChestsDisplay(Consumer<UnloadChestsPayload> chests) {
    unloadChestsDisplay = chests;
  }

  /** On the client only: how the arrows over the marks of the Organizers show (answer 1297). */
  public static void setOrganizerMarksDisplay(Consumer<OrganizerMarksPayload> marks) {
    organizerMarksDisplay = marks;
  }

  /** On the client only: how the route of the chosen Patroller shows (answers 1580 and 1612). */
  public static void setPatrolRouteDisplay(Consumer<PatrolRoutePayload> route) {
    patrolRouteDisplay = route;
  }

  /** On the client only: how the arrows over the escorted ones show (answer 1661). */
  public static void setEscortMarksDisplay(Consumer<EscortMarksPayload> marks) {
    escortMarksDisplay = marks;
  }

  /** On the client only: how the arrows over the marked doors show (answer 1371). */
  public static void setDoorMarksDisplay(Consumer<DoorMarksPayload> doors) {
    doorMarksDisplay = doors;
  }

  /** On the client only: how the Deaths tab shows the log (answer 1111). */
  public static void setDeathLogDisplay(Consumer<DeathLogPayload> log) {
    deathLogDisplay = log;
  }

  /** On the server: who handles the requests, so the network never depends on the player code. */
  public static void setTabletRequests(TabletRequests requests) {
    tabletRequests = requests;
  }

  public static void sendRobotList(ServerPlayer player, RobotListPayload list) {
    send(player, list);
  }

  public static void sendChosenRobot(ServerPlayer player, ChosenRobotPayload chosen) {
    send(player, chosen);
  }

  public static void sendAreaMark(ServerPlayer player, AreaMarkPayload mark) {
    send(player, mark);
  }

  public static void sendUnloadChests(ServerPlayer player, UnloadChestsPayload chests) {
    send(player, chests);
  }

  public static void sendOrganizerMarks(ServerPlayer player, OrganizerMarksPayload marks) {
    send(player, marks);
  }

  public static void sendDoorMarks(ServerPlayer player, DoorMarksPayload doors) {
    send(player, doors);
  }

  public static void sendPatrolRoute(ServerPlayer player, PatrolRoutePayload route) {
    send(player, route);
  }

  public static void sendEscortMarks(ServerPlayer player, EscortMarksPayload marks) {
    send(player, marks);
  }

  public static void sendDeathLog(ServerPlayer player, DeathLogPayload log) {
    send(player, log);
  }

  /** On the client: the guide button of a row of the Deaths tab, to guide or to cancel. */
  public static void guideToDeath(UUID robot, int row, long dayTime, boolean start) {
    PacketDistributor.sendToServer(new DeathGuidePayload(robot, row, dayTime, start));
  }

  /** On the client: the delete button of a row of the Deaths tab. */
  public static void deleteDeath(int row, long dayTime) {
    PacketDistributor.sendToServer(new DeleteDeathPayload(row, dayTime));
  }

  /** On the client: asks the server to choose a Cubit and to open its screen. */
  public static void chooseRobot(UUID robot) {
    PacketDistributor.sendToServer(new ChooseRobotPayload(robot));
  }

  /** On the client: asks the server for the list of the selection again. */
  public static void requestRobotList() {
    PacketDistributor.sendToServer(RobotListRequestPayload.INSTANCE);
  }

  /** On the client: Shift and the wheel with the Tablet in hand. */
  public static void switchTabletApp(int direction) {
    PacketDistributor.sendToServer(new SwitchTabletAppPayload(direction));
  }

  /** Only the block of the screen the player has open, which checks the owner and the reach. */
  private static void renameBlock(ServerPlayer player, RenameBlockPayload payload) {
    if (player.containerMenu instanceof RenamableMenu menu
        && player.containerMenu.containerId == payload.containerId()) {
      menu.rename(player, payload.name());
    }
  }

  /** On the client: asks the server to name the block of the open screen (answer 1868). */
  public static void renameBlock(int containerId, String name) {
    PacketDistributor.sendToServer(new RenameBlockPayload(containerId, name));
  }

  /**
   * On the server: opens the window of the name of a Cubit placed for the first time (1879). A real
   * client always has the channel, since the game refuses one without the mod; only a test player
   * lacks it, and then nothing is sent.
   */
  public static void askName(ServerPlayer player, UUID robot, String standard) {
    if (player.connection.hasChannel(AskRobotNamePayload.TYPE)) {
      PacketDistributor.sendToPlayer(player, new AskRobotNamePayload(robot, standard));
    }
  }

  /** On the client: the answer of the window, the name typed or nothing (answer 1879). */
  public static void answerName(UUID robot, String name) {
    PacketDistributor.sendToServer(new RobotNamedPayload(robot, name));
  }

  /** On the server: opens the window of the name of a block placed for the first time (2422). */
  public static void askBlockName(
      ServerPlayer player, BlockPos pos, BlockName.Kind kind, String standard) {
    if (player.connection.hasChannel(AskBlockNamePayload.TYPE)) {
      PacketDistributor.sendToPlayer(player, new AskBlockNamePayload(pos, kind, standard));
    }
  }

  /** On the client: the answer of the window of a block, the name typed or nothing (2422). */
  public static void answerBlockName(BlockPos pos, String name) {
    PacketDistributor.sendToServer(new BlockNamedPayload(pos, name));
  }

  public static void setBlockNamed(BiConsumer<ServerPlayer, BlockNamedPayload> handler) {
    blockNamed = handler;
  }

  public static void setAskNameDisplay(
      Consumer<AskRobotNamePayload> robots, Consumer<AskBlockNamePayload> blocks) {
    askNameDisplay = robots;
    askBlockNameDisplay = blocks;
  }

  /** On the client: asks the server to name a Cubit. */
  public static void renameRobot(UUID robot, String name) {
    PacketDistributor.sendToServer(new RenameRobotPayload(robot, name));
  }

  public static void setNoticeDisplay(Consumer<Component> display) {
    noticeDisplay = display;
  }

  public static void setMarksDisplay(
      Consumer<OreMarksPayload> ores, Consumer<MobMarksPayload> mobs) {
    oreMarksDisplay = ores;
    mobMarksDisplay = mobs;
  }

  public static void setFocusMarkDisplay(Consumer<FocusMarkPayload> display) {
    focusMarkDisplay = display;
  }

  public static void sendFocusMark(ServerPlayer player, FocusMarkPayload mark) {
    send(player, mark);
  }

  public static void sendNotice(ServerPlayer player, Component text) {
    send(player, new NoticePayload(text));
  }

  public static void sendOreMarks(ServerPlayer player, OreMarksPayload marks) {
    send(player, marks);
  }

  public static void sendMobMarks(ServerPlayer player, MobMarksPayload marks) {
    send(player, marks);
  }

  /** The game refuses a payload the connection did not agree on, instead of ignoring it. */
  private static void send(ServerPlayer player, CustomPacketPayload payload) {
    if (player.connection.hasChannel(payload.type())) {
      PacketDistributor.sendToPlayer(player, payload);
    }
  }
}
