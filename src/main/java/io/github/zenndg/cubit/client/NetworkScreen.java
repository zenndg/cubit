package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.client.ScreenPainter.Colors;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.menu.NetworkMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.network.CubitNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * What the screens of the blocks of the Network share: the battery with its input slot, the owner
 * with the Rename and the inventory of the player, as in the approved previews (answer 2397).
 */
abstract class NetworkScreen<M extends NetworkMenu> extends AbstractContainerScreen<M> {
  static final int LINE = 9;
  static final int DETAIL_LINE = 7;
  static final int MARGIN = 8;
  static final int ROW_LEFT = 7;
  static final int ROW_RIGHT = 169;
  static final int ROW_WIDTH = ROW_RIGHT - ROW_LEFT;
  static final int ROW_HEIGHT = 11;
  static final int ROW_TEXT = 3;
  static final int ROW_INSET = 4;
  static final Box STATUS = new Box(100, 6, 68, LINE);
  private static final int WIDTH = 176;
  private static final int LABEL_GAP = 10;
  private static final int ROWS = 3;
  private static final int COLUMNS = 9;
  private static final int HOTBAR_GAP = 58;
  private static final Box STORED = new Box(8, 18, 138, LINE);
  private static final Box STOCK_BAR = new Box(8, 26, 138, 7);
  private static final Box INPUT = new Box(8, 36, 138, DETAIL_LINE);
  private static final Box LIMIT = new Box(8, 43, 138, DETAIL_LINE);
  private static final int BOLT_X = 156;
  private static final int BOLT_Y = 18;
  private static final int REDSTONE_CENTER = 160;
  private static final int REDSTONE_Y = 53;
  private static final int OWNER_RIGHT = 112;
  private static final int PERCENT = 100;

  final ScreenPainter painter = new ScreenPainter();
  final BlockRename rename;
  private final int playerY;
  private final int ownerY;

  NetworkScreen(M menu, Inventory inventory, Component title, Layout layout) {
    super(menu, inventory, title);
    playerY = layout.playerY();
    ownerY = layout.ownerY();
    imageWidth = WIDTH;
    imageHeight = layout.height();
    inventoryLabelX = MARGIN;
    inventoryLabelY = playerY - LABEL_GAP;
    rename =
        new BlockRename(
            new BlockRename.Named(
                menu.kind(),
                menu.networkId(),
                menu.networkName(),
                layout.renameKey(),
                typed -> CubitNetwork.renameBlock(menu.containerId, typed)),
            ownerY);
  }

  /** Whether the open tab has the owner with the Rename. */
  abstract boolean showsOwner();

  abstract void renderTab(GuiGraphics graphics, int mouseX, int mouseY);

  /** The parts of the open tab under the items. */
  abstract void renderFrames(GuiGraphics graphics, int left, int top);

  @Override
  protected void init() {
    super.init();
    rename.init(font, width, height, leftPos, topPos, this::addRenderableWidget);
    rename.show(showsOwner());
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    painter.begin(font);
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
    graphics.pose().pushPose();
    graphics.pose().translate(leftPos, topPos, 0);
    painter.cutTooltip(graphics, mouseX - leftPos, mouseY - topPos);
    graphics.pose().popPose();
    rename.render(graphics, width, height, mouseX, mouseY, partialTick);
    rename.show(showsOwner());
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    ScreenPainter.window(graphics, leftPos, topPos, imageWidth, imageHeight);
    for (int row = 0; row < ROWS; row++) {
      slotRow(graphics, leftPos, topPos + playerY - 1 + row * ScreenPainter.SLOT_SIZE);
    }
    slotRow(graphics, leftPos, topPos + playerY - 1 + HOTBAR_GAP);
    renderFrames(graphics, leftPos, topPos);
  }

  static void slotRow(GuiGraphics graphics, int left, int y) {
    for (int column = 0; column < COLUMNS; column++) {
      ScreenPainter.slot(graphics, left + ROW_LEFT + column * ScreenPainter.SLOT_SIZE, y);
    }
  }

  static void row(GuiGraphics graphics, int left, int y, int height) {
    graphics.fill(left + ROW_LEFT, y, left + ROW_RIGHT, y + height, ScreenPainter.ROW);
  }

  static void inputFrames(GuiGraphics graphics, int left, int top) {
    ScreenSprites.bolt(graphics, left + BOLT_X, top + BOLT_Y);
    ScreenPainter.slot(graphics, left + NetworkMenu.SLOT_X - 1, top + NetworkMenu.SLOT_Y - 1);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    renderTab(graphics, mouseX - leftPos, mouseY - topPos);
    painter.text(
        graphics,
        playerInventoryTitle,
        inventoryLabelX,
        inventoryLabelY,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
  }

  /** The battery, with the mark of the reserve, what enters it and the label of the slot. */
  final void renderBattery(GuiGraphics graphics, UpsReserve mark) {
    painter.pair(
        graphics,
        STORED,
        Component.translatable(ScreenKeys.DOCK_STORED_ENERGY),
        DockTexts.amount(menu.energy(), menu.capacity()),
        Colors.TITLE_FIRST);
    ScreenPainter.bar(
        graphics,
        STOCK_BAR,
        menu.energy().amount(),
        menu.capacity().amount(),
        ScreenPainter.ORANGE);
    if (!mark.isNone()) {
      int x = STOCK_BAR.x() + 1 + (STOCK_BAR.width() - 2) * mark.percent() / PERCENT;
      graphics.fill(x, STOCK_BAR.y(), x + 1, STOCK_BAR.bottom(), ScreenPainter.WHITE);
    }
    detail(graphics, input(), INPUT, ScreenPainter.DETAIL);
    Component limit =
        Component.translatable(ScreenKeys.DOCK_LIMIT, EnergyFormat.rate(menu.inputLimit()));
    detail(graphics, limit, LIMIT, ScreenPainter.DETAIL);
    Component redstone = Component.translatable(ScreenKeys.REDSTONE);
    int x = REDSTONE_CENTER - painter.width(redstone, ScreenPainter.DETAIL_SCALE) / 2;
    painter.text(
        graphics, redstone, x, REDSTONE_Y, ScreenPainter.DETAIL, ScreenPainter.DETAIL_SCALE);
  }

  /** A full battery takes nothing in, so it says so before the input. */
  private Component input() {
    if (menu.energy().amount() >= menu.capacity().amount()) {
      return Component.translatable(ScreenKeys.DOCK_FULL);
    }
    return menu.input().isZero()
        ? Component.translatable(ScreenKeys.DOCK_NO_INPUT)
        : Component.translatable(ScreenKeys.DOCK_INPUT, EnergyFormat.rate(menu.input()));
  }

  final void renderOwner(GuiGraphics graphics) {
    Component owner =
        menu.ownerName()
            .map(Component::literal)
            .orElseGet(() -> Component.translatable(ScreenKeys.DOCK_OWNER_UNKNOWN))
            .withStyle(ChatFormatting.BOLD);
    int left = ROW_LEFT + ROW_INSET;
    painter.pair(
        graphics,
        new Box(left, ownerY + ROW_TEXT, OWNER_RIGHT - left, LINE),
        Component.translatable(ScreenKeys.DOCK_OWNER),
        owner,
        new Colors(ScreenPainter.TEXT, ScreenPainter.TEXT));
  }

  final void bold(GuiGraphics graphics, Component text, Box box) {
    painter.text(
        graphics,
        text.copy().withStyle(ChatFormatting.BOLD),
        box,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
  }

  final void detail(GuiGraphics graphics, Component text, Box box, int color) {
    painter.text(graphics, text, box, color, ScreenPainter.DETAIL_SCALE);
  }

  /** A label and its value in a row of the list. */
  final void rowPair(GuiGraphics graphics, int y, String key, Component value, int color) {
    painter.pair(
        graphics,
        new Box(ROW_LEFT + ROW_INSET, y + ROW_TEXT, ROW_WIDTH - ROW_INSET * 2, LINE),
        Component.translatable(key),
        value,
        new Colors(ScreenPainter.TEXT, color));
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return rename.isOpen()
        ? rename.mouseClicked(mouseX, mouseY, button)
        : super.mouseClicked(mouseX, mouseY, button);
  }

  /** The dialog takes the keys, so typing a name never moves the items. */
  @Override
  public boolean keyPressed(int key, int scanCode, int modifiers) {
    return rename.isOpen()
        ? rename.keyPressed(key, scanCode, modifiers)
        : super.keyPressed(key, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char typed, int modifiers) {
    return rename.isOpen() ? rename.charTyped(typed, modifiers) : super.charTyped(typed, modifiers);
  }

  /** The height of the window, where the player and the owner go, and the title of the Rename. */
  record Layout(int height, int playerY, int ownerY, String renameKey) {}
}
