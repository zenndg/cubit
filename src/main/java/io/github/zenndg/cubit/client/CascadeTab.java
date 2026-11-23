package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** The buttons of the Cascade tab around its panel (answers 2011, 2019, 2023 and 2035). */
final class CascadeTab {
  private static final int BUTTON_Y = 108;
  private static final int BUTTON_HEIGHT = 12;
  private static final int SMALL = 14;
  private static final int WIDE = 44;
  private static final int MINUS_X = 96;
  private static final int PLUS_X = 136;
  private static final int QUANTITY_X = 123;
  private static final int FIRST_X = 156;
  private static final int SECOND_X = 204;
  private static final int TEXT_Y = BUTTON_Y + 2;
  private static final String MINUS_LABEL = "-";
  private static final String PLUS_LABEL = "+";

  private final EngineeringDeskMenu menu;
  private final CascadePanel panel;
  private final DeskCatalog catalog;
  private final CascadeCatalogRules rules;
  private final IntConsumer send;
  private boolean open;
  private BlueprintButton minus;
  private BlueprintButton plus;
  private BlueprintButton start;
  private BlueprintButton yes;
  private BlueprintButton no;
  private BlueprintButton again;

  CascadeTab(
      EngineeringDeskMenu menu, DeskCatalog catalog, CascadeCatalogRules rules, IntConsumer send) {
    this.menu = menu;
    this.catalog = catalog;
    this.rules = rules;
    this.send = send;
    this.panel = new CascadePanel(menu, catalog);
  }

  void init(int left, int top, Consumer<BlueprintButton> add) {
    minus = button(left + MINUS_X, top, SMALL, Component.literal(MINUS_LABEL), () -> order(false));
    plus = button(left + PLUS_X, top, SMALL, Component.literal(PLUS_LABEL), () -> order(true));
    start = wide(left + SECOND_X, top, DeskScreenKeys.CASCADE_START, () -> panel.ask(true));
    yes = wide(left + FIRST_X, top, DeskScreenKeys.CASCADE_YES, this::confirm);
    no = wide(left + SECOND_X, top, DeskScreenKeys.CASCADE_NO, () -> panel.ask(false));
    again =
        wide(
            left + SECOND_X,
            top,
            DeskScreenKeys.CASCADE_NEW,
            () -> send.accept(CascadeMenuPart.NEW_PLAN));
    for (BlueprintButton each : new BlueprintButton[] {minus, plus, start, yes, no, again}) {
      add.accept(each);
    }
    update();
  }

  private static BlueprintButton wide(int x, int top, String key, Runnable action) {
    return button(x, top, WIDE, Component.translatable(key), action);
  }

  private static BlueprintButton button(
      int x, int top, int width, Component label, Runnable action) {
    return new BlueprintButton(
        x, top + BUTTON_Y, width, BUTTON_HEIGHT, label, BlueprintPainter.CELL, action);
  }

  private void order(boolean up) {
    boolean shift = Screen.hasShiftDown();
    int plusId = shift ? CascadeMenuPart.PLUS_SHIFT : CascadeMenuPart.PLUS;
    int minusId = shift ? CascadeMenuPart.MINUS_SHIFT : CascadeMenuPart.MINUS;
    send.accept(up ? plusId : minusId);
  }

  private void confirm() {
    panel.ask(false);
    send.accept(CascadeMenuPart.START);
  }

  void show(boolean shown) {
    open = shown;
    update();
  }

  void tick() {
    if (open) {
      rules.tick(catalog.recipes());
    }
    panel.tick();
    update();
  }

  /** Each button shows only in its moment of the plan. */
  private void update() {
    boolean choosing = open && panel.choosing() && !panel.isAsking();
    minus.visible = choosing;
    plus.visible = choosing;
    start.visible = choosing;
    start.active = panel.mayStart();
    yes.visible = open && panel.isAsking();
    no.visible = open && panel.isAsking();
    again.visible = open && menu.cascade().state() == CascadeMenuPart.State.DONE;
  }

  Component name() {
    return Component.translatable(
        menu.cascade().allowed() ? DeskScreenKeys.CASCADE : DeskScreenKeys.CASCADE_LOCKED);
  }

  void renderLabels(GuiGraphics graphics, Font font) {
    panel.render(graphics, font);
    if (minus.visible) {
      String quantity = String.valueOf(menu.cascade().quantity());
      graphics.drawCenteredString(font, quantity, QUANTITY_X, TEXT_Y, BlueprintPainter.TEXT);
    }
  }

  void tooltips(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    if (start.visible && !start.active && start.isHovered()) {
      graphics.renderTooltip(font, panel.reason(), mouseX, mouseY);
    }
  }
}
