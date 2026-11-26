package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.PanelState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * The animations of the blocks of the Network: the startup of the server and the panel of the
 * relay, which unfolds leaf by leaf and leans only then (answers 2338, 2384 and 2391). What follows
 * something that moves, as the fans and the sun, is set on the bones by the models.
 */
public final class NetworkAnimations {
  private static final String DOOR = "door";
  private static final String PANEL = "panel";
  private static final String SERVER = "network_server.";
  private static final String RELAY = "solar_relay.";

  /** As in the Dock, a pose needs a short blend to be taken from the queue of GeckoLib. */
  private static final int POSE_BLEND_TICKS = 2;

  private static final RawAnimation IDLE = hold(SERVER + "idle");
  private static final RawAnimation STARTUP = hold(SERVER + "startup");
  private static final RawAnimation STORED = hold(RELAY + "stored");
  private static final RawAnimation DEPLOYED = hold(RELAY + "deployed");
  private static final RawAnimation DEPLOY = hold(RELAY + "deploy");
  private static final RawAnimation RETRACT = hold(RELAY + "retract");

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

  private static RawAnimation pose(PanelState panel) {
    return switch (panel) {
      case STORED -> STORED;
      case DEPLOYING -> DEPLOY;
      case DEPLOYED -> DEPLOYED;
      case RETRACTING -> RETRACT;
    };
  }
}
