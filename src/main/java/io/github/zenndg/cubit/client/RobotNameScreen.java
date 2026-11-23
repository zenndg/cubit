package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.menu.ScreenKeys;
import io.github.zenndg.cubit.network.AskRobotNamePayload;
import io.github.zenndg.cubit.network.CubitNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The window of the name of a Cubit placed for the first time, with its standard name in the field
 * (answers 1875 and 1879). Confirm sends the name; cancel, Esc or closing keep the standard one,
 * and either way the server answers once, so the greeting follows (answer 1880).
 */
final class RobotNameScreen extends Screen {
  private static final String NO_NAME = "";

  private final AskRobotNamePayload ask;
  private TabletRenameDialog dialog;
  private boolean answered;

  private RobotNameScreen(AskRobotNamePayload ask) {
    super(Component.translatable(ScreenKeys.ROBOT_NAME_TITLE));
    this.ask = ask;
  }

  static void show(AskRobotNamePayload ask) {
    Minecraft.getInstance().setScreen(new RobotNameScreen(ask));
  }

  @Override
  protected void init() {
    dialog = new TabletRenameDialog(font, width, height, ScreenKeys.ROBOT_NAME_TITLE, this::answer);
    dialog.open(ask.name());
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    dialog.render(graphics, width, height, mouseX, mouseY, partialTick);
  }

  /** Cancel closes the dialog, and the window goes with it. */
  @Override
  public void tick() {
    if (!dialog.isOpen()) {
      onClose();
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return dialog.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean keyPressed(int key, int scanCode, int modifiers) {
    return dialog.keyPressed(key, scanCode, modifiers)
        || super.keyPressed(key, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char typed, int modifiers) {
    return dialog.charTyped(typed, modifiers);
  }

  @Override
  public void onClose() {
    answer(NO_NAME);
    super.onClose();
  }

  @Override
  public boolean isPauseScreen() {
    return false;
  }

  private void answer(String name) {
    if (!answered) {
      answered = true;
      CubitNetwork.answerName(ask.robot(), name);
    }
  }
}
