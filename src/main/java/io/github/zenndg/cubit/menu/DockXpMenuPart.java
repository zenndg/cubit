package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.xp.XpRate;
import io.github.zenndg.cubit.domain.xp.XpTank;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

/**
 * The XP inside the dock menu: the tank, the mode, the energy it made and the trade with the owner,
 * a level each click (answers 1837, 2072 and 2109). The server decides, and the client reads the
 * numbers.
 */
public final class DockXpMenuPart {
  public static final int TAKE_BUTTON = 1;
  public static final int SEND_BUTTON = 2;
  public static final int FIRST_MODE_BUTTON = 3;
  private static final int LAST_BUTTON = FIRST_MODE_BUTTON + DockXpMode.values().length - 1;
  private static final int FLUID = 0;
  private static final int CAPACITY = 2;
  private static final int RATE = 4;
  private static final int MODE = 5;
  private static final int MADE_LOW = 6;
  private static final int MADE_HIGH = 8;
  private static final int COUNT = 10;

  private final DockXpControl control;
  private final ContainerData values = new SimpleContainerData(COUNT);

  DockXpMenuPart(DockXpControl control) {
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
    values.set(MODE, control.mode().ordinal());
    long made = control.energyMade().amount();
    IntHalves.put(values, MADE_LOW, (int) made);
    IntHalves.put(values, MADE_HIGH, (int) (made >>> Integer.SIZE));
  }

  static boolean handles(int button) {
    return button >= TAKE_BUTTON && button <= LAST_BUTTON;
  }

  boolean press(Player player, int button) {
    return switch (button) {
      case TAKE_BUTTON -> control.receive(player);
      case SEND_BUTTON -> control.send(player);
      default ->
          control.choose(player, ScreenEnums.of(DockXpMode.values(), button - FIRST_MODE_BUTTON));
    };
  }

  /** What the screen reads, on the client, with a safe value before the first ones arrive. */
  public XpTank tank() {
    return new XpTank(
        IntHalves.joinPositive(values, FLUID), IntHalves.joinPositive(values, CAPACITY));
  }

  public XpRate rate() {
    return new XpRate(Math.max(1, values.get(RATE)));
  }

  public DockXpMode mode() {
    return ScreenEnums.of(DockXpMode.values(), values.get(MODE));
  }

  public Energy energyMade() {
    long high = (long) IntHalves.join(values, MADE_HIGH) << Integer.SIZE;
    long low = Integer.toUnsignedLong(IntHalves.join(values, MADE_LOW));
    return new Energy(Math.max(0, high | low));
  }
}
