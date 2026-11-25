package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.PowerBankItem;
import io.github.zenndg.cubit.item.SavedBigStack;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

/** The storage of the Network Server, with 128 items per slot (answers 2263, 2267 and 2368). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkStorageGameTests {
  private static final int LIMIT = 128;
  private static final int OVER = 200;
  private static final int STACK = 64;
  private static final int BASE_SLOTS = 27;
  private static final int FIRST = 0;
  private static final int SECOND = 1;
  private static final int ONE = 1;
  private static final int INPUT = 0;
  private static final int HANDLER_FIRST = 1;
  private static final long STANDBY = 20;
  private static final int CHARGE_TICKS = 20;
  private static final int DROP_TICKS = 5;
  private static final int UPS = 30;
  private static final Energy KEPT = new Energy(400_000);
  private static final Energy RESERVE = new Energy(360_000);
  private static final Energy ROOM = new Energy(1_000);
  private static final String ITEMS_KEY = "Items";
  private static final String AMOUNT_KEY = "Amount";
  private static final String WRONG_ITEM = "The storage kept the wrong items";
  private static final String WRONG_AMOUNT = "Wrong amount in the slot";
  private static final String TOO_MANY = "At most a normal stack should leave";
  private static final String LOST = "Something was lost";
  private static final String NESTS = "A server that keeps anything should not nest";
  private static final String REFUSED = "An empty server should fit anywhere";
  private static final String WRONG_STATE = "The placed server should be as it was";
  private static final String WRONG_CHARGE = "The banks should only take what passes the reserve";

  private NetworkStorageGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void aSlotKeepsOneHundredAndTwentyEight(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);

    insert(server, casings(STACK));
    insert(server, casings(STACK));
    ItemStack rest = insert(server, casings(STACK));

    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, WRONG_AMOUNT);
    helper.assertValueEqual(rest.getCount(), STACK, WRONG_AMOUNT);
    helper.succeed();
  }

  /** A small stack goes to 128 too, and what does not stack stays alone (answer 2263). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aSmallStackGrowsAndAnItemThatDoesNotStackStaysAlone(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);
    ItemStack toolboxes = new ItemStack(CubitItems.TOOLBOX.get());
    for (int count = 0; count < LIMIT; count++) {
      insert(server, toolboxes.copy());
    }
    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, WRONG_AMOUNT);

    IItemHandler handler = server.itemHandler();
    handler.insertItem(HANDLER_FIRST + SECOND, bank(), false);
    ItemStack rest = handler.insertItem(HANDLER_FIRST + SECOND, bank(), false);

    helper.assertValueEqual(server.storage().getItem(SECOND).getCount(), ONE, WRONG_AMOUNT);
    helper.assertValueEqual(rest.getCount(), ONE, WRONG_AMOUNT);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void onlyItemsOfTheModInTheSlotsOfTheTier(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);

    helper.assertTrue(server.storage().canPlaceItem(BASE_SLOTS - ONE, casings(ONE)), WRONG_ITEM);
    helper.assertFalse(server.storage().canPlaceItem(BASE_SLOTS, casings(ONE)), WRONG_ITEM);
    helper.assertFalse(server.storage().canPlaceItem(FIRST, new ItemStack(Items.DIRT)), WRONG_ITEM);
    helper.succeed();
  }

  /** At most a normal stack at a time, so no stack of 128 reaches a hand (answer 2368). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void atMostANormalStackLeavesAtATime(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);
    server.storage().setItem(FIRST, casings(LIMIT));
    IItemHandler handler = server.itemHandler();

    helper.assertValueEqual(
        handler.extractItem(HANDLER_FIRST, LIMIT, true).getCount(), STACK, TOO_MANY);
    helper.assertValueEqual(
        handler.extractItem(HANDLER_FIRST, LIMIT, false).getCount(), STACK, TOO_MANY);
    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), STACK, WRONG_AMOUNT);
    server.storage().setItem(FIRST, casings(LIMIT));
    helper.assertValueEqual(server.storage().removeItem(FIRST, LIMIT).getCount(), STACK, TOO_MANY);
    helper.succeed();
  }

  /** A bank leaves only full, and redstone goes in and never comes back (answer 953). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theAutomationFollowsTheRulesOfTheDock(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);
    IItemHandler handler = server.itemHandler();

    helper.assertValueEqual(handler.insertItem(INPUT, bank(), false).getCount(), ONE, WRONG_ITEM);
    handler.insertItem(HANDLER_FIRST, bank(), false);

    helper.assertTrue(handler.extractItem(HANDLER_FIRST, ONE, false).isEmpty(), WRONG_ITEM);
    helper.assertTrue(handler.extractItem(INPUT, ONE, false).isEmpty(), WRONG_ITEM);
    helper.succeed();
  }

  /** By the click: the Toolbox, the Power Bank and the Damaged Cubit (detail 5). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theClickStoresAndSendsRedstoneToTheSlot(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkServerBlockEntity server = NetworkScene.placeServer(helper, owner, RobotTier.BASE, KEPT);

    NetworkScene.clickServer(helper, owner, new ItemStack(CubitItems.TOOLBOX.get()));
    NetworkScene.clickServer(helper, owner, new ItemStack(CubitItems.DAMAGED_GENERAL_ROBOT.get()));
    NetworkScene.clickServer(helper, owner, new ItemStack(Items.REDSTONE, STACK));

    helper.assertTrue(server.storage().getItem(FIRST).is(CubitItems.TOOLBOX.get()), WRONG_ITEM);
    helper.assertTrue(
        server.storage().getItem(SECOND).is(CubitItems.DAMAGED_GENERAL_ROBOT.get()), WRONG_ITEM);
    helper.assertTrue(server.slot().getItem(FIRST).is(Items.REDSTONE), WRONG_ITEM);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void aSavedSlotKeepsItsAmount(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);
    server.storage().setItem(SECOND, casings(LIMIT));
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    CompoundTag saved = server.saveWithoutMetadata(registries);

    server.storage().clearContent();
    server.loadWithComponents(saved, registries);

    helper.assertValueEqual(server.storage().getItem(SECOND).getCount(), LIMIT, LOST);
    helper.succeed();
  }

  /** What passes the slot, from an edited save, drops beside the server (detail 7). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aBrokenAmountDropsTheRest(GameTestHelper helper) {
    NetworkServerBlockEntity server = server(helper, Energy.ZERO);
    server.storage().setItem(FIRST, casings(LIMIT));
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    CompoundTag saved = server.saveWithoutMetadata(registries);
    ListTag items = saved.getList(ITEMS_KEY, Tag.TAG_COMPOUND);
    items.getCompound(FIRST).putInt(AMOUNT_KEY, OVER);

    server.loadWithComponents(saved, registries);

    helper.assertValueEqual(server.storage().getItem(FIRST).getCount(), LIMIT, WRONG_AMOUNT);
    helper.runAfterDelay(
        DROP_TICKS,
        () -> {
          helper.assertValueEqual(dropped(helper), OVER - LIMIT, LOST);
          helper.succeed();
        });
  }

  /** The whole way of the game: the drop of the loot table, placed again (answers 2254, 2411). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theDropKeepsAllOfTheServer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkServerBlockEntity server = placeFull(helper, owner);
    server.setUps(new UpsReserve(UPS));
    server.setChunkLoading(false);
    Object id = server.identity().id();

    helper
        .getLevel()
        .destroyBlock(helper.absolutePos(NetworkScene.serverFloor(helper).above()), true);
    ItemStack item = drop(helper);
    NetworkServerBlockEntity placed = NetworkScene.placeServer(helper, owner, item);

    assertFull(helper, placed);
    helper.assertValueEqual(placed.ups(), new UpsReserve(UPS), WRONG_STATE);
    helper.assertFalse(placed.chunkLoading(), WRONG_STATE);
    helper.assertValueEqual(placed.identity().id(), id, WRONG_STATE);
    helper.succeed();
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void breakingInCreativeKeepsAllOfTheServer(GameTestHelper helper) {
    ServerPlayer owner = GameTestRobots.onlinePlayer(helper);
    placeFull(helper, owner);

    owner.gameMode.destroyBlock(helper.absolutePos(NetworkScene.serverFloor(helper).above(2)));
    NetworkServerBlockEntity placed = NetworkScene.placeServer(helper, owner, drop(helper));

    assertFull(helper, placed);
    helper.succeed();
  }

  /** The codec of the item keeps more than 99 (answer 2368). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theItemSavesWithItsAmounts(GameTestHelper helper) {
    ItemStack item = full();
    HolderLookup.Provider registries = helper.getLevel().registryAccess();

    ItemStack loaded = ItemStack.parse(registries, item.save(registries)).orElseThrow();

    helper.assertTrue(ItemStack.matches(item, loaded), LOST);
    helper.succeed();
  }

  /** With items or energy it never nests, and empty it fits (detail 16). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aServerThatKeepsAnythingDoesNotNest(GameTestHelper helper) {
    ItemStack empty = CubitItems.NETWORK_SERVER.get().getDefaultInstance();
    ItemStack charged = NetworkScene.serverItem(RobotTier.BASE, ROOM);

    helper.assertTrue(empty.canFitInsideContainerItems(), REFUSED);
    helper.assertFalse(charged.canFitInsideContainerItems(), NESTS);
    helper.assertFalse(full().canFitInsideContainerItems(), NESTS);
    helper.succeed();
  }

  /** One at a time, and never out of the reserve, without creating or losing FE (2373). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theBanksNeverTakeTheReserve(GameTestHelper helper) {
    Energy start = RESERVE.plus(ROOM);
    NetworkServerBlockEntity server = server(helper, start);
    server.setUps(new UpsReserve(UPS));
    server.setChunkLoading(false);
    server.storage().setItem(FIRST, bank());
    server.storage().setItem(SECOND, bank());

    helper.runAfterDelay(
        CHARGE_TICKS,
        () -> {
          // Only the standby takes from the reserve (answer 2437).
          long spent = start.amount() - server.energy().amount() - chargeAt(server, FIRST).amount();
          helper.assertTrue(
              spent > 0 && spent % STANDBY == 0 && spent <= STANDBY * (CHARGE_TICKS + 1),
              WRONG_CHARGE);
          helper.assertTrue(chargeAt(server, FIRST).amount() <= ROOM.amount(), WRONG_CHARGE);
          helper.assertTrue(
              server.energy().amount() >= RESERVE.amount() - STANDBY * (CHARGE_TICKS + 1),
              WRONG_CHARGE);
          helper.assertTrue(chargeAt(server, SECOND).isZero(), WRONG_CHARGE);
          helper.succeed();
        });
  }

  private static NetworkServerBlockEntity server(GameTestHelper helper, Energy energy) {
    return NetworkScene.placeServer(
        helper, helper.makeMockPlayer(GameType.SURVIVAL), RobotTier.BASE, energy);
  }

  private static ItemStack full() {
    return NetworkScene.serverItem(
        RobotTier.ADVANCED, KEPT, SavedBigStack.of(SECOND, casings(LIMIT)));
  }

  private static NetworkServerBlockEntity placeFull(GameTestHelper helper, Player owner) {
    return NetworkScene.placeServer(helper, owner, full());
  }

  private static void assertFull(GameTestHelper helper, NetworkServerBlockEntity server) {
    helper.assertValueEqual(server.tier(), RobotTier.ADVANCED, WRONG_STATE);
    helper.assertValueEqual(server.energy(), KEPT, LOST);
    helper.assertValueEqual(server.storage().getItem(SECOND).getCount(), LIMIT, LOST);
  }

  private static ItemStack insert(NetworkServerBlockEntity server, ItemStack stack) {
    return server.itemHandler().insertItem(HANDLER_FIRST, stack, false);
  }

  private static ItemStack casings(int count) {
    return new ItemStack(CubitItems.GENERAL_ROBOT_CASING.get(), count);
  }

  private static ItemStack bank() {
    return CubitItems.BASE_POWER_BANK.get().getDefaultInstance();
  }

  private static Energy chargeAt(NetworkServerBlockEntity server, int index) {
    return PowerBankItem.batteryOf(server.storage().getItem(index)).charge();
  }

  private static int dropped(GameTestHelper helper) {
    return helper.getEntities(EntityType.ITEM).stream()
        .map(ItemEntity::getItem)
        .filter(stack -> stack.is(CubitItems.GENERAL_ROBOT_CASING.get()))
        .mapToInt(ItemStack::getCount)
        .sum();
  }

  private static ItemStack drop(GameTestHelper helper) {
    return helper.getEntities(EntityType.ITEM).stream()
        .map(ItemEntity::getItem)
        .filter(stack -> stack.has(CubitDataComponents.NETWORK_STATE.get()))
        .filter(stack -> SavedNetwork.of(stack).holdsAnything())
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(LOST));
  }
}
