package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.energy.EnergyFormat;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.item.TierLook;
import io.github.zenndg.cubit.menu.NetworkKeys;
import io.github.zenndg.cubit.menu.SolarRelayMenu;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** The screen of the Solar Relay: the battery, what it gives now and its pair (2280 and 2397). */
final class SolarRelayScreen extends NetworkScreen<SolarRelayMenu> {
  private static final int HEIGHT = 240;
  private static final int OWNER_Y = 133;
  private static final Box TITLE = new Box(8, 6, 90, LINE);
  private static final int HEADING_Y = 59;
  private static final int FIRST_ROW = 72;
  private static final int SKY_ROW = 0;
  private static final int NOW_ROW = 1;
  private static final int MOST_ROW = 2;
  private static final int PANEL_ROW = 3;
  private static final int PAIR_Y = 118;
  private static final int PAIR_HEIGHT = 13;
  private static final int PAIR_TEXT = 119;

  SolarRelayScreen(SolarRelayMenu menu, Inventory inventory, Component title) {
    super(
        menu,
        inventory,
        title,
        new Layout(HEIGHT, SolarRelayMenu.PLAYER_Y, OWNER_Y, NetworkKeys.RENAME_RELAY));
  }

  @Override
  boolean showsOwner() {
    return true;
  }

  @Override
  void renderFrames(GuiGraphics graphics, int left, int top) {
    inputFrames(graphics, left, top);
    row(graphics, left, top + rowTop(SKY_ROW), ROW_HEIGHT);
    row(graphics, left, top + rowTop(MOST_ROW), ROW_HEIGHT);
    row(graphics, left, top + PAIR_Y, PAIR_HEIGHT);
  }

  private static int rowTop(int row) {
    return FIRST_ROW + row * ROW_HEIGHT;
  }

  @Override
  void renderTab(GuiGraphics graphics, int mouseX, int mouseY) {
    bold(graphics, rename.name(), TITLE);
    painter.status(graphics, Component.translatable(statusKey()), STATUS, statusColor());
    renderBattery(graphics, UpsReserve.NONE);
    XpHeading.render(graphics, painter, NetworkKeys.GENERATION, HEADING_Y, ROW_RIGHT);
    renderRows(graphics);
    renderPair(graphics);
    renderOwner(graphics);
  }

  private String statusKey() {
    if (!menu.paired()) {
      return NetworkKeys.NO_PAIR;
    }
    return menu.now().isZero() ? NetworkKeys.STOPPED : NetworkKeys.GENERATING;
  }

  private int statusColor() {
    if (!menu.paired()) {
      return ScreenPainter.STATUS_ORANGE;
    }
    return menu.now().isZero() ? ScreenPainter.STATUS_GRAY : ScreenPainter.STATUS_GREEN;
  }

  private void renderRows(GuiGraphics graphics) {
    Component sky = Component.translatable(NetworkKeys.sky(menu.sky()));
    rowPair(graphics, rowTop(SKY_ROW), NetworkKeys.SKY, sky, ScreenPainter.TEXT);
    boolean gives = !menu.now().isZero();
    Component now =
        gives
            ? Component.translatable(NetworkKeys.GAIN, EnergyFormat.rate(menu.now()))
            : Component.translatable(NetworkKeys.NOTHING);
    rowPair(
        graphics,
        rowTop(NOW_ROW),
        NetworkKeys.NOW,
        now,
        gives ? ScreenPainter.STATUS_GREEN : ScreenPainter.DETAIL);
    Component most =
        menu.paired()
            ? Component.translatable(NetworkKeys.RATE, EnergyFormat.rate(menu.most()))
            : Component.translatable(NetworkKeys.NONE);
    rowPair(graphics, rowTop(MOST_ROW), NetworkKeys.TIER_MOST, most, ScreenPainter.TEXT);
    Component panel = Component.translatable(NetworkKeys.panel(menu.panel()));
    rowPair(graphics, rowTop(PANEL_ROW), NetworkKeys.PANEL, panel, ScreenPainter.TEXT);
  }

  /** The server with its tier, or why there is none (answers 2374 and 2397). */
  private void renderPair(GuiGraphics graphics) {
    Component server =
        menu.serverName().isEmpty()
            ? CubitBlocks.NETWORK_SERVER.get().getName()
            : Component.literal(menu.serverName());
    Component pair =
        menu.paired()
            ? Component.translatable(NetworkKeys.PAIRED, server, TierLook.name(menu.tier()))
            : Component.translatable(NetworkKeys.NO_SERVER);
    rowPair(
        graphics,
        PAIR_TEXT,
        NetworkKeys.PAIR,
        pair.copy().withStyle(ChatFormatting.BOLD),
        menu.paired() ? ScreenPainter.STATUS_GREEN : ScreenPainter.STATUS_ORANGE);
  }
}
