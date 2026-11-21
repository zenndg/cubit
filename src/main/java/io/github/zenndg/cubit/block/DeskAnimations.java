package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskLid;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * The animations of the desk, in three controllers: what it does, in a loop; the lid of the laptop;
 * and the moments the server triggers. The last one is added last, so the start up wins over the
 * lid while it plays (answers 1465, 1466, 1470, 1471 and 1511).
 */
public final class DeskAnimations {
  public static final String ACTIONS = "actions";
  public static final String STARTUP = "startup";
  public static final String DRAWER_IN = "drawer_in";
  public static final String DRAWER_OUT = "drawer_out";
  public static final String DONE = "done";

  private static final String STATE = "state";
  private static final String LID = "lid";
  private static final String PREFIX = "engineering_desk.";
  private static final int NO_BLEND = 0;
  private static final int LID_BLEND_TICKS = 2;
  private static final int STATE_BLEND_TICKS = 5;

  private static final RawAnimation IDLE = loop("idle");
  private static final RawAnimation CRAFTING = loop("crafting");
  private static final RawAnimation PAUSED = loop("paused");
  private static final RawAnimation LID_OPEN = hold("lid_open");
  private static final RawAnimation LID_CLOSED = hold("lid_closed");

  private DeskAnimations() {}

  private static RawAnimation loop(String name) {
    return RawAnimation.begin().thenLoop(PREFIX + name);
  }

  private static RawAnimation hold(String name) {
    return RawAnimation.begin().thenPlayAndHold(PREFIX + name);
  }

  private static RawAnimation once(String name) {
    return RawAnimation.begin().thenPlay(PREFIX + name);
  }

  static void register(
      AnimatableManager.ControllerRegistrar controllers, EngineeringDeskBlockEntity desk) {
    controllers.add(
        new AnimationController<>(
            desk, STATE, STATE_BLEND_TICKS, state -> state.setAndContinue(look(desk))));
    controllers.add(
        new AnimationController<>(
            desk,
            LID,
            LID_BLEND_TICKS,
            state -> state.setAndContinue(lidOpen(desk) ? LID_OPEN : LID_CLOSED)));
    controllers.add(
        new AnimationController<>(desk, ACTIONS, NO_BLEND, state -> PlayState.STOP)
            .triggerableAnim(STARTUP, once(STARTUP))
            .triggerableAnim(DRAWER_IN, once(DRAWER_IN))
            .triggerableAnim(DRAWER_OUT, once(DRAWER_OUT))
            .triggerableAnim(DONE, once(DONE)));
  }

  private static RawAnimation look(EngineeringDeskBlockEntity desk) {
    return switch (desk.look()) {
      case IDLE -> IDLE;
      case CRAFTING -> CRAFTING;
      case PAUSED -> PAUSED;
    };
  }

  private static boolean lidOpen(EngineeringDeskBlockEntity desk) {
    DeskLook look = desk.look();
    return DeskLid.isOpen(desk.clientView().viewers(), look);
  }
}
