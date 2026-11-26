package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.client.ScreenPainter.Colors;
import io.github.zenndg.cubit.domain.dock.DockStatus;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** The Dock tab of the dock screen, as in the concept image, in its three states and two more. */
final class DockMainTab {
  private static final int MARGIN = 8;
  private static final int LINE = 9;
  private static final int DETAIL_LINE = 7;
  private static final Box TITLE = new Box(8, 6, 92, LINE);
  private static final Box STATUS = new Box(100, 6, 68, LINE);
  private static final int PICTURE_X = 8;
  private static final int TOWER_Y = 17;
  private static final int ROBOT_Y = 73;
  private static final Box STORED = new Box(50, 18, 96, LINE);
  private static final Box STOCK_BAR = new Box(50, 26, 96, 7);
  private static final Box INPUT = new Box(50, 36, 100, DETAIL_LINE);
  private static final Box LIMIT = new Box(50, 43, 100, DETAIL_LINE);
  private static final Box NETWORK = new Box(50, 50, 100, DETAIL_LINE);
  private static final int BOLT_X = 156;
  private static final int BOLT_Y = 18;
  private static final int SLOT_FRAME_X = InductionDockMenu.SLOT_X - 1;
  private static final int SLOT_FRAME_Y = InductionDockMenu.SLOT_Y - 1;
  private static final int REDSTONE_CENTER = 160;
  private static final int REDSTONE_Y = 53;
  private static final Box DOCKED = new Box(8, 60, 160, LINE);
  private static final int RULE_Y = 69;
  private static final Box SECTION = new Box(50, 74, 116, LINE);
  private static final Box BATTERY_BAR = new Box(50, 82, 116, 6);
  private static final Box HEALTH = new Box(50, 92, 116, LINE);
  private static final Box HEALTH_BAR = new Box(50, 100, 116, 6);
  private static final Box ROBOT_LINE = new Box(50, 109, 116, DETAIL_LINE);
  private static final Box NO_ROBOT = new Box(50, 78, 116, LINE);
  private static final Box NO_ROBOT_STATE = new Box(50, 89, 116, DETAIL_LINE);
  private static final Box NO_ROBOT_HINT = new Box(50, 97, 118, DETAIL_LINE);
  private static final int ROW_LEFT = 7;
  private static final int ROW_RIGHT = 169;
  private static final int QUEUE_TOP = 119;
  private static final int ROW_HEIGHT = 13;
  private static final Box QUEUE = new Box(11, 122, 154, LINE);
  private static final Box OWNER = new Box(11, 135, 104, LINE);

  private final ScreenPainter painter;
  private final InductionDockMenu menu;

  DockMainTab(ScreenPainter painter, InductionDockMenu menu) {
    this.painter = painter;
    this.menu = menu;
  }

  void renderFrames(GuiGraphics graphics, int left, int top) {
    ScreenPainter.sunken(
        graphics,
        left + PICTURE_X,
        top + TOWER_Y,
        RobotPortrait.SIZE,
        RobotPortrait.SIZE,
        ScreenPainter.PORTRAIT);
    ScreenSprites.tower(graphics, left + PICTURE_X + 1, top + TOWER_Y + 1, menu.status());
    ScreenSprites.bolt(graphics, left + BOLT_X, top + BOLT_Y);
    ScreenPainter.slot(graphics, left + SLOT_FRAME_X, top + SLOT_FRAME_Y);
    graphics.fill(
        left + MARGIN, top + RULE_Y, left + ROW_RIGHT - 1, top + RULE_Y + 1, ScreenPainter.SHADE);
    graphics.fill(
        left + ROW_LEFT,
        top + QUEUE_TOP,
        left + ROW_RIGHT,
        top + QUEUE_TOP + ROW_HEIGHT,
        ScreenPainter.ROW);
    renderPicture(graphics, left, top);
  }

  private void renderPicture(GuiGraphics graphics, int left, int top) {
    if (menu.hasRobot()) {
      Player player = Minecraft.getInstance().player;
      long tick = player == null ? 0 : player.tickCount;
      RobotPortrait.draw(
          graphics,
          left + PICTURE_X,
          top + ROBOT_Y,
          menu.robotExpression(),
          menu.robotTier(),
          tick);
      return;
    }
    ScreenPainter.sunken(
        graphics,
        left + PICTURE_X,
        top + ROBOT_Y,
        RobotPortrait.SIZE,
        RobotPortrait.SIZE,
        ScreenPainter.WINDOW);
    ScreenSprites.emptyPlate(graphics, left + PICTURE_X + 1, top + ROBOT_Y + 1);
  }

  void renderLabels(GuiGraphics graphics, Component name) {
    Component bold =
        Component.translatable(ScreenKeys.DOCK_TITLE_TIER, name, TierLook.name(menu.dockTier()))
            .withStyle(ChatFormatting.BOLD);
    painter.text(graphics, bold, TITLE, ScreenPainter.TEXT, ScreenPainter.FULL_SCALE);
    DockStatus status = menu.status();
    painter.status(
        graphics, Component.translatable(ScreenKeys.dockStatus(status)), STATUS, colorOf(status));
    renderStock(graphics);
    renderRedstoneLabel(graphics);
    painter.text(
        graphics,
        Component.translatable(ScreenKeys.DOCK_DOCKED).withStyle(ChatFormatting.BOLD),
        DOCKED,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    if (menu.hasRobot()) {
      renderRobot(graphics);
    } else {
      renderNoRobot(graphics, status);
    }
    renderRows(graphics);
  }

  private void renderStock(GuiGraphics graphics) {
    painter.pair(
        graphics,
        STORED,
        Component.translatable(ScreenKeys.DOCK_STORED_ENERGY),
        DockTexts.amount(menu.stock(), menu.capacity()),
        Colors.TITLE_FIRST);
    ScreenPainter.bar(
        graphics, STOCK_BAR, menu.stock().amount(), menu.capacity().amount(), ScreenPainter.ORANGE);
    painter.text(
        graphics, DockTexts.input(menu), INPUT, ScreenPainter.DETAIL, ScreenPainter.DETAIL_SCALE);
    Component limit =
        Component.translatable(ScreenKeys.DOCK_LIMIT, EnergyFormat.rate(menu.inputLimit()));
    painter.text(graphics, limit, LIMIT, ScreenPainter.DETAIL, ScreenPainter.DETAIL_SCALE);
    menu.network().ifPresent(given -> renderNetwork(graphics, given));
  }

  /** In green with what entered, or in gray with nothing (answer 2593). */
  private void renderNetwork(GuiGraphics graphics, Energy given) {
    Component line =
        given.isZero()
            ? Component.translatable(ScreenKeys.DOCK_NETWORK_IDLE)
            : Component.translatable(ScreenKeys.DOCK_NETWORK, EnergyFormat.rate(given));
    int color = given.isZero() ? ScreenPainter.DETAIL : ScreenPainter.STATUS_GREEN;
    painter.text(graphics, line, NETWORK, color, ScreenPainter.DETAIL_SCALE);
  }

  private void renderRedstoneLabel(GuiGraphics graphics) {
    Component label = Component.translatable(ScreenKeys.REDSTONE);
    int width = painter.width(label, ScreenPainter.DETAIL_SCALE);
    painter.text(
        graphics,
        label,
        REDSTONE_CENTER - width / 2,
        REDSTONE_Y,
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  private void renderRobot(GuiGraphics graphics) {
    painter.pair(
        graphics,
        SECTION,
        Component.translatable(ScreenKeys.DOCK_ROBOT_BATTERY),
        DockTexts.amount(menu.robotCharge(), menu.robotCapacity()),
        Colors.TITLE_FIRST);
    ScreenPainter.bar(
        graphics,
        BATTERY_BAR,
        menu.robotCharge().amount(),
        menu.robotCapacity().amount(),
        ScreenPainter.GREEN);
    Component health =
        Component.translatable(ScreenKeys.HEALTH_AMOUNT, menu.robotHealth(), menu.robotMaxHealth());
    painter.pair(
        graphics, HEALTH, Component.translatable(ScreenKeys.HEALTH), health, Colors.TITLE_FIRST);
    ScreenPainter.bar(
        graphics, HEALTH_BAR, menu.robotHealth(), menu.robotMaxHealth(), ScreenPainter.RED);
    painter.text(
        graphics,
        DockTexts.robotLine(menu),
        ROBOT_LINE,
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  private void renderNoRobot(GuiGraphics graphics, DockStatus status) {
    painter.text(
        graphics,
        Component.translatable(ScreenKeys.DOCK_NO_ROBOT).withStyle(ChatFormatting.BOLD),
        NO_ROBOT,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
    String state = noPower(status) ? ScreenKeys.DOCK_NEEDS_ENERGY : ScreenKeys.DOCK_READY;
    painter.text(
        graphics,
        Component.translatable(state),
        NO_ROBOT_STATE,
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
    painter.text(
        graphics,
        Component.translatable(ScreenKeys.DOCK_PLACE_HINT),
        NO_ROBOT_HINT,
        ScreenPainter.DETAIL,
        ScreenPainter.DETAIL_SCALE);
  }

  private void renderRows(GuiGraphics graphics) {
    painter.pair(
        graphics,
        QUEUE,
        Component.translatable(ScreenKeys.DOCK_QUEUE),
        DockTexts.queue(menu.waiting()).withStyle(ChatFormatting.BOLD),
        Colors.VALUE_FIRST);
    painter.pair(
        graphics,
        OWNER,
        Component.translatable(ScreenKeys.DOCK_OWNER),
        DockTexts.owner(menu).withStyle(ChatFormatting.BOLD),
        Colors.VALUE_FIRST);
  }

  static boolean noPower(DockStatus status) {
    return status == DockStatus.OFF || status == DockStatus.NO_ENERGY;
  }

  private static int colorOf(DockStatus status) {
    return switch (status) {
      case CHARGING, COMPLETE -> ScreenPainter.STATUS_GREEN;
      case IDLE -> ScreenPainter.STATUS_BLUE;
      case OFF, NO_ENERGY -> ScreenPainter.STATUS_ORANGE;
    };
  }
}
