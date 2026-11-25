package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.ServerFront;
import io.github.zenndg.cubit.domain.network.ServerLook;
import net.minecraft.util.Mth;

/**
 * What a client shows of a Network Server, moved on by itself between two updates: the lights that
 * come up as it starts and go down as it falls, the fans that speed up and slow down and the bars
 * that climb one by one (answers 2338, 2375 and 2391). A server just loaded shows no motion. It
 * lives in common code, but only the client fills it.
 */
public final class ServerClientView {
  private static final int STARTUP_TICKS = 60;
  private static final int FALL_TICKS = 30;
  private static final int BAR_TICKS = 2;
  private static final float FULL = 1f;
  private static final float FAN_DEGREES = 24f;
  private static final float FAN_CHANGE = 0.8f;
  private static final float WHOLE_TURN = 360f;

  private ServerLook look = ServerLook.OFF;
  private boolean known;
  private int starting;
  private float light;
  private float lastLight;
  private float fanSpeed;
  private float fanAngle;
  private int age;
  private ServerLook.Columns bars = ServerLook.OFF.columns();

  void receive(ServerLook next) {
    boolean starts = known && next.powered() && !look.powered();
    look = next;
    if (starts) {
      starting = STARTUP_TICKS;
    }
    if (!next.powered()) {
      starting = 0;
    }
    if (!known) {
      known = true;
      light = next.powered() ? FULL : 0;
      lastLight = light;
      bars = next.columns();
    }
  }

  void tick() {
    age++;
    lastLight = light;
    float change = look.powered() ? FULL / STARTUP_TICKS : -FULL / FALL_TICKS;
    light = Mth.clamp(light + change, 0, FULL);
    starting = Math.max(starting - 1, 0);
    float wanted = fansTurn() ? FAN_DEGREES : 0;
    fanSpeed = Mth.approach(fanSpeed, wanted, FAN_CHANGE);
    fanAngle = (fanAngle + fanSpeed) % WHOLE_TURN;
    if (age % BAR_TICKS == 0) {
      bars =
          new ServerLook.Columns(
              toward(bars.energy(), look.columns().energy()),
              toward(bars.items(), look.columns().items()));
    }
  }

  private static int toward(int shown, int wanted) {
    return Mth.clamp(shown + Integer.signum(wanted - shown), 0, ServerFront.BARS);
  }

  public ServerLook look() {
    return look;
  }

  public ServerLook.Columns bars() {
    return bars;
  }

  /** From 0, dark, to 1. */
  public float light(float partialTick) {
    return Mth.lerp(partialTick, lastLight, light);
  }

  public boolean isStarting() {
    return starting > 0;
  }

  public boolean isFalling() {
    return !look.powered() && light > 0;
  }

  /** While energy passes, once it started, for the fans and their sound. */
  public boolean fansTurn() {
    return look.flowing() && look.powered() && starting == 0;
  }

  public float fanDegrees(float partialTick) {
    return fanAngle + fanSpeed * partialTick;
  }
}
