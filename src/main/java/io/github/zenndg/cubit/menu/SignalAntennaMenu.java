package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.config.ChunkKeys;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.domain.network.AntennaSpec;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.registry.CubitMenus;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The screen of a Signal Antenna, without tabs: its channel, its Server, its signal and what it
 * spends, the three switches and the reserve (answers 2548 and 2559). The server checks the owner
 * and the distance of every press (rule 20).
 */
public final class SignalAntennaMenu extends NetworkMenu {
  public static final int PLAYER_Y = 194;
  public static final int ENABLE_BUTTON = 0;
  public static final int CHUNK_BUTTON = 1;
  public static final int WALL_BUTTON = 2;
  public static final int UPS_LESS_BUTTON = 3;
  public static final int UPS_MORE_BUTTON = 4;
  public static final int CHANNEL_BACK_BUTTON = 5;
  public static final int CHANNEL_FORWARD_BUTTON = 6;
  private static final int PLAYER_FIRST = 1;
  private static final int TICKS_PER_MINUTE = 1200;

  private final AntennaControl control;
  private final AntennaNumbers numbers;

  private SignalAntennaMenu(
      int containerId,
      Inventory inventory,
      Container slot,
      ContainerData values,
      Server server,
      AntennaControl control,
      Opening opening,
      AntennaNumbers numbers) {
    super(CubitMenus.SIGNAL_ANTENNA.get(), containerId, values, server, opening);
    this.control = control;
    this.numbers = numbers;
    addInputSlot(slot);
    addDataSlots(values);
    addPlayerSlots(inventory, PLAYER_Y);
  }

  public static SignalAntennaMenu onServer(
      int containerId, Inventory inventory, SignalAntennaBlockEntity antenna) {
    AntennaScreenValues values = new AntennaScreenValues(antenna);
    return new SignalAntennaMenu(
        containerId,
        inventory,
        antenna.slot(),
        values,
        Server.of(antenna, values),
        new AntennaControl(antenna),
        Opening.NONE,
        AntennaNumbers.of(antenna));
  }

  public static SignalAntennaMenu onClient(
      int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
    Opening opening = Opening.read(data);
    return new SignalAntennaMenu(
        containerId,
        inventory,
        new SimpleContainer(1),
        new SimpleContainerData(AntennaScreenValues.COUNT),
        Server.NONE,
        AntennaControl.NONE,
        opening,
        AntennaNumbers.read(data));
  }

  public static void writeOpening(RegistryFriendlyByteBuf data, SignalAntennaBlockEntity antenna) {
    Opening.of(antenna).write(data);
    AntennaNumbers.of(antenna).write(data);
  }

  /** Every press is checked again, for any other caller (rule 20). */
  @Override
  public boolean clickMenuButton(Player player, int button) {
    return stillValid(player) && control.press(button);
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
    return BlockName.Kind.ANTENNA;
  }

  public AntennaNumbers numbers() {
    return numbers;
  }

  public Optional<AntennaChannel> channel() {
    int value = values().get(AntennaScreenValues.CHANNEL);
    return value == AntennaScreenValues.NONE
        ? Optional.empty()
        : Optional.of(ScreenEnums.of(AntennaChannel.values(), value - 1));
  }

  public AntennaSignal.State signal() {
    return ScreenEnums.of(AntennaSignal.State.values(), values().get(AntennaScreenValues.SIGNAL));
  }

  public int minutesLeft() {
    return values().get(AntennaScreenValues.MINUTES_LEFT);
  }

  public long distance() {
    return ScreenValues.energy(values(), AntennaScreenValues.DISTANCE).amount();
  }

  public Energy spending() {
    return ScreenValues.energy(values(), AntennaScreenValues.SPENDING);
  }

  public boolean enabled() {
    return ScreenValues.isTrue(values(), AntennaScreenValues.ENABLED);
  }

  public boolean chunkLoading() {
    return ScreenValues.isTrue(values(), AntennaScreenValues.CHUNK_ON);
  }

  public boolean showsWall() {
    return ScreenValues.isTrue(values(), AntennaScreenValues.WALL);
  }

  public ChunkState chunkState() {
    return ScreenEnums.of(ChunkState.values(), values().get(AntennaScreenValues.CHUNK_STATE));
  }

  public int chunksHeld() {
    return values().get(AntennaScreenValues.CHUNKS_HELD);
  }

  public UpsReserve ups() {
    return UpsReserve.of(values().get(AntennaScreenValues.UPS));
  }

  /** The tier of its Server, while the data of the world knows it. */
  public Optional<RobotTier> serverTier() {
    int value = values().get(AntennaScreenValues.SERVER_TIER);
    return value == AntennaScreenValues.NONE
        ? Optional.empty()
        : Optional.of(ScreenEnums.of(RobotTier.values(), value - 1));
  }

  /**
   * What travels once, when the screen opens: the name its Server had at the link, the chunks a
   * player may hold, the charge that takes its chunk back and the minutes it works without signal.
   */
  public record AntennaNumbers(String serverName, int perPlayer, long resume, int lossMinutes) {
    private static final String NO_SERVER = "";

    static AntennaNumbers of(SignalAntennaBlockEntity antenna) {
      AntennaSpec spec = CubitMod.CONFIG.network().antennas().spec();
      ChunkKeys chunks = CubitMod.CONFIG.network().chunks();
      int loss = spec.signalLossTicks();
      return new AntennaNumbers(
          antenna.link().serverName().orElse(NO_SERVER),
          chunks.limits().perPlayer(),
          chunks.price(spec.standby()).resume().amount(),
          (loss + TICKS_PER_MINUTE - 1) / TICKS_PER_MINUTE);
    }

    static AntennaNumbers read(RegistryFriendlyByteBuf data) {
      return new AntennaNumbers(
          data.readUtf(), data.readVarInt(), data.readVarLong(), data.readVarInt());
    }

    void write(RegistryFriendlyByteBuf data) {
      data.writeUtf(serverName);
      data.writeVarInt(perPlayer);
      data.writeVarLong(resume);
      data.writeVarInt(lossMinutes);
    }
  }
}
