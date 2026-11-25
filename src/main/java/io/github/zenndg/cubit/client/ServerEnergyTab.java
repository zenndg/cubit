package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.HoldTime;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.NetworkServerMenu;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/** The Energy tab of the Network Server: the flow, the chunk with its switch and the UPS (2397). */
final class ServerEnergyTab {
  private static final int FLOW_Y = 59;
  private static final Box NET = new Box(8, FLOW_Y, 160, NetworkScreen.LINE);
  private static final int FLOW_ROWS = 72;
  private static final int CHUNK_Y = 108;
  private static final int SWITCH_ROW = 120;
  private static final int SWITCH_HEIGHT = 13;
  private static final int SWITCH_X = 139;
  private static final int SWITCH_Y = 121;
  private static final Box SWITCH_NAME = new Box(11, 124, 124, NetworkScreen.LINE);
  private static final Box CHUNK_LINE = new Box(11, 135, 156, NetworkScreen.DETAIL_LINE);
  private static final int UPS_ROW = 141;
  private static final int UPS_HEIGHT = 14;
  private static final Box UPS_NAME = new Box(11, 145, 40, NetworkScreen.LINE);
  private static final Box LESS = new Box(52, 142, 12, 12);
  private static final Box MORE = new Box(90, 142, 12, 12);
  private static final int UPS_CENTER = 77;
  private static final int UPS_TEXT = 145;
  private static final Box UPS_TIME = new Box(105, 146, 60, NetworkScreen.DETAIL_LINE);
  private static final String MINUS = "−";
  private static final String PLUS = "+";

  private final NetworkServerScreen screen;
  private final NetworkServerMenu menu;

  ServerEnergyTab(NetworkServerScreen screen, NetworkServerMenu menu) {
    this.screen = screen;
    this.menu = menu;
  }

  void renderFrames(GuiGraphics graphics, int left, int top) {
    NetworkScreen.inputFrames(graphics, left, top);
    NetworkScreen.row(graphics, left, top + FLOW_ROWS, NetworkScreen.ROW_HEIGHT);
    NetworkScreen.row(
        graphics, left, top + FLOW_ROWS + NetworkScreen.ROW_HEIGHT * 2, NetworkScreen.ROW_HEIGHT);
    NetworkScreen.row(graphics, left, top + SWITCH_ROW, SWITCH_HEIGHT);
    NetworkScreen.row(graphics, left, top + UPS_ROW, UPS_HEIGHT);
  }

  void render(GuiGraphics graphics, int mouseX, int mouseY) {
    screen.renderBattery(graphics, menu.chunkLoading() ? menu.ups() : UpsReserve.NONE);
    renderFlow(graphics);
    renderChunk(graphics);
    renderUps(graphics, mouseX, mouseY);
    screen.renderOwner(graphics);
  }

  private void renderFlow(GuiGraphics graphics) {
    XpHeading.render(graphics, screen.painter, NetworkKeys.FLOW, FLOW_Y, NetworkScreen.ROW_RIGHT);
    long net = menu.sun().amount() - menu.chunkCost().amount() - menu.banks().amount();
    Energy size = new Energy(Math.abs(net));
    screen.painter.textRight(
        graphics,
        Component.translatable(
            net < 0 ? NetworkKeys.LOSS : NetworkKeys.GAIN, EnergyFormat.rate(size)),
        NET,
        ScreenPainter.DETAIL,
        ScreenPainter.FULL_SCALE);
    Component sun =
        Component.translatable(
            NetworkKeys.SUN_RATE, EnergyFormat.rate(menu.sun()), EnergyFormat.rate(menu.sunMost()));
    int color = menu.sun().isZero() ? ScreenPainter.DETAIL : ScreenPainter.STATUS_GREEN;
    screen.rowPair(graphics, FLOW_ROWS, NetworkKeys.SUN, sun, color);
    renderCost(graphics, 1, NetworkKeys.CHUNK_COST, menu.chunkCost());
    renderCost(graphics, 2, NetworkKeys.BANKS, menu.banks());
  }

  private void renderCost(GuiGraphics graphics, int row, String key, Energy cost) {
    Component value =
        cost.isZero()
            ? Component.translatable(NetworkKeys.NOTHING)
            : Component.translatable(NetworkKeys.LOSS, EnergyFormat.rate(cost));
    int color = cost.isZero() ? ScreenPainter.DETAIL : ScreenPainter.STATUS_ORANGE;
    screen.rowPair(graphics, FLOW_ROWS + row * NetworkScreen.ROW_HEIGHT, key, value, color);
  }

  private void renderChunk(GuiGraphics graphics) {
    XpHeading.render(graphics, screen.painter, NetworkKeys.CHUNK, CHUNK_Y, NetworkScreen.ROW_RIGHT);
    screen.painter.text(
        graphics,
        Component.translatable(NetworkKeys.LOAD_CHUNK),
        SWITCH_NAME,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    ScreenToggle.draw(graphics, screen.painter, SWITCH_X, SWITCH_Y, menu.chunkLoading(), !locked());
    ChunkState state = menu.chunkState();
    screen.detail(graphics, chunkLine(state), CHUNK_LINE, colorOf(state));
  }

  private boolean locked() {
    return menu.chunkState() == ChunkState.LOCKED;
  }

  private Component chunkLine(ChunkState state) {
    NetworkServerMenu.ServerNumbers numbers = menu.numbers();
    return switch (state) {
      case HOLDING ->
          numbers.perPlayer() == 0
              ? Component.translatable(NetworkKeys.HOLDING_WITHOUT_LIMIT, menu.chunksHeld())
              : Component.translatable(
                  NetworkKeys.chunk(state), menu.chunksHeld(), numbers.perPlayer());
      case NO_ENERGY ->
          Component.translatable(
              NetworkKeys.chunk(state), EnergyFormat.compact(new Energy(numbers.resume())));
      case PLAYER_LIMIT -> Component.translatable(NetworkKeys.chunk(state), numbers.perPlayer());
      default -> Component.translatable(NetworkKeys.chunk(state));
    };
  }

  private static int colorOf(ChunkState state) {
    return switch (state) {
      case HOLDING -> ScreenPainter.STATUS_GREEN;
      case NO_ENERGY -> ScreenPainter.STATUS_RED;
      case PLAYER_LIMIT, WORLD_LIMIT, OWNER_OFFLINE -> ScreenPainter.STATUS_ORANGE;
      case LOCKED, SWITCHED_OFF -> ScreenPainter.DETAIL;
    };
  }

  private void renderUps(GuiGraphics graphics, int mouseX, int mouseY) {
    UpsReserve ups = menu.ups();
    boolean on = menu.chunkLoading() && !locked();
    screen.painter.text(
        graphics,
        Component.translatable(NetworkKeys.UPS),
        UPS_NAME,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    Component value =
        Component.translatable(NetworkKeys.UPS_VALUE, ups.percent()).withStyle(ChatFormatting.BOLD);
    int x = UPS_CENTER - screen.painter.width(value, ScreenPainter.FULL_SCALE) / 2;
    screen.painter.text(graphics, value, x, UPS_TEXT, ScreenPainter.TEXT, ScreenPainter.FULL_SCALE);
    button(graphics, LESS, MINUS, on && !ups.isNone(), mouseX, mouseY);
    button(graphics, MORE, PLUS, on && ups.percent() < UpsReserve.MAX, mouseX, mouseY);
    renderUpsTime(graphics, ups, on);
  }

  private static void button(
      GuiGraphics graphics, Box box, String label, boolean on, int mouseX, int mouseY) {
    FlatButton.paint(
        graphics, box, Component.literal(label), !on, on && box.contains(mouseX, mouseY));
  }

  private void renderUpsTime(GuiGraphics graphics, UpsReserve ups, boolean on) {
    boolean drops = on && ups.isNone();
    Component text = Component.translatable(NetworkKeys.NONE);
    if (drops) {
      text = Component.translatable(NetworkKeys.MAY_DROP);
    } else if (on) {
      text = time(ups);
    }
    screen.painter.textRight(
        graphics,
        text,
        UPS_TIME,
        drops ? ScreenPainter.STATUS_RED : ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  private Component time(UpsReserve ups) {
    Energy price = new Energy(menu.numbers().upkeep());
    if (price.isZero()) {
      return Component.translatable(NetworkKeys.NONE);
    }
    HoldTime time = HoldTime.of(ups.holdTicks(menu.capacity(), price));
    return time.hours() == 0
        ? Component.translatable(NetworkKeys.HOLDS_MINUTES, time.minutes())
        : Component.translatable(NetworkKeys.HOLDS_HOURS, time.hours(), time.minutes());
  }

  /** The button of the server under a point of the window, if it can be pressed. */
  OptionalInt buttonAt(double x, double y) {
    if (locked()) {
      return OptionalInt.empty();
    }
    if (ScreenToggle.contains(SWITCH_X, SWITCH_Y, x, y)) {
      return OptionalInt.of(NetworkServerMenu.CHUNK_BUTTON);
    }
    if (menu.chunkLoading() && LESS.contains((int) x, (int) y)) {
      return OptionalInt.of(NetworkServerMenu.UPS_LESS_BUTTON);
    }
    return menu.chunkLoading() && MORE.contains((int) x, (int) y)
        ? OptionalInt.of(NetworkServerMenu.UPS_MORE_BUTTON)
        : OptionalInt.empty();
  }
}
