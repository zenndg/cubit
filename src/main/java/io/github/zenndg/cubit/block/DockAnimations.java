package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.PanelState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * The panel of the dock: still in its cabinet or held in the third block, and the transformation of
 * about 6 s between them, like a robot transforming, played when the server starts a motion
 * (answers 1894, 1895 and 1910). A dock loaded mid motion shows where the panel goes (1864).
 */
public final class DockAnimations {
  private static final String PANEL = "panel";
  private static final String PREFIX = "induction_dock.";

  /**
   * With no blend, GeckoLib starts running before it takes the new pose from its queue and keeps
   * the old one, so the pose blends over a few ticks, as the lid of the desk does.
   */
  private static final int POSE_BLEND_TICKS = 2;

  private static final RawAnimation STORED = hold("stored");
  private static final RawAnimation DEPLOYED = hold("deployed");
  private static final RawAnimation DEPLOY = hold("deploy");
  private static final RawAnimation RETRACT = hold("retract");

  private DockAnimations() {}

  private static RawAnimation hold(String name) {
    return RawAnimation.begin().thenPlayAndHold(PREFIX + name);
  }

  static void register(
      AnimatableManager.ControllerRegistrar controllers, InductionDockBlockEntity dock) {
    controllers.add(
        new AnimationController<>(
            dock,
            PANEL,
            POSE_BLEND_TICKS,
            state -> state.setAndContinue(pose(dock.solar().panel()))));
  }

  private static RawAnimation pose(PanelState panel) {
    return switch (panel) {
      case STORED -> STORED;
      case DEPLOYING -> DEPLOY;
      case DEPLOYED -> DEPLOYED;
      case RETRACTING -> RETRACT;
    };
  }
}
