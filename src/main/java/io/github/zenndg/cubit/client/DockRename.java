package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.dock.DockName;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.network.CubitNetwork;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The Rename button of the Dock screen and its dialog, as the one of the Cubit on the Tablet. The
 * server names the Dock; the screen shows the name it will keep, by the same rule, so the title
 * follows at once (answers 1867 to 1870).
 */
final class DockRename {
  private static final Box BUTTON = new Box(118, 133, 50, 12);

  private final InductionDockMenu menu;
  private String shown;
  private TabletRenameDialog dialog;
  private FlatButton button;

  DockRename(InductionDockMenu menu) {
    this.menu = menu;
    this.shown = menu.dockName();
  }

  void init(Font font, int width, int height, int left, int top, Function<FlatButton, ?> add) {
    dialog = new TabletRenameDialog(font, width, height, ScreenKeys.DOCK_RENAME_TITLE, this::send);
    Box at = BUTTON.moved(left, top);
    Component label = Component.translatable(TabletKeys.RENAME).withStyle(ChatFormatting.BOLD);
    button =
        new FlatButton(at.x(), at.y(), at.width(), at.height(), label, () -> dialog.open(shown));
    add.apply(button);
  }

  /** The name in the title: the one the Dock had, or the one just given. */
  Component name() {
    return Component.literal(shown);
  }

  void show(boolean visible) {
    button.visible = visible && !dialog.isOpen();
  }

  boolean isOpen() {
    return dialog.isOpen();
  }

  void render(GuiGraphics graphics, int width, int height, int mouseX, int mouseY, float tick) {
    if (dialog.isOpen()) {
      dialog.render(graphics, width, height, mouseX, mouseY, tick);
    }
  }

  boolean mouseClicked(double mouseX, double mouseY, int pressed) {
    return dialog.mouseClicked(mouseX, mouseY, pressed);
  }

  boolean keyPressed(int key, int scanCode, int modifiers) {
    return dialog.keyPressed(key, scanCode, modifiers);
  }

  boolean charTyped(char typed, int modifiers) {
    return dialog.charTyped(typed, modifiers);
  }

  /** A name that does not fit changes nothing; an empty one goes back to the standard (1868). */
  private void send(String typed) {
    Optional<RobotName> name = RobotName.typed(typed);
    if (name.isEmpty()) {
      return;
    }
    CubitNetwork.renameDock(menu.containerId, typed);
    shown = DockName.of(menu.dockId(), name).value();
  }
}
