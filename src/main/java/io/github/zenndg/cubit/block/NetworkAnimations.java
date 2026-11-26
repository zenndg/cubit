package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.PanelState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * The animations of the blocks of the Network: the startup of the server and the panel of the
 * relay, which unfolds leaf by leaf and leans only then, and the mast and the dish of the antenna
 * (answers 2338, 2384, 2391 and 2557). What follows something that moves, as the fans and the sun,
 * is set on the bones by the models.
 */
public final class NetworkAnimations {
  private static final String DOOR = "door";
  private static final String PANEL = "panel";
  private static final String SERVER = "network_server.";
  private static final String RELAY = "solar_relay.";
  private static final String ANTENNA = "signal_antenna.";
  private static final String MAST = "mast";
  private static final String DISH = "dish";

  /** The dish blends from where its sweep was to the front as it locks. */
  private static final int DISH_BLEND_TICKS = 6;

  /** As in the Dock, a pose needs a short blend to be taken from the queue of GeckoLib. */
  private static final int POSE_BLEND_TICKS = 2;

  private static final RawAnimation IDLE = hold(SERVER + "idle");
  private static final RawAnimation STARTUP = hold(SERVER + "startup");
  private static final RawAnimation STORED = hold(RELAY + "stored");
  private static final RawAnimation DEPLOYED = hold(RELAY + "deployed");
  private static final RawAnimation DEPLOY = hold(RELAY + "deploy");
  private static final RawAnimation RETRACT = hold(RELAY + "retract");
  private static final String RAISED_NAME = ANTENNA + "raised";
  private static final String RAISE_NAME = ANTENNA + "raise";
  private static final String LOCK_NAME = ANTENNA + "lock";
  private static final String LOCKED_NAME = ANTENNA + "locked";
  private static final RawAnimation RAISED = hold(RAISED_NAME);
  private static final RawAnimation RAISE =
      RawAnimation.begin().thenPlay(RAISE_NAME).thenPlayAndHold(RAISED_NAME);
  private static final RawAnimation SEEK = RawAnimation.begin().thenLoop(ANTENNA + "seek");
  private static final RawAnimation LOCK =
      RawAnimation.begin().thenPlay(LOCK_NAME).thenPlayAndHold(LOCKED_NAME);
  private static final RawAnimation STOPPED_DISH = hold(ANTENNA + "stopped");

  private NetworkAnimations() {}

  private static RawAnimation hold(String name) {
    return RawAnimation.begin().thenPlayAndHold(name);
  }

  static void register(
      AnimatableManager.ControllerRegistrar controllers, NetworkServerBlockEntity server) {
    controllers.add(
        new AnimationController<>(
            server,
            DOOR,
            POSE_BLEND_TICKS,
            state -> state.setAndContinue(server.view().isStarting() ? STARTUP : IDLE)));
  }

  static void register(
      AnimatableManager.ControllerRegistrar controllers, SolarRelayBlockEntity relay) {
    controllers.add(
        new AnimationController<>(
            relay,
            PANEL,
            POSE_BLEND_TICKS,
            state -> state.setAndContinue(pose(relay.solar().panel()))));
  }

  /**
   * The mast rises once, when the antenna is placed, and the dish seeks without signal, locks with
   * it and droops when the antenna stops or is switched off (answer 2557).
   */
  static void register(
      AnimatableManager.ControllerRegistrar controllers, SignalAntennaBlockEntity antenna) {
    AntennaClientView view = antenna.view();
    controllers.add(
        new AnimationController<>(
            antenna,
            MAST,
            POSE_BLEND_TICKS,
            state -> state.setAndContinue(view.raising(gameTime(antenna)) ? RAISE : RAISED)));
    controllers.add(
        new AnimationController<>(
            antenna,
            DISH,
            DISH_BLEND_TICKS,
            state -> state.setAndContinue(dish(view, gameTime(antenna)))));
  }

  private static RawAnimation dish(AntennaClientView view, long gameTime) {
    if (view.stopped()) {
      return STOPPED_DISH;
    }
    return view.linked() && !view.raising(gameTime) ? LOCK : SEEK;
  }

  private static long gameTime(SignalAntennaBlockEntity antenna) {
    return antenna.getLevel() == null ? 0 : antenna.getLevel().getGameTime();
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
