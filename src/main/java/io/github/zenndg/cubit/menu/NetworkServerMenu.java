package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.ServerSlots;
import io.github.zenndg.cubit.config.ChunkKeys;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.BigSlot;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.ServerStatus;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The screen of a Network Server, with the Energy and Storage tabs (answers 2277 and 2397). The
 * open tab is only a choice of the client, and every button is checked by the server.
 */
public final class NetworkServerMenu extends NetworkMenu {
  public static final int PLAYER_Y = 179;
  public static final int STORAGE_Y = 18;
  public static final int CHUNK_BUTTON = 0;
  public static final int UPS_LESS_BUTTON = 1;
  public static final int UPS_MORE_BUTTON = 2;
  private static final int STORAGE_FIRST = 1;
  private static final int PLAYER_FIRST = STORAGE_FIRST + ServerSpec.MAX_SLOTS;

  private final ServerControl control;
  private final ServerNumbers numbers;
  private boolean storageShown;

  private NetworkServerMenu(
      int containerId,
      Inventory inventory,
      Boxes boxes,
      ContainerData values,
      Server server,
      ServerControl control,
      Opening opening,
      ServerNumbers numbers) {
    super(CubitMenus.NETWORK_SERVER.get(), containerId, values, server, opening);
    this.control = control;
    this.numbers = numbers;
    addInputSlot(boxes.input());
    for (int index = 0; index < ServerSpec.MAX_SLOTS; index++) {
      int y = STORAGE_Y + index / SLOTS_PER_ROW * SLOT_SIZE;
      addSlot(
          new BigSlotView(
              boxes.storage(), index, slotX(index % SLOTS_PER_ROW), y, () -> storageShown));
    }
    addDataSlots(values);
    addPlayerSlots(inventory, PLAYER_Y);
  }

  public static NetworkServerMenu onServer(
      int containerId, Inventory inventory, NetworkServerBlockEntity server) {
    ServerScreenValues values = new ServerScreenValues(server);
    return new NetworkServerMenu(
        containerId,
        inventory,
        new Boxes(server.slot(), server.storage()),
        values,
        Server.of(server, values),
        new ServerControl(server),
        Opening.NONE,
        ServerNumbers.of(server));
  }

  /** On the client. The real items and the numbers come from the server. */
  public static NetworkServerMenu onClient(
      int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
    Opening opening = Opening.read(data);
    ServerNumbers numbers = ServerNumbers.read(data);
    ServerSlots storage =
        new ServerSlots(() -> {}, numbers::storageSlots, () -> new BigSlot(numbers.slotLimit()));
    return new NetworkServerMenu(
        containerId,
        inventory,
        new Boxes(new SimpleContainer(1), storage),
        new SimpleContainerData(ServerScreenValues.COUNT),
        Server.NONE,
        ServerControl.NONE,
        opening,
        numbers);
  }

  public static void writeOpening(RegistryFriendlyByteBuf data, NetworkServerBlockEntity server) {
    Opening.of(server).write(data);
    ServerNumbers.of(server).write(data);
  }

  /** Every press is checked again, for any other caller (rule 20). */
  @Override
  public boolean clickMenuButton(Player player, int button) {
    return stillValid(player) && control.press(button);
  }

  /** A swap with a key takes one normal stack out of a large pile, into an empty place (2368). */
  @Override
  public void clicked(int slotId, int button, ClickType type, Player player) {
    if (type == ClickType.SWAP && largeAt(slotId)) {
      takeByKey((BigSlotView) slots.get(slotId), button, player);
      return;
    }
    super.clicked(slotId, button, type, player);
  }

  private boolean largeAt(int slotId) {
    return slotId >= STORAGE_FIRST
        && slotId < PLAYER_FIRST
        && ((BigSlotView) slots.get(slotId)).isLarge();
  }

  private static void takeByKey(BigSlotView slot, int key, Player player) {
    boolean hotbar = Inventory.isHotbarSlot(key) || key == Inventory.SLOT_OFFHAND;
    if (hotbar && player.getInventory().getItem(key).isEmpty() && slot.mayPickup(player)) {
      ItemStack taken = slot.remove(slot.getItem().getMaxStackSize());
      player.getInventory().setItem(key, taken);
      slot.onTake(player, taken);
    }
  }

  /** At most a normal stack leaves at a time, so it never asks to move the rest (2368). */
  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = slots.get(index);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }
    if (index < PLAYER_FIRST) {
      takeToPlayer(slot, player);
    } else {
      storeFromPlayer(slot);
    }
    return ItemStack.EMPTY;
  }

  private void takeToPlayer(Slot slot, Player player) {
    ItemStack stored = slot.getItem();
    int offered = Math.min(stored.getCount(), stored.getMaxStackSize());
    ItemStack moving = stored.copyWithCount(offered);
    moveItemStackTo(moving, PLAYER_FIRST, slots.size(), true);
    int moved = offered - moving.getCount();
    if (moved > 0) {
      slot.onTake(player, slot.remove(moved));
    }
  }

  /** The items of the mod to the storage, and redstone to the input, as the Dock (959). */
  private void storeFromPlayer(Slot slot) {
    ItemStack stack = slot.getItem();
    boolean moved =
        moveItemStackTo(stack, STORAGE_FIRST, PLAYER_FIRST, false)
            || moveItemStackTo(stack, 0, STORAGE_FIRST, false);
    if (moved && stack.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }
  }

  /** Only on the client: the slots of the closed tab are hidden, so they take no clicks. */
  public void showStorage(boolean shown) {
    storageShown = shown;
  }

  @Override
  boolean showsInput() {
    return !storageShown;
  }

  @Override
  public BlockName.Kind kind() {
    return BlockName.Kind.SERVER;
  }

  public RobotTier tier() {
    return ScreenEnums.of(RobotTier.values(), numbers.tier());
  }

  public ServerNumbers numbers() {
    return numbers;
  }

  public Energy sun() {
    return ScreenValues.energy(values(), ServerScreenValues.SUN);
  }

  public Energy sunMost() {
    return ScreenValues.energy(values(), ServerScreenValues.SUN_MOST);
  }

  public Energy banks() {
    return ScreenValues.energy(values(), ServerScreenValues.BANKS);
  }

  public Energy chunkCost() {
    return ScreenValues.energy(values(), ServerScreenValues.CHUNK_COST);
  }

  /** What the server spends for being on, now (answer 2435). */
  public Energy standby() {
    return ScreenValues.energy(values(), ServerScreenValues.STANDBY);
  }

  /** What it pays for its relay, now (answer 2436). */
  public Energy relayCost() {
    return ScreenValues.energy(values(), ServerScreenValues.RELAY_COST);
  }

  /** The chunk, the server and its relay together (answer 2440). */
  public Energy upkeep() {
    return chunkCost().plus(standby()).plus(relayCost());
  }

  public ChunkState chunkState() {
    return ScreenEnums.of(ChunkState.values(), values().get(ServerScreenValues.CHUNK_STATE));
  }

  public boolean chunkLoading() {
    return ScreenValues.isTrue(values(), ServerScreenValues.CHUNK_ON);
  }

  public int chunksHeld() {
    return values().get(ServerScreenValues.CHUNKS_HELD);
  }

  public UpsReserve ups() {
    return UpsReserve.of(values().get(ServerScreenValues.UPS));
  }

  public ServerStatus status() {
    return ServerStatus.of(energy(), capacity(), ups(), chunkLoading());
  }

  /** How many slots have something, on either side. */
  public int usedSlots() {
    int used = 0;
    for (int index = STORAGE_FIRST; index < PLAYER_FIRST; index++) {
      used += slots.get(index).hasItem() ? 1 : 0;
    }
    return used;
  }

  /** A Power Bank that is not full, so the screen says whether it charges or waits. */
  public boolean hasBankToCharge() {
    for (int index = STORAGE_FIRST; index < PLAYER_FIRST; index++) {
      if (ServerSlots.isBankToCharge(slots.get(index).getItem())) {
        return true;
      }
    }
    return false;
  }

  private record Boxes(Container input, ServerSlots storage) {}

  /** The numbers of the tier and of the config that the screen shows. */
  public record ServerNumbers(
      int tier, int storageSlots, int slotLimit, long upkeep, long resume, int perPlayer) {
    static ServerNumbers of(NetworkServerBlockEntity server) {
      ChunkKeys chunks = CubitMod.CONFIG.network().chunks();
      Energy standby = server.spec().standby().total(server.relay().isPresent());
      return new ServerNumbers(
          server.tier().ordinal(),
          server.storageSlots(),
          CubitMod.CONFIG.network().slot().limit(),
          chunks.perTick().plus(standby).amount(),
          chunks.price(standby).resume().amount(),
          chunks.limits().perPlayer());
    }

    static ServerNumbers read(RegistryFriendlyByteBuf data) {
      return new ServerNumbers(
          data.readVarInt(),
          data.readVarInt(),
          data.readVarInt(),
          data.readVarLong(),
          data.readVarLong(),
          data.readVarInt());
    }

    void write(RegistryFriendlyByteBuf data) {
      data.writeVarInt(tier);
      data.writeVarInt(storageSlots);
      data.writeVarInt(slotLimit);
      data.writeVarLong(upkeep);
      data.writeVarLong(resume);
      data.writeVarInt(perPlayer);
    }
  }
}
