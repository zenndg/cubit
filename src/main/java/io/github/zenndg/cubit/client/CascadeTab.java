package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.CascadeMenuPart;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import io.github.zenndg.cubit.menu.EngineeringDeskMenu;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The Cascade tab: choosing what to make, seeing its plan, the question of the start and the plan
 * on its way, with each button only in its moment (answers 2017, 2019 and 2044).
 */
final class CascadeTab {
  private static final int BUTTON_Y = 108;
  private static final int BUTTON_HEIGHT = 12;
  private static final int SMALL = 14;
  private static final int WIDE = 53;
  private static final int VIEW_WIDTH = 72;
  private static final int NEW_WIDTH = 60;
  private static final int RIGHT = 248;
  private static final int MINUS_X = 96;
  private static final int PLUS_X = 136;
  private static final int QUANTITY_X = 123;
  private static final int QUANTITY_ROOM = 22;
  private static final int START_X = 138;
  private static final int TEXT_Y = BUTTON_Y + 2;
  private static final String MINUS_LABEL = "-";
  private static final String PLUS_LABEL = "+";

  private final EngineeringDeskMenu menu;
  private final CascadeReading reading;
  private final CascadeMaterials materials;
  private final CascadeTableView table;
  private final CascadeQuestion question;
  private final DeskCatalog catalog;
  private final CascadeCatalogRules rules;
  private final IntConsumer send;
  private boolean open;
  private boolean planOpen;
  private BlueprintButton minus;
  private BlueprintButton plus;
  private BlueprintButton view;
  private BlueprintButton start;
  private BlueprintButton cancel;
  private BlueprintButton again;

  CascadeTab(
      EngineeringDeskMenu menu, DeskCatalog catalog, CascadeCatalogRules rules, IntConsumer send) {
    this.menu = menu;
    this.catalog = catalog;
    this.rules = rules;
    this.send = send;
    this.reading = new CascadeReading(menu, catalog);
    this.materials = new CascadeMaterials(reading);
    this.table = new CascadeTableView(reading);
    this.question = new CascadeQuestion(reading, () -> send.accept(CascadeMenuPart.START));
  }

  void init(int left, int top, Consumer<BlueprintButton> add) {
    minus = button(left + MINUS_X, top, SMALL, Component.literal(MINUS_LABEL), () -> order(false));
    plus = button(left + PLUS_X, top, SMALL, Component.literal(PLUS_LABEL), () -> order(true));
    Component viewLabel = Component.translatable(DeskScreenKeys.CASCADE_VIEW);
    view = button(left + RIGHT - VIEW_WIDTH, top, VIEW_WIDTH, viewLabel, () -> planOpen = true);
    start =
        new BlueprintButton(
            left + START_X,
            top + BUTTON_Y,
            WIDE,
            BUTTON_HEIGHT,
            Component.translatable(DeskScreenKeys.CASCADE_START),
            BlueprintPainter.PRIMARY,
            () -> question.ask(true));
    Component cancelLabel = Component.translatable(DeskScreenKeys.CANCEL);
    cancel = button(left + RIGHT - WIDE, top, WIDE, cancelLabel, () -> planOpen = false);
    Component newLabel = Component.translatable(DeskScreenKeys.CASCADE_NEW);
    again = button(left + RIGHT - NEW_WIDTH, top, NEW_WIDTH, newLabel, this::forget);
    List.of(minus, plus, view, start, cancel, again).forEach(add);
    question.init(left, top);
    update();
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

  private void forget() {
    send.accept(CascadeMenuPart.NEW_PLAN);
  }

  void show(boolean shown) {
    open = shown;
    update();
  }

  void tick() {
    if (open) {
      rules.tick(catalog.recipes());
      reading.tick();
    }
    if (!reading.choosing() || reading.plan().isEmpty()) {
      planOpen = false;
    }
    question.ask(question.isOpen() && open && planOpen && reading.mayStart());
    update();
  }

  /** The first step, with the catalog and the materials. */
  boolean choosing() {
    return reading.choosing() && !planOpen;
  }

  private void update() {
    boolean running = menu.cascade().locks();
    boolean choosing = open && choosing();
    minus.visible = choosing;
    plus.visible = choosing;
    view.visible = choosing;
    view.active = reading.showsPlan();
    start.visible = open && (planOpen || running);
    start.active = !running && reading.mayStart();
    start.lock(running);
    cancel.visible = start.visible;
    cancel.active = !running;
    cancel.lock(running);
    again.visible = open && menu.cascade().state() == CascadeMenuPart.State.DONE;
  }

  Component name() {
    return Component.translatable(
        menu.cascade().allowed() ? DeskScreenKeys.CASCADE : DeskScreenKeys.CASCADE_LOCKED);
  }

  void renderLabels(GuiGraphics graphics, Font font) {
    if (!choosing()) {
      table.render(graphics, font);
      return;
    }
    materials.render(graphics, font);
    Component quantity = Component.literal(String.valueOf(menu.cascade().quantity()));
    BlueprintPainter.fitted(
        graphics, font, quantity, QUANTITY_X, TEXT_Y, QUANTITY_ROOM, BlueprintPainter.TEXT);
  }

  /** The tooltip under the mouse, from the left and the top of the window. */
  List<Component> hint(double x, double y) {
    if (over(start) || over(cancel) || over(view)) {
      return buttonHint();
    }
    return choosing() ? materials.hint(x, y) : table.hint(x, y);
  }

  private static boolean over(BlueprintButton button) {
    return button.visible && button.isHovered();
  }

  /** Why a button that is off does not work. */
  private List<Component> buttonHint() {
    if (menu.cascade().locks()) {
      return List.of(Component.translatable(DeskScreenKeys.CASCADE_LOCKS));
    }
    boolean off = over(view) && !view.active || over(start) && !start.active;
    return off ? List.of(reading.reason()) : List.of();
  }

  boolean scroll(double x, double y, double delta) {
    return choosing() ? materials.scroll(x, y, delta) : table.scroll(x, y, delta);
  }

  CascadeQuestion question() {
    return question;
  }
}
