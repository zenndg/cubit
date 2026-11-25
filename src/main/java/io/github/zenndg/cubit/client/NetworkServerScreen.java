package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.network.ServerStatus;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.NetworkServerMenu;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The screen of the Network Server, in its Energy and Storage tabs (answers 2277 and 2397). */
final class NetworkServerScreen extends NetworkScreen<NetworkServerMenu> {
  private static final int HEIGHT = 262;
  private static final int OWNER_Y = 156;
  private static final int TITLE_Y = 6;
  private static final int NAME_WIDTH = 72;
  private static final int BADGE_GAP = 4;
  private static final int BADGE_TOP = 5;
  private static final int BADGE_HEIGHT = 9;
  private static final int BADGE_TEXT = 7;
  private static final int BADGE_PADDING = 3;
  private static final int TWO_DIGITS = 99;
  private static final int SLOT_END = 17;
  private static final int GLYPH_HEIGHT = 8;
  private static final float SMALL_COUNT = 0.75f;
  private static final float ABOVE_ITEM = 200f;
  private static final String NO_COUNT = "";

  private final ServerScreenTabs tabs = new ServerScreenTabs();
  private final ServerEnergyTab energy;
  private final ServerStorageTab storage;

  NetworkServerScreen(NetworkServerMenu menu, Inventory inventory, Component title) {
    super(
        menu,
        inventory,
        title,
        new Layout(HEIGHT, NetworkServerMenu.PLAYER_Y, OWNER_Y, NetworkKeys.RENAME_SERVER));
    energy = new ServerEnergyTab(this, menu);
    storage = new ServerStorageTab(this, menu);
  }

  private boolean showsStorage() {
    return tabs.open() == ServerScreenTabs.Tab.STORAGE;
  }

  @Override
  boolean showsOwner() {
    return !showsStorage();
  }

  @Override
  protected void init() {
    super.init();
    menu.showStorage(showsStorage());
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    tabAt(mouseX, mouseY)
        .ifPresent(under ->
            graphics.renderTooltip(font, Component.translatable(under.title()), mouseX, mouseY));
    if (!showsStorage() && !rename.isOpen()) {
      List<Component> lines = energy.costLines(mouseX - leftPos, mouseY - topPos);
      graphics.renderComponentTooltip(font, lines, mouseX, mouseY);
    }
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    tabs.renderBehind(graphics, leftPos + tabLeft(), topPos);
    super.renderBg(graphics, partialTick, mouseX, mouseY);
    tabs.renderFront(graphics, leftPos + tabLeft(), topPos);
  }

  @Override
  void renderFrames(GuiGraphics graphics, int left, int top) {
    if (showsStorage()) {
      storage.renderFrames(graphics, left, top);
    } else {
      energy.renderFrames(graphics, left, top);
    }
  }

  @Override
  void renderTab(GuiGraphics graphics, int mouseX, int mouseY) {
    if (showsStorage()) {
      storage.render(graphics);
      return;
    }
    renderTitle(graphics);
    energy.render(graphics, mouseX, mouseY);
  }

  /** The name, the tier in its badge and what the energy does. */
  private void renderTitle(GuiGraphics graphics) {
    Component name = rename.name().copy().withStyle(ChatFormatting.BOLD);
    bold(graphics, rename.name(), new Box(MARGIN, TITLE_Y, NAME_WIDTH, LINE));
    int end = MARGIN + Math.min(painter.width(name, ScreenPainter.FULL_SCALE), NAME_WIDTH);
    Component tier =
        Component.literal(TierLook.name(menu.tier()).getString().toUpperCase(Locale.ROOT));
    int width = painter.width(tier, ScreenPainter.DETAIL_SCALE) + BADGE_PADDING * 2;
    int left = end + BADGE_GAP;
    graphics.fill(left, BADGE_TOP, left + width, BADGE_TOP + BADGE_HEIGHT, ScreenPainter.SLOT);
    painter.text(
        graphics,
        tier,
        left + BADGE_PADDING,
        BADGE_TEXT,
        ScreenPainter.WHITE,
        ScreenPainter.DETAIL_SCALE);
    ServerStatus status = menu.status();
    painter.status(
        graphics, Component.translatable(NetworkKeys.status(status)), STATUS, colorOf(status));
  }

  private static int colorOf(ServerStatus status) {
    return switch (status) {
      case ON -> ScreenPainter.STATUS_GREEN;
      case ON_UPS -> ScreenPainter.STATUS_ORANGE;
      case NO_ENERGY -> ScreenPainter.STATUS_RED;
    };
  }

  /** Three digits do not fit in a slot at the size of the game, so they are smaller (2397). */
  @Override
  protected void renderSlotContents(
      GuiGraphics graphics, ItemStack stack, Slot slot, String countText) {
    if (countText != null || stack.getCount() <= TWO_DIGITS) {
      super.renderSlotContents(graphics, stack, slot, countText);
      return;
    }
    super.renderSlotContents(graphics, stack, slot, NO_COUNT);
    String count = Integer.toString(stack.getCount());
    graphics.pose().pushPose();
    graphics
        .pose()
        .translate(
            slot.x + SLOT_END - font.width(count) * SMALL_COUNT,
            slot.y + SLOT_END - GLYPH_HEIGHT * SMALL_COUNT,
            ABOVE_ITEM);
    graphics.pose().scale(SMALL_COUNT, SMALL_COUNT, 1f);
    graphics.drawString(font, count, 0, 0, ScreenPainter.WHITE, true);
    graphics.pose().popPose();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (rename.isOpen()) {
      return super.mouseClicked(mouseX, mouseY, button);
    }
    Optional<ServerScreenTabs.Tab> clicked = tabAt(mouseX, mouseY);
    if (clicked.isPresent()) {
      tabs.open(clicked.get());
      menu.showStorage(showsStorage());
      return true;
    }
    OptionalInt pressed =
        showsStorage() ? OptionalInt.empty() : energy.buttonAt(mouseX - leftPos, mouseY - topPos);
    if (pressed.isPresent()) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, pressed.getAsInt());
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  /** The tabs stand outside the window, and a click there must not throw the item. */
  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
    return tabAt(mouseX, mouseY).isEmpty()
        && super.hasClickedOutside(mouseX, mouseY, left, top, button);
  }

  private Optional<ServerScreenTabs.Tab> tabAt(double mouseX, double mouseY) {
    return tabs.at(mouseX - leftPos - tabLeft(), mouseY - topPos);
  }

  private int tabLeft() {
    return imageWidth - ScreenTabStrip.INSET;
  }
}
