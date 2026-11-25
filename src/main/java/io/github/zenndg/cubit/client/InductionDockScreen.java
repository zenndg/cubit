package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * The dock screen of the concept image, in its three states and the two without a picture, in the
 * Dock tab, the storage in the Storage tab, the panel in the Solar tab and the XP in the XP tab
 * (answers 956, 960, 1837 and 1865).
 */
final class InductionDockScreen extends AbstractContainerScreen<InductionDockMenu> {
  private static final int WIDTH = 176;
  private static final int HEIGHT = 262;
  private static final int MARGIN = 8;
  private static final int LINE = 9;
  private static final Box TITLE = new Box(8, 6, 92, LINE);
  private static final int STORAGE_FRAME_Y = InductionDockMenu.STORAGE_Y - 1;
  private static final Box CALL = new Box(7, 149, 162, 16);
  private static final int INVENTORY_LABEL_Y = 169;
  private static final int ROWS = 3;
  private static final int COLUMNS = 9;
  private static final int SLOTS_FRAME_X = 7;
  private static final int HOTBAR_GAP = 58;

  private final ScreenPainter painter = new ScreenPainter();
  private final DockScreenTabs tabs = new DockScreenTabs();
  private final DockMainTab mainTab;
  private final DockXpPanel xp;
  private final BlockRename rename;
  private FlatButton callButton;

  InductionDockScreen(InductionDockMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.mainTab = new DockMainTab(painter, menu);
    this.xp = new DockXpPanel(painter, menu.xp());
    this.rename =
        new BlockRename(
            new BlockRename.Named(
                BlockName.Kind.DOCK,
                menu.dockId(),
                menu.dockName(),
                ScreenKeys.DOCK_RENAME_TITLE,
                typed -> CubitNetwork.renameDock(menu.containerId, typed)));
    imageWidth = WIDTH;
    imageHeight = HEIGHT;
    inventoryLabelX = MARGIN;
    inventoryLabelY = INVENTORY_LABEL_Y;
  }

  @Override
  protected void init() {
    super.init();
    Component label =
        Component.translatable(ScreenKeys.DOCK_CALL, menu.callRadius())
            .withStyle(ChatFormatting.BOLD);
    Box shown = CALL.moved(leftPos, topPos);
    callButton =
        addRenderableWidget(
            new FlatButton(shown.x(), shown.y(), shown.width(), shown.height(), label, this::call));
    rename.init(font, width, height, leftPos, topPos, this::addRenderableWidget);
    showOpenTab();
  }

  private void showOpenTab() {
    menu.showStorage(tabs.open() == DockScreenTabs.Tab.STORAGE);
    callButton.visible = tabs.open() == DockScreenTabs.Tab.DOCK;
    rename.show(tabs.open() == DockScreenTabs.Tab.DOCK);
  }

  private void call() {
    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, InductionDockMenu.CALL_BUTTON);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    painter.begin(font);
    callButton.setMuted(DockMainTab.noPower(menu.status()));
    super.render(graphics, mouseX, mouseY, partialTick);
    renderTooltip(graphics, mouseX, mouseY);
    graphics.pose().pushPose();
    graphics.pose().translate(leftPos, topPos, 0);
    painter.cutTooltip(graphics, mouseX - leftPos, mouseY - topPos);
    graphics.pose().popPose();
    rename.render(graphics, width, height, mouseX, mouseY, partialTick);
    showOpenTab();
    if (tabs.open() == DockScreenTabs.Tab.XP) {
      xp.renderDetail(graphics, font, mouseX, mouseY, leftPos, topPos);
    }
    tabAt(mouseX, mouseY)
        .ifPresent(under ->
            graphics.renderTooltip(font, Component.translatable(under.title()), mouseX, mouseY));
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    tabs.renderBehind(graphics, leftPos + tabLeft(), topPos);
    ScreenPainter.window(graphics, leftPos, topPos, imageWidth, imageHeight);
    tabs.renderFront(graphics, leftPos + tabLeft(), topPos);
    renderSlots(graphics, leftPos, topPos);
    if (tabs.open() == DockScreenTabs.Tab.STORAGE) {
      renderStorageFrames(graphics, leftPos, topPos);
    } else if (tabs.open() == DockScreenTabs.Tab.DOCK) {
      mainTab.renderFrames(graphics, leftPos, topPos);
    }
  }

  /** The rows of the tier of the dock, from three to six (answer 1374). */
  private void renderStorageFrames(GuiGraphics graphics, int left, int top) {
    int rows = menu.storageSlots() / COLUMNS;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        ScreenPainter.slot(
            graphics,
            left + SLOTS_FRAME_X + column * ScreenPainter.SLOT_SIZE,
            top + STORAGE_FRAME_Y + row * ScreenPainter.SLOT_SIZE);
      }
    }
  }

  private void renderSlots(GuiGraphics graphics, int left, int top) {
    int firstRow = InductionDockMenu.PLAYER_INVENTORY_Y - 1;
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        ScreenPainter.slot(
            graphics,
            left + SLOTS_FRAME_X + column * ScreenPainter.SLOT_SIZE,
            top + firstRow + row * ScreenPainter.SLOT_SIZE);
      }
    }
    for (int column = 0; column < COLUMNS; column++) {
      ScreenPainter.slot(
          graphics,
          left + SLOTS_FRAME_X + column * ScreenPainter.SLOT_SIZE,
          top + firstRow + HOTBAR_GAP);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    if (tabs.open() == DockScreenTabs.Tab.STORAGE) {
      Component storage =
          Component.translatable(ScreenKeys.DOCK_TAB_STORAGE).withStyle(ChatFormatting.BOLD);
      painter.text(graphics, storage, TITLE, ScreenPainter.TEXT, ScreenPainter.FULL_SCALE);
    } else if (tabs.open() == DockScreenTabs.Tab.SOLAR) {
      DockSolarTab.render(painter, graphics, menu);
    } else if (tabs.open() == DockScreenTabs.Tab.XP) {
      xp.render(graphics, mouseX - leftPos, mouseY - topPos);
    } else {
      mainTab.renderLabels(graphics, rename.name());
    }
    painter.text(
        graphics,
        playerInventoryTitle,
        inventoryLabelX,
        inventoryLabelY,
        ScreenPainter.TEXT,
        ScreenPainter.FULL_SCALE);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (rename.isOpen()) {
      return rename.mouseClicked(mouseX, mouseY, button);
    }
    Optional<DockScreenTabs.Tab> clicked = tabAt(mouseX, mouseY);
    if (clicked.isPresent()) {
      tabs.open(clicked.get());
      showOpenTab();
      return true;
    }
    OptionalInt pressed =
        tabs.open() == DockScreenTabs.Tab.XP
            ? xp.buttonAt(mouseX - leftPos, mouseY - topPos)
            : OptionalInt.empty();
    if (pressed.isPresent()) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, pressed.getAsInt());
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
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

  /** The tabs stand outside the window, and a click there must not throw the item. */
  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
    return tabAt(mouseX, mouseY).isEmpty()
        && super.hasClickedOutside(mouseX, mouseY, left, top, button);
  }

  private Optional<DockScreenTabs.Tab> tabAt(double mouseX, double mouseY) {
    return tabs.at(mouseX - leftPos - tabLeft(), mouseY - topPos);
  }

  private int tabLeft() {
    return imageWidth - ScreenTabStrip.INSET;
  }
}
