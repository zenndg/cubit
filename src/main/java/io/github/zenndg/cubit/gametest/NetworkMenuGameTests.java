package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.menu.NetworkServerMenu;
import io.github.zenndg.cubit.menu.SolarRelayMenu;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The screens of the Network: who may use them, the buttons and the clicks on a slot with 128,
 * which never put more than a normal stack in a hand (answers 2219, 2259 and 2368).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkMenuGameTests {
  private static final int MENU_ID = 1;
  private static final int LIMIT = 128;
  private static final int STACK = 64;
  private static final int FIRST = 0;
  private static final int FIRST_BIG = 1;
  private static final int PLAYER_FIRST = 55;
  private static final int LEFT = 0;
  private static final int RIGHT = 1;
  private static final int KEY = 3;
  private static final int DROP_ALL = 1;
  private static final int DUST = 10;
  private static final String WALL_KEY = "ShowWall";
  private static final UpsReserve STANDARD = new UpsReserve(25);
  private static final UpsReserve LESS = new UpsReserve(20);
  private static final Energy KEPT = new Energy(400_000);
  private static final String WRONG_MENU = "Only the owner keeps the screen open";
  private static final String PRESSED = "The button should do nothing";
  private static final String WRONG_CHOICE = "The button should change the server";
  private static final String TOO_MANY = "At most a normal stack should leave";
  private static final String LOST = "Items were created or lost";
  private static final String SWAPPED = "A large pile should never swap";
  private static final String WRONG_SLOT = "The item went to the wrong slot";
  private static final String NAME = "Core";
  private static final String NOT_NAMED = "The owner should name it on its screen";

  private NetworkMenuGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void onlyTheOwnerUsesTheScreen(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    ServerPlayer stranger = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = menu(owner, server);

    helper.assertTrue(menu.stillValid(owner), WRONG_MENU);
    helper.assertFalse(menu.stillValid(stranger), WRONG_MENU);
    helper.assertFalse(
        menu.clickMenuButton(stranger, NetworkServerMenu.CHUNK_BUTTON)
            || menu.rename(stranger, NAME),
        PRESSED);
    helper.assertTrue(menu.rename(owner, NAME), NOT_NAMED);
    helper.assertValueEqual(server.identity().name().value(), NAME, NOT_NAMED);
    helper.succeed();
  }

  /** The reserve only changes with the chunk on (detail 23). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theButtonsChangeTheChunkAndTheReserve(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    server.setChunkLoading(true);
    NetworkServerMenu menu = menu(owner, server);

    helper.assertTrue(menu.clickMenuButton(owner, NetworkServerMenu.UPS_LESS_BUTTON), WRONG_CHOICE);
    helper.assertValueEqual(server.ups(), LESS, WRONG_CHOICE);
    menu.clickMenuButton(owner, NetworkServerMenu.UPS_MORE_BUTTON);
    helper.assertValueEqual(server.ups(), STANDARD, WRONG_CHOICE);
    menu.clickMenuButton(owner, NetworkServerMenu.CHUNK_BUTTON);
    helper.assertFalse(server.chunkLoading(), WRONG_CHOICE);
    helper.assertFalse(menu.clickMenuButton(owner, NetworkServerMenu.UPS_LESS_BUTTON), PRESSED);
    helper.assertValueEqual(server.ups(), STANDARD, PRESSED);
    helper.succeed();
  }

  /** Off at first, also with the chunk off, and only for the owner (answers 2446 and 2448). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theViewButtonShowsTheWall(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    ServerPlayer stranger = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    server.setChunkLoading(false);
    NetworkServerMenu menu = menu(owner, server);

    helper.assertFalse(server.showsWall(), WRONG_CHOICE);
    helper.assertFalse(menu.clickMenuButton(stranger, NetworkServerMenu.WALL_BUTTON), PRESSED);
    helper.assertTrue(menu.clickMenuButton(owner, NetworkServerMenu.WALL_BUTTON), WRONG_CHOICE);
    helper.assertTrue(server.showsWall(), WRONG_CHOICE);
    helper.assertTrue(
        server.getUpdateTag(helper.getLevel().registryAccess()).getBoolean(WALL_KEY), WRONG_CHOICE);
    menu.clickMenuButton(owner, NetworkServerMenu.WALL_BUTTON);
    helper.assertFalse(server.showsWall(), WRONG_CHOICE);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aClickTakesAStackAtMost(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);

    menu.clicked(FIRST_BIG, LEFT, ClickType.PICKUP, owner);
    helper.assertValueEqual(menu.getCarried().getCount(), STACK, TOO_MANY);
    assertTotal(helper, server, owner, menu);
    menu.clicked(FIRST_BIG, LEFT, ClickType.PICKUP, owner);
    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, LOST);
    menu.clicked(FIRST_BIG, RIGHT, ClickType.PICKUP, owner);
    helper.assertTrue(menu.getCarried().getCount() <= STACK, TOO_MANY);
    assertTotal(helper, server, owner, menu);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aShiftClickTakesAStackAtMost(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);

    menu.clicked(FIRST_BIG, LEFT, ClickType.QUICK_MOVE, owner);

    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), STACK, TOO_MANY);
    assertTotal(helper, server, owner, menu);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aKeyTakesAStackIntoAnEmptyPlaceAndNeverSwaps(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);
    Inventory inventory = owner.getInventory();
    inventory.setItem(KEY, new ItemStack(CubitItems.TOOLBOX.get()));

    menu.clicked(FIRST_BIG, KEY, ClickType.SWAP, owner);
    helper.assertTrue(inventory.getItem(KEY).is(CubitItems.TOOLBOX.get()), SWAPPED);
    inventory.setItem(KEY, ItemStack.EMPTY);
    menu.clicked(FIRST_BIG, KEY, ClickType.SWAP, owner);

    helper.assertValueEqual(inventory.getItem(KEY).getCount(), STACK, TOO_MANY);
    assertTotal(helper, server, owner, menu);
    helper.succeed();
  }

  /** A pile above a stack never goes to the cursor in a swap with another item. */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anotherItemInTheCursorNeverSwapsALargePile(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);
    menu.setCarried(new ItemStack(CubitItems.TOOLBOX.get()));

    menu.clicked(FIRST_BIG, LEFT, ClickType.PICKUP, owner);

    helper.assertTrue(menu.getCarried().is(CubitItems.TOOLBOX.get()), SWAPPED);
    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, LOST);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void droppingTakesAStackAtMost(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);

    menu.clicked(FIRST_BIG, DROP_ALL, ClickType.THROW, owner);

    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), STACK, TOO_MANY);
    helper.succeed();
  }

  /**
   * With Shift + click, the items of the mod go to the storage, and redstone to the input (959).
   */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aShiftClickStoresAndFeeds(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner);
    NetworkServerMenu menu = filled(owner, server);
    server.storage().setItem(FIRST, casings(STACK));
    menu.slots.get(PLAYER_FIRST).set(casings(STACK));
    menu.slots.get(PLAYER_FIRST + 1).set(new ItemStack(Items.REDSTONE, DUST));

    menu.clicked(PLAYER_FIRST, LEFT, ClickType.QUICK_MOVE, owner);
    menu.clicked(PLAYER_FIRST + 1, LEFT, ClickType.QUICK_MOVE, owner);

    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, WRONG_SLOT);
    helper.assertTrue(server.slot().getItem(FIRST).is(Items.REDSTONE), WRONG_SLOT);
    helper.assertTrue(menu.slots.get(PLAYER_FIRST).getItem().isEmpty(), LOST);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theRelayShowsItsPair(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    place(helper, owner);
    SolarRelayBlockEntity relay = NetworkScene.placeRelay(helper, owner);

    SolarRelayMenu menu = SolarRelayMenu.onServer(MENU_ID, owner.getInventory(), relay);

    helper.assertTrue(menu.paired() && !menu.serverName().isEmpty(), WRONG_MENU);
    helper.succeed();
  }

  private static ServerPlayer owner(GameTestHelper helper) {
    ServerPlayer player = GameTestRobots.onlinePlayer(helper);
    BlockPos beside = helper.absolutePos(NetworkScene.serverFloor(helper).south(2).above());
    player.moveTo(beside, 0, 0);
    return player;
  }

  private static NetworkServerBlockEntity place(GameTestHelper helper, ServerPlayer owner) {
    NetworkServerBlockEntity server = NetworkScene.placeServer(helper, owner, RobotTier.BASE, KEPT);
    server.setChunkLoading(false);
    owner.getInventory().clearContent();
    return server;
  }

  private static NetworkServerMenu menu(ServerPlayer owner, NetworkServerBlockEntity server) {
    return NetworkServerMenu.onServer(MENU_ID, owner.getInventory(), server);
  }

  private static NetworkServerMenu filled(ServerPlayer owner, NetworkServerBlockEntity server) {
    server.storage().setItem(FIRST, casings(LIMIT));
    return menu(owner, server);
  }

  private static ItemStack casings(int count) {
    return new ItemStack(CubitItems.GENERAL_ROBOT_CASING.get(), count);
  }

  private static void assertTotal(
      GameTestHelper helper,
      NetworkServerBlockEntity server,
      ServerPlayer owner,
      NetworkServerMenu menu) {
    int held = owner.getInventory().countItem(CubitItems.GENERAL_ROBOT_CASING.get());
    int total = server.storage().getItem(FIRST).getCount() + menu.getCarried().getCount() + held;
    helper.assertValueEqual(total, LIMIT, LOST);
  }
}
