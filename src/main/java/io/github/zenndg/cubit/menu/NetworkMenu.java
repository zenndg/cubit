package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.NetworkBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.item.EnergySlotItems;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * What the screens of the blocks of the Network share: the battery, the input slot, the owner, the
 * Rename and the inventory of the player. The server decides who may keep it open (rule 20).
 */
public abstract class NetworkMenu extends AbstractContainerMenu implements RenamableMenu {
  public static final int SLOT_X = 152;
  public static final int SLOT_Y = 33;
  static final int SLOT_SIZE = 18;
  static final int FIRST_SLOT_X = 8;
  static final int SLOTS_PER_ROW = 9;
  static final int ENERGY = 0;
  static final int CAPACITY = 2;
  static final int INPUT = 4;
  static final int FIRST_FREE = 6;
  private static final int HOTBAR_GAP = 58;
  private static final int PLAYER_ROWS = 3;

  private final ContainerData values;
  private final Server server;
  private final Opening opening;

  NetworkMenu(
      MenuType<?> type, int containerId, ContainerData values, Server server, Opening opening) {
    super(type, containerId);
    this.values = values;
    this.server = server;
    this.opening = opening;
  }

  /** Where the energy of redstone and Power Banks goes in. */
  final void addInputSlot(Container slot) {
    addSlot(
        new Slot(slot, 0, SLOT_X, SLOT_Y) {
          @Override
          public boolean mayPlace(ItemStack stack) {
            return EnergySlotItems.accepts(stack);
          }

          @Override
          public boolean isActive() {
            return showsInput();
          }
        });
  }

  /** Whether the open tab has the input slot. */
  boolean showsInput() {
    return true;
  }

  final void addPlayerSlots(Inventory inventory, int top) {
    for (int row = 0; row < PLAYER_ROWS; row++) {
      for (int column = 0; column < SLOTS_PER_ROW; column++) {
        int index = column + (row + 1) * SLOTS_PER_ROW;
        addSlot(new Slot(inventory, index, slotX(column), top + row * SLOT_SIZE));
      }
    }
    for (int column = 0; column < SLOTS_PER_ROW; column++) {
      addSlot(new Slot(inventory, column, slotX(column), top + HOTBAR_GAP));
    }
  }

  static int slotX(int column) {
    return FIRST_SLOT_X + column * SLOT_SIZE;
  }

  @Override
  public void broadcastChanges() {
    server.refresh().run();
    super.broadcastChanges();
  }

  final ContainerData values() {
    return values;
  }

  public Energy energy() {
    return ScreenValues.energy(values, ENERGY);
  }

  public Energy capacity() {
    return ScreenValues.energy(values, CAPACITY);
  }

  public Energy input() {
    return ScreenValues.energy(values, INPUT);
  }

  public Energy inputLimit() {
    return new Energy(Math.max(opening.inputLimit(), 0));
  }

  /** Empty when the server does not know the name of the owner. */
  public Optional<String> ownerName() {
    return opening.ownerName().isEmpty() ? Optional.empty() : Optional.of(opening.ownerName());
  }

  public UUID networkId() {
    return opening.id();
  }

  /** The name the block had when the screen opened. */
  public String networkName() {
    return opening.name();
  }

  @Override
  public boolean stillValid(Player player) {
    return server.stillValid().test(player);
  }

  @Override
  public boolean rename(Player player, String typed) {
    return stillValid(player) && server.rename().test(typed);
  }

  /** What only the server does: check the player, read the block and name it. */
  record Server(Predicate<Player> stillValid, Runnable refresh, Predicate<String> rename) {
    static final Server NONE = new Server(player -> true, () -> {}, typed -> false);

    static Server of(NetworkBlockEntity block, ScreenValues values) {
      values.refresh();
      return new Server(
          player ->
              !block.isRemoved()
                  && block.isOwner(player)
                  && player.canInteractWithBlock(
                      block.getBlockPos(), Container.DEFAULT_DISTANCE_BUFFER),
          values::refresh,
          typed -> block.identity().rename(typed));
    }
  }

  /** What travels once, when the screen opens, because it does not change while it is open. */
  record Opening(String ownerName, long inputLimit, UUID id, String name) {
    private static final String NO_NAME = "";
    static final Opening NONE = new Opening(NO_NAME, 0, new UUID(0L, 0L), NO_NAME);

    static Opening of(NetworkBlockEntity block) {
      return new Opening(
          block.ownerName().orElse(NO_NAME),
          block.inputPerTick().amount(),
          block.identity().id(),
          block.identity().name().value());
    }

    static Opening read(RegistryFriendlyByteBuf data) {
      return new Opening(data.readUtf(), data.readVarLong(), data.readUUID(), data.readUtf());
    }

    void write(RegistryFriendlyByteBuf data) {
      data.writeUtf(ownerName);
      data.writeVarLong(inputLimit);
      data.writeUUID(id);
      data.writeUtf(name);
    }
  }

  /** The kind of the block, for its standard name. */
  public abstract BlockName.Kind kind();
}
