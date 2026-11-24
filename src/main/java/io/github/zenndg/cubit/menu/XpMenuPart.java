package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

/**
 * The XP inside the robot menu: the tank, the mode, the switch of the excess and the trade with the
 * owner, a level each click (answers 1830 and 2070 to 2078). The server decides, and the client
 * reads the numbers.
 */
public final class XpMenuPart {
  /** After the buttons of the Organizer: − takes from the tank, and + fills it (answer 2076). */
  public static final int TAKE_BUTTON = 50;

  public static final int SEND_BUTTON = 51;
  public static final int EXCESS_BUTTON = 52;
  public static final int FIRST_MODE_BUTTON = 53;
  private static final int LAST_BUTTON = FIRST_MODE_BUTTON + XpMode.values().length - 1;
  private static final int FLUID = 0;
  private static final int CAPACITY = 2;
  private static final int RATE = 4;
  private static final int MODE = 5;
  private static final int EXCESS = 6;
  private static final int TRADES = 7;
  private static final int COUNT = 8;

  private final XpControl control;
  private final ContainerData values = new SimpleContainerData(COUNT);

  XpMenuPart(XpControl control) {
    this.control = control;
    values.set(RATE, 1);
  }

  ContainerData values() {
    return values;
  }

  /** On the server only, before the menu sends its changes. */
  void refresh() {
    IntHalves.put(values, FLUID, control.tank().fluid());
    IntHalves.put(values, CAPACITY, control.tank().capacity());
    values.set(RATE, CubitMod.CONFIG.xp().rate().fluidPerPoint());
    values.set(MODE, control.settings().mode().ordinal());
    values.set(EXCESS, control.settings().deleteExcess() ? 1 : 0);
    values.set(TRADES, control.trades() ? 1 : 0);
  }

  /** On the server: whether the Cubit has a whole point to unload (answer 2107). */
  boolean hasXp() {
    return !control.tank().isEmpty(CubitMod.CONFIG.xp().rate());
  }

  static boolean handles(int button) {
    return button >= TAKE_BUTTON && button <= LAST_BUTTON;
  }

  /** The XP moves only by the screen of the Cubit, never by the Tablet (answer 2075). */
  static boolean trades(int button) {
    return button == TAKE_BUTTON || button == SEND_BUTTON;
  }

  boolean press(Player player, int button) {
    return switch (button) {
      case TAKE_BUTTON -> control.receive(player);
      case SEND_BUTTON -> control.send(player);
      case EXCESS_BUTTON -> control.toggleExcess(player);
      default ->
          control.choose(player, ScreenEnums.of(XpMode.values(), button - FIRST_MODE_BUTTON));
    };
  }

  /** What the screens read, on the client, with a safe value before the first ones arrive. */
  public XpTank tank() {
    return new XpTank(
        IntHalves.joinPositive(values, FLUID), IntHalves.joinPositive(values, CAPACITY));
  }

  public XpRate rate() {
    return new XpRate(Math.max(1, values.get(RATE)));
  }

  public XpMode mode() {
    return ScreenEnums.of(XpMode.values(), values.get(MODE));
  }

  public boolean deleteExcess() {
    return values.get(EXCESS) != 0;
  }

  public boolean active() {
    return values.get(TRADES) != 0;
  }
}
