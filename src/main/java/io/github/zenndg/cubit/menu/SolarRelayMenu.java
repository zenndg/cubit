package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The screen of a Solar Relay, without tabs (answers 2280 and 2397). */
public final class SolarRelayMenu extends NetworkMenu {
  public static final int PLAYER_Y = 158;
  private static final int PLAYER_FIRST = 1;
  private static final String NO_SERVER = "";

  private final String serverName;

  private SolarRelayMenu(
      int containerId,
      Inventory inventory,
      Container slot,
      ContainerData values,
      Server server,
      Opening opening,
      String serverName) {
    super(CubitMenus.SOLAR_RELAY.get(), containerId, values, server, opening);
    this.serverName = serverName;
    addInputSlot(slot);
    addDataSlots(values);
    addPlayerSlots(inventory, PLAYER_Y);
  }

  public static SolarRelayMenu onServer(
      int containerId, Inventory inventory, SolarRelayBlockEntity relay) {
    RelayScreenValues values = new RelayScreenValues(relay);
    return new SolarRelayMenu(
        containerId,
        inventory,
        relay.slot(),
        values,
        Server.of(relay, values),
        Opening.NONE,
        nameOf(relay));
  }

  public static SolarRelayMenu onClient(
      int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
    return new SolarRelayMenu(
        containerId,
        inventory,
        new SimpleContainer(1),
        new SimpleContainerData(RelayScreenValues.COUNT),
        Server.NONE,
        Opening.read(data),
        data.readUtf());
  }

  public static void writeOpening(RegistryFriendlyByteBuf data, SolarRelayBlockEntity relay) {
    Opening.of(relay).write(data);
    data.writeUtf(nameOf(relay));
  }

  private static String nameOf(SolarRelayBlockEntity relay) {
    return relay
        .server()
        .map(NetworkServerBlockEntity::identity)
        .map(id -> id.name().value())
        .orElse(NO_SERVER);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = slots.get(index);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = slot.getItem();
    ItemStack moved = stack.copy();
    boolean fits =
        index < PLAYER_FIRST
            ? moveItemStackTo(stack, PLAYER_FIRST, slots.size(), true)
            : moveItemStackTo(stack, 0, PLAYER_FIRST, false);
    if (!fits) {
      return ItemStack.EMPTY;
    }
    if (stack.isEmpty()) {
      slot.setByPlayer(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }
    return moved;
  }

  @Override
  public BlockName.Kind kind() {
    return BlockName.Kind.RELAY;
  }

  /** The name of the server as the screen opened, empty when there was none. */
  public String serverName() {
    return serverName;
  }

  public boolean paired() {
    return ScreenValues.isTrue(values(), RelayScreenValues.PAIRED);
  }

  public RobotTier tier() {
    return ScreenEnums.of(RobotTier.values(), values().get(RelayScreenValues.TIER));
  }

  public SunState sky() {
    return ScreenEnums.of(SunState.values(), values().get(RelayScreenValues.SKY));
  }

  public PanelState panel() {
    return ScreenEnums.of(PanelState.values(), values().get(RelayScreenValues.PANEL));
  }

  public Energy now() {
    return ScreenValues.energy(values(), RelayScreenValues.NOW);
  }

  public Energy most() {
    return ScreenValues.energy(values(), RelayScreenValues.MOST);
  }
}
