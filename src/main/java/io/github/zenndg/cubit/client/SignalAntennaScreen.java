package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.AntennaPlacing;
import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.AntennaScreenKeys;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.SignalAntennaMenu;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;

/**
 * The screen of the Signal Antenna, as the approved preview (answers 2548 and 2559): its channel,
 * its Server, its signal and what it spends, the three switches, the chunk and the reserve.
 */
final class SignalAntennaScreen extends NetworkScreen<SignalAntennaMenu> {
  private static final int HEIGHT = 276;
  private static final int OWNER_Y = 167;
  private static final Box TITLE = new Box(8, 6, 90, LINE);
  private static final int CHANNEL_Y = 59;
  private static final int SERVER_Y = 70;
  private static final int SIGNAL_Y = 81;
  private static final int SPEND_Y = 92;
  private static final int DIVIDER_Y = 104;
  private static final int SWITCH_HEIGHT = 12;
  private static final int ON_Y = 107;
  private static final int CHUNK_Y = 119;
  private static final int WALL_Y = 131;
  private static final int SWITCH_X = 139;
  private static final int SWITCH_LABEL_X = 11;
  private static final Box CHUNK_LINE = new Box(11, 145, 156, DETAIL_LINE);
  private static final int UPS_ROW = 152;
  private static final int UPS_HEIGHT = 14;
  private static final Box UPS_NAME = new Box(11, 156, 40, LINE);
  private static final Box LESS = new Box(52, 153, 12, 12);
  private static final Box MORE = new Box(90, 153, 12, 12);
  private static final int UPS_CENTER = 77;
  private static final int UPS_TEXT = 156;
  private static final Box UPS_TIME = new Box(105, 157, 60, DETAIL_LINE);
  private static final Box FORWARD = new Box(153, CHANNEL_Y, 12, ROW_HEIGHT);
  private static final int NAME_RIGHT = 149;
  private static final int SWATCH = 7;
  private static final int SWATCH_GAP = 3;
  private static final int BACK_GAP = 15;
  private static final int BACK_WIDTH = 12;
  private static final int OPAQUE = 0xFF000000;
  private static final String MINUS = "−";
  private static final String PLUS = "+";
  private static final String BACK_ARROW = "<";
  private static final String FORWARD_ARROW = ">";
  private static final String GROUPED = "%,d";

  /** Where the back arrow was drawn, which moves with the width of the name of the channel. */
  private Box backBox = new Box(0, CHANNEL_Y, 0, 0);

  SignalAntennaScreen(SignalAntennaMenu menu, Inventory inventory, Component title) {
    super(
        menu,
        inventory,
        title,
        new Layout(HEIGHT, SignalAntennaMenu.PLAYER_Y, OWNER_Y, AntennaScreenKeys.RENAME));
  }

  @Override
  boolean showsOwner() {
    return true;
  }

  @Override
  void renderFrames(GuiGraphics graphics, int left, int top) {
    inputFrames(graphics, left, top);
    row(graphics, left, top + CHANNEL_Y, ROW_HEIGHT);
    row(graphics, left, top + SIGNAL_Y, ROW_HEIGHT);
    graphics.fill(
        left + ROW_LEFT,
        top + DIVIDER_Y,
        left + ROW_RIGHT,
        top + DIVIDER_Y + 1,
        ScreenPainter.SHADE);
    graphics.fill(
        left + ROW_LEFT,
        top + DIVIDER_Y + 1,
        left + ROW_RIGHT,
        top + DIVIDER_Y + 2,
        ScreenPainter.WHITE);
    row(graphics, left, top + ON_Y, SWITCH_HEIGHT);
    row(graphics, left, top + WALL_Y, SWITCH_HEIGHT);
    row(graphics, left, top + UPS_ROW, UPS_HEIGHT);
  }

  @Override
  void renderTab(GuiGraphics graphics, int mouseX, int mouseY) {
    int color = AntennaTexts.color(menu);
    bold(graphics, rename.name(), TITLE);
    painter.status(graphics, AntennaTexts.status(menu), STATUS, color);
    renderBattery(graphics, menu.enabled() ? menu.ups() : UpsReserve.NONE);
    renderChannel(graphics, mouseX, mouseY);
    rowPair(graphics, SERVER_Y, AntennaScreenKeys.SERVER, server(), serverColor());
    rowPair(graphics, SIGNAL_Y, AntennaScreenKeys.SIGNAL, AntennaTexts.signal(menu), color);
    renderSpending(graphics);
    renderSwitch(graphics, ON_Y, AntennaScreenKeys.ON, menu.enabled(), true);
    renderSwitch(graphics, CHUNK_Y, NetworkKeys.LOAD_CHUNK, menu.chunkLoading(), !locked());
    renderSwitch(graphics, WALL_Y, AntennaScreenKeys.SHOW_WALL, menu.showsWall(), true);
    detail(graphics, AntennaTexts.chunk(menu), CHUNK_LINE, AntennaTexts.chunkColor(menu));
    renderUps(graphics, mouseX, mouseY);
    renderOwner(graphics);
  }

  /** The arrows, the swatch in the color of the channel and its name (answer 2309). */
  private void renderChannel(GuiGraphics graphics, int mouseX, int mouseY) {
    painter.text(
        graphics,
        Component.translatable(AntennaScreenKeys.CHANNEL),
        ROW_LEFT + ROW_INSET,
        CHANNEL_Y + ROW_TEXT,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    Optional<AntennaChannel> channel = menu.channel();
    Component name =
        channel
            .map(AntennaPlacing::colorOf)
            .orElseGet(() -> Component.translatable(NetworkKeys.NONE))
            .copy()
            .withStyle(ChatFormatting.BOLD);
    int nameWidth = painter.width(name, ScreenPainter.FULL_SCALE);
    painter.text(
        graphics,
        name,
        NAME_RIGHT - nameWidth,
        CHANNEL_Y + ROW_TEXT,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    int swatch = NAME_RIGHT - nameWidth - SWATCH_GAP - SWATCH;
    channel.ifPresent(shown -> paintSwatch(graphics, swatch, shown));
    boolean moves = channel.isPresent();
    backBox = new Box(swatch - BACK_GAP, CHANNEL_Y, BACK_WIDTH, ROW_HEIGHT);
    arrow(graphics, backBox, BACK_ARROW, moves, mouseX, mouseY);
    arrow(graphics, FORWARD, FORWARD_ARROW, moves, mouseX, mouseY);
  }

  private static void paintSwatch(GuiGraphics graphics, int x, AntennaChannel channel) {
    int y = CHANNEL_Y + (ROW_HEIGHT - SWATCH) / 2;
    graphics.fill(x, y, x + SWATCH, y + SWATCH, OPAQUE);
    int color = DyeColor.byName(channel.key(), DyeColor.WHITE).getTextureDiffuseColor();
    graphics.fill(x + 1, y + 1, x + SWATCH - 1, y + SWATCH - 1, OPAQUE | color);
  }

  private static void arrow(
      GuiGraphics graphics, Box box, String label, boolean on, int mouseX, int mouseY) {
    FlatButton.paint(
        graphics, box, Component.literal(label), !on, on && box.contains(mouseX, mouseY));
  }

  /** Its Server with its tier, while the data of the world knows it. */
  private Component server() {
    String name = menu.numbers().serverName();
    return menu.serverTier()
        .<Component>map(tier ->
            Component.translatable(AntennaScreenKeys.SERVER_VALUE, name, TierLook.name(tier)))
        .orElseGet(() -> Component.translatable(AntennaScreenKeys.NO_SERVER));
  }

  private int serverColor() {
    return menu.serverTier().isPresent() ? ScreenPainter.STATUS_GREEN : ScreenPainter.STATUS_ORANGE;
  }

  /** The distance to its Server and what it spends now, with the chunk (answer 2310). */
  private void renderSpending(GuiGraphics graphics) {
    String blocks = String.format(Locale.ROOT, GROUPED, menu.distance());
    Energy spent = menu.spending();
    Component value =
        spent.isZero()
            ? Component.translatable(AntennaScreenKeys.SPEND_NONE, blocks)
            : Component.translatable(
                AntennaScreenKeys.SPEND_VALUE, blocks, EnergyFormat.rate(spent));
    int color = spent.isZero() ? ScreenPainter.DETAIL : ScreenPainter.STATUS_ORANGE;
    rowPair(graphics, SPEND_Y, AntennaScreenKeys.SPENDING, value, color);
  }

  private void renderSwitch(GuiGraphics graphics, int y, String key, boolean on, boolean free) {
    painter.text(
        graphics,
        Component.translatable(key),
        SWITCH_LABEL_X,
        y + ROW_TEXT,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    ScreenToggle.draw(graphics, painter, SWITCH_X, y + 1, on, free);
  }

  private boolean locked() {
    return menu.chunkState() == ChunkState.LOCKED;
  }

  private void renderUps(GuiGraphics graphics, int mouseX, int mouseY) {
    UpsReserve ups = menu.ups();
    boolean on = menu.enabled();
    painter.text(
        graphics,
        Component.translatable(NetworkKeys.UPS),
        UPS_NAME,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    Component value =
        Component.translatable(NetworkKeys.UPS_VALUE, ups.percent()).withStyle(ChatFormatting.BOLD);
    int x = UPS_CENTER - painter.width(value, ScreenPainter.FULL_SCALE) / 2;
    painter.text(graphics, value, x, UPS_TEXT, ScreenPainter.TEXT, ScreenPainter.FULL_SCALE);
    arrow(graphics, LESS, MINUS, on && !ups.isNone(), mouseX, mouseY);
    arrow(graphics, MORE, PLUS, on && ups.percent() < UpsReserve.MAX, mouseX, mouseY);
    if (on) {
      boolean drops = ups.isNone();
      painter.textRight(
          graphics,
          drops ? Component.translatable(NetworkKeys.MAY_DROP) : AntennaTexts.holds(menu),
          UPS_TIME,
          drops ? ScreenPainter.STATUS_RED : ScreenPainter.DETAIL,
          ScreenPainter.DETAIL_SCALE);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (rename.isOpen()) {
      return super.mouseClicked(mouseX, mouseY, button);
    }
    OptionalInt pressed = buttonAt(mouseX - leftPos, mouseY - topPos);
    if (pressed.isPresent()) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, pressed.getAsInt());
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  /** The button under a point of the window, if it can be pressed now. */
  private OptionalInt buttonAt(double x, double y) {
    if (ScreenToggle.contains(SWITCH_X, ON_Y + 1, x, y)) {
      return OptionalInt.of(SignalAntennaMenu.ENABLE_BUTTON);
    }
    if (!locked() && ScreenToggle.contains(SWITCH_X, CHUNK_Y + 1, x, y)) {
      return OptionalInt.of(SignalAntennaMenu.CHUNK_BUTTON);
    }
    if (ScreenToggle.contains(SWITCH_X, WALL_Y + 1, x, y)) {
      return OptionalInt.of(SignalAntennaMenu.WALL_BUTTON);
    }
    return channelOrUps((int) x, (int) y);
  }

  private OptionalInt channelOrUps(int x, int y) {
    if (menu.channel().isPresent() && FORWARD.contains(x, y)) {
      return OptionalInt.of(SignalAntennaMenu.CHANNEL_FORWARD_BUTTON);
    }
    if (menu.channel().isPresent() && backBox.contains(x, y)) {
      return OptionalInt.of(SignalAntennaMenu.CHANNEL_BACK_BUTTON);
    }
    if (menu.enabled() && LESS.contains(x, y)) {
      return OptionalInt.of(SignalAntennaMenu.UPS_LESS_BUTTON);
    }
    return menu.enabled() && MORE.contains(x, y)
        ? OptionalInt.of(SignalAntennaMenu.UPS_MORE_BUTTON)
        : OptionalInt.empty();
  }
}
