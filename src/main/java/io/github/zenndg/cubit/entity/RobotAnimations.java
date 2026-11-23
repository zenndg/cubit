package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.RobotPose;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/** Plays the animation of the current pose, named after it in the animation file of the robot. */
final class RobotAnimations {
  private static final String CONTROLLER = "pose";
  private static final int TRANSITION_TICKS = 5;
  private static final String ARMS_CONTROLLER = "arms";
  private static final String ARMS_RAISED = "arms_raised";
  private static final RawAnimation RAISED_ARMS = RawAnimation.begin().thenPlayAndHold(ARMS_RAISED);
  private static final String FISHING = "fishing";
  private static final RawAnimation FISHING_ARM = RawAnimation.begin().thenPlayAndHold(FISHING);
  private static final Map<RobotPose, RawAnimation> ANIMATIONS = new EnumMap<>(RobotPose.class);
  private static final String VISOR_CONTROLLER = "patroller_visor";
  private static final String WINGS_CONTROLLER = "patroller_wings";
  private static final String VISOR_DOWN_NAME = "patroller_visor_down";
  private static final RawAnimation VISOR_DOWN =
      RawAnimation.begin().thenPlayAndHold(VISOR_DOWN_NAME);
  private static final String VISOR_UP_NAME = "patroller_visor_up";
  private static final RawAnimation VISOR_UP = RawAnimation.begin().thenPlayAndHold(VISOR_UP_NAME);
  private static final String WINGS_OPEN_NAME = "patroller_wings_open";
  private static final RawAnimation WINGS_OPEN =
      RawAnimation.begin().thenPlayAndHold(WINGS_OPEN_NAME);
  private static final String WINGS_CLOSED_NAME = "patroller_wings_closed";
  private static final RawAnimation WINGS_CLOSED =
      RawAnimation.begin().thenPlayAndHold(WINGS_CLOSED_NAME);
  private static final String GUARD_NAME = "patroller_guard";
  private static final RawAnimation GUARD = RawAnimation.begin().thenPlayAndHold(GUARD_NAME);

  static {
    for (RobotPose pose : RobotPose.values()) {
      String name = pose.name().toLowerCase(Locale.ROOT);
      ANIMATIONS.put(
          pose,
          pose == RobotPose.POWERED_DOWN
              ? RawAnimation.begin().thenPlayAndHold(name)
              : RawAnimation.begin().thenLoop(name));
    }
  }

  private RobotAnimations() {}

  /** The arms come last, because the last controller decides a bone that two of them move. */
  static void register(RobotEntity robot, AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(
        new AnimationController<>(
            robot,
            CONTROLLER,
            TRANSITION_TICKS,
            state ->
                state.setAndContinue(
                    ANIMATIONS.get(RobotPose.of(RobotFace.expression(robot), state.isMoving())))));
    controllers.add(
        new AnimationController<>(
            robot,
            VISOR_CONTROLLER,
            TRANSITION_TICKS,
            state -> patroller(robot, state, RobotJobLook.visorDown(robot), VISOR_DOWN, VISOR_UP)));
    controllers.add(
        new AnimationController<>(
            robot,
            WINGS_CONTROLLER,
            TRANSITION_TICKS,
            state ->
                patroller(robot, state, RobotJobLook.wingsOpen(robot), WINGS_OPEN, WINGS_CLOSED)));
    controllers.add(
        new AnimationController<>(
            robot, ARMS_CONTROLLER, TRANSITION_TICKS, state -> arms(robot, state)));
  }

  /** A piece of the Galactic Patroller, only with its card: down or open, or back (1623, 1553). */
  private static PlayState patroller(
      RobotEntity robot,
      AnimationState<RobotEntity> state,
      boolean on,
      RawAnimation whenOn,
      RawAnimation whenOff) {
    if (RobotJobLook.job(robot).filter(RobotJob.PATROLLER::equals).isEmpty()) {
      return PlayState.STOP;
    }
    return state.setAndContinue(on ? whenOn : whenOff);
  }

  /**
   * The raised arms of a feeling come first, then the pistol up on guard at a stop of the round of
   * the Patroller, and then the arm with the tool of the job (answers 1199, 1229 and 1627).
   */
  private static PlayState arms(RobotEntity robot, AnimationState<RobotEntity> state) {
    if (RobotFace.armsRaised(robot)) {
      return state.setAndContinue(RAISED_ARMS);
    }
    if (RobotJobLook.onGuard(robot)) {
      return state.setAndContinue(GUARD);
    }
    return RobotJobLook.toolInClaw(robot) ? state.setAndContinue(FISHING_ARM) : PlayState.STOP;
  }
}
