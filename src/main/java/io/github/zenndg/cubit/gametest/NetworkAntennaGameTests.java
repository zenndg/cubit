package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlocks;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Linking the Signal Antenna to a Network Server, where it may go and which channel it takes
 * (Delivery 4, details 6, 13, 14 and 19, and answers 2546, 2547 and 2569).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkAntennaGameTests {
  private static final int STACK = 3;
  private static final long FAR_AWAY = 1_000_000L;
  private static final String NOT_LINKED = "The stack should be linked to the Server";
  private static final String LINKED = "A Server of another player should not link it";
  private static final String PLACED = "It should be refused";
  private static final String NOT_PLACED = "It should be placed";
  private static final String KEPT = "The refused item should stay in the hand";
  private static final String WRONG_CHANNEL = "It should take another channel";
  private static final String NOT_LISTED = "The data of the world should know it";
  private static final String STILL_LISTED = "The data of the world should forget it";

  private NetworkAntennaGameTests() {}

  /** The whole stack, with the name the Server has now (answer 2567). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aClickOnTheServerOfTheOwnerLinksTheStack(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack stack = new ItemStack(CubitItems.SIGNAL_ANTENNA.get(), STACK);

    NetworkScene.clickServer(helper, owner, stack);

    SavedLink link = SavedNetwork.of(stack).link();
    helper.assertValueEqual(link.server(), Optional.of(server.identity().id()), NOT_LINKED);
    helper.assertValueEqual(
        link.serverName(), Optional.of(server.identity().name().value()), NOT_LINKED);
    helper.assertValueEqual(stack.getCount(), STACK, NOT_LINKED);
    helper.succeed();
  }

  /** Also with the Shift held, the item links and is never placed by the Server. */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theItemLinksOnTheServerAlsoWithoutTheBlock(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack stack = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());

    useOnServer(helper, owner, stack);

    helper.assertValueEqual(
        SavedNetwork.of(stack).link().server(), Optional.of(server.identity().id()), NOT_LINKED);
    helper.assertValueEqual(stack.getCount(), 1, KEPT);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aServerOfAnotherPlayerDoesNotLinkIt(GameTestHelper helper) {
    server(helper, player(helper));
    ItemStack stack = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());

    useOnServer(helper, player(helper), stack);

    helper.assertTrue(SavedNetwork.of(stack).link().server().isEmpty(), LINKED);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaWithoutALinkIsRefused(GameTestHelper helper) {
    assertRefused(helper, player(helper), new ItemStack(CubitItems.SIGNAL_ANTENNA.get()));
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaOfAServerThatIsGoneIsRefused(GameTestHelper helper) {
    ItemStack stack = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());
    linkTo(stack, SavedLink.NONE.linkedTo(UUID.randomUUID(), PLACED));

    assertRefused(helper, player(helper), stack);
  }

  /** Given to another player, it never takes a channel of its old Server (answer 2569). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaOfAServerOfAnotherPlayerIsRefused(GameTestHelper helper) {
    Player owner = player(helper);
    ItemStack stack = linked(helper, owner, server(helper, owner));

    assertRefused(helper, player(helper), stack);
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void anAntennaInAnotherDimensionIsRefused(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack stack = linked(helper, owner, server);
    NetworkRoster<GlobalPos> roster = roster(helper);
    NetworkRoster.Server<GlobalPos> here = roster.server(server.identity().id()).orElseThrow();
    roster.putServer(
        new NetworkRoster.Server<>(
            here.id(), here.owner(), GlobalPos.of(Level.NETHER, here.place().pos()), here.tier()));

    assertRefused(helper, owner, stack);
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aLinkedAntennaTakesTheFirstFreeChannel(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    SignalAntennaBlockEntity antenna = place(helper, owner, linked(helper, owner, server));

    helper.assertValueEqual(
        antenna.link().channel(), Optional.of(AntennaChannel.BLACK), WRONG_CHANNEL);
    helper.assertTrue(
        roster(helper).taken(server.identity().id()).contains(AntennaChannel.BLACK), NOT_LISTED);
    helper.succeed();
  }

  /** Placed again, it goes back to its last channel while it is free (answer 2547). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aPlacedAntennaGoesBackToItsLastChannel(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack stack = linked(helper, owner, server);
    linkTo(stack, SavedNetwork.of(stack).link().withChannel(AntennaChannel.BROWN));

    SignalAntennaBlockEntity antenna = place(helper, owner, stack);

    helper.assertValueEqual(
        antenna.link().channel(), Optional.of(AntennaChannel.BROWN), WRONG_CHANNEL);
    helper.succeed();
  }

  /** The Base opens 4 channels, and with all of them taken nothing is placed. */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aServerWithoutFreeChannelsRefusesIt(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack stack = linked(helper, owner, server);
    List<GlobalPos> taken = takeEveryChannel(helper, server, owner);

    assertRefused(helper, owner, stack);
    taken.forEach(roster(helper)::removeAntenna);
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aSecondAntennaInTheChunkIsRefused(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    ItemStack first = linked(helper, owner, server);
    GameTestRobots.use(helper, owner, first, NetworkScene.relayFloor(helper));

    assertRefused(helper, owner, linked(helper, owner, server));
  }

  /** Broken, its channel is free again. */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aBrokenAntennaGivesItsChannelBack(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server = server(helper, owner);
    place(helper, owner, linked(helper, owner, server));

    helper.destroyBlock(NetworkScene.otherFloor(helper).above());

    helper.assertTrue(roster(helper).taken(server.identity().id()).isEmpty(), STILL_LISTED);
    helper.succeed();
  }

  /** The Server is known far away by its place and its tier, and forgotten when broken. */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theDataOfTheWorldFollowsTheServer(GameTestHelper helper) {
    Player owner = player(helper);
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(helper, owner, RobotTier.ELITE, Energy.ZERO);
    UUID id = server.identity().id();

    helper.assertValueEqual(
        roster(helper).server(id).map(NetworkRoster.Server::tier),
        Optional.of(RobotTier.ELITE),
        NOT_LISTED);
    helper.destroyBlock(NetworkScene.serverFloor(helper).above());

    helper.assertTrue(roster(helper).server(id).isEmpty(), STILL_LISTED);
    helper.succeed();
  }

  private static List<GlobalPos> takeEveryChannel(
      GameTestHelper helper, NetworkServerBlockEntity server, Player owner) {
    List<GlobalPos> places = new ArrayList<>();
    int open = CubitMod.CONFIG.network().server(server.tier()).channels();
    for (AntennaChannel channel : AntennaChannel.values()) {
      if (!channel.isOpen(open)) {
        continue;
      }
      GlobalPos place =
          GlobalPos.of(helper.getLevel().dimension(), BlockPos.of(FAR_AWAY + channel.ordinal()));
      roster(helper)
          .putAntenna(
              new NetworkRoster.Antenna<>(place, owner.getUUID(), server.identity().id(), channel));
      places.add(place);
    }
    return places;
  }

  private static void assertRefused(GameTestHelper helper, Player player, ItemStack item) {
    BlockPos floor = NetworkScene.otherFloor(helper);

    GameTestRobots.use(helper, player, item, floor);

    helper.assertBlockNotPresent(CubitBlocks.SIGNAL_ANTENNA.get(), floor.above());
    helper.assertValueEqual(player.getMainHandItem().getCount(), 1, KEPT);
    helper.succeed();
  }

  private static SignalAntennaBlockEntity place(
      GameTestHelper helper, Player owner, ItemStack stack) {
    BlockPos floor = NetworkScene.otherFloor(helper);
    GameTestRobots.use(helper, owner, stack, floor);
    helper.assertBlockPresent(CubitBlocks.SIGNAL_ANTENNA.get(), floor.above());
    if (!(helper.getBlockEntity(floor.above()) instanceof SignalAntennaBlockEntity antenna)) {
      throw new IllegalStateException(NOT_PLACED);
    }
    return antenna;
  }

  private static ItemStack linked(
      GameTestHelper helper, Player owner, NetworkServerBlockEntity server) {
    ItemStack stack = new ItemStack(CubitItems.SIGNAL_ANTENNA.get());
    NetworkScene.clickServer(helper, owner, stack);
    return stack;
  }

  private static void linkTo(ItemStack stack, SavedLink link) {
    stack.set(CubitDataComponents.NETWORK_STATE.get(), SavedNetwork.of(stack).withLink(link));
  }

  private static void useOnServer(GameTestHelper helper, Player player, ItemStack stack) {
    BlockPos absolute = helper.absolutePos(NetworkScene.serverFloor(helper).above());
    BlockHitResult hit =
        new BlockHitResult(Vec3.atCenterOf(absolute), Direction.UP, absolute, false);
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
  }

  private static NetworkRoster<GlobalPos> roster(GameTestHelper helper) {
    return NetworkData.of(helper.getLevel().getServer()).roster();
  }

  private static Player player(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }

  private static NetworkServerBlockEntity server(GameTestHelper helper, Player owner) {
    return NetworkScene.placeServer(helper, owner, RobotTier.BASE, Energy.ZERO);
  }
}
