package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.client.ScreenPainter.Box;
import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.menu.TabletKeys;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * The Rename button of the screen of a block and its dialog, as the one of the Cubit on the Tablet.
 * The server names the block; the screen shows the name it will keep, by the same rule, so the
 * title follows at once (answers 1867 to 1870).
 */
final class BlockRename {
  private static final Box BUTTON = new Box(118, 133, 50, 12);

  private final Named named;
  private String shown;
  private TabletRenameDialog dialog;
  private FlatButton button;

  BlockRename(Named named) {
    this.named = named;
    this.shown = named.name();
  }

  void init(Font font, int width, int height, int left, int top, Function<FlatButton, ?> add) {
    dialog = new TabletRenameDialog(font, width, height, named.titleKey(), this::send);
    Box at = BUTTON.moved(left, top);
    Component label = Component.translatable(TabletKeys.RENAME).withStyle(ChatFormatting.BOLD);
    button =
        new FlatButton(at.x(), at.y(), at.width(), at.height(), label, () -> dialog.open(shown));
    add.apply(button);
  }

  /** The name in the title: the one the block had, or the one just given. */
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
    named.send().accept(typed);
    shown = BlockName.of(named.kind(), named.id(), name).value();
  }

  /**
   * The block of the screen: its kind, its id, its name and how the typed one reaches the server.
   */
  record Named(BlockName.Kind kind, UUID id, String name, String titleKey, Consumer<String> send) {}
}
