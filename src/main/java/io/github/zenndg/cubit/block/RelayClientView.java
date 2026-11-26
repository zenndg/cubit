package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.PanelAim;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * What a client shows of a Solar Relay, moved on by itself between two updates: where the panel
 * aims, little by little after the sun, and the dish, which turns to the server while it transmits
 * (answers 2338, 2375 and 2385). It lives in common code, but only the client fills it.
 */
public final class RelayClientView {
  private static final float AIM_DEGREES = 1.5f;
  private static final float DISH_DEGREES = 2.5f;

  /** The most the dish turns without leaving the block. */
  private static final float DISH_MOST = 40;

  private Optional<BlockPos> server = Optional.empty();
  private boolean transmitting;
  private float tilt;
  private float lastTilt;
  private float roll;
  private float lastRoll;
  private float dish;
  private float lastDish;
  private boolean aimed;

  void receive(Optional<BlockPos> pairedServer, boolean transmits) {
    server = pairedServer;
    transmitting = transmits;
  }

  void tick(BlockPos pos, Direction facing, long dayTime) {
    lastTilt = tilt;
    lastRoll = roll;
    lastDish = dish;
    PanelAim aim = PanelAim.of(sideOf(facing), dayTime);
    if (aimed) {
      tilt = Mth.approach(tilt, (float) aim.tilt(), AIM_DEGREES);
      roll = Mth.approach(roll, (float) aim.roll(), AIM_DEGREES);
    } else {
      tilt = (float) aim.tilt();
      roll = (float) aim.roll();
      lastTilt = tilt;
      lastRoll = roll;
      aimed = true;
    }
    float wanted = transmitting ? server.map(to -> yawTo(pos, to, facing)).orElse(0f) : 0f;
    dish = Mth.approach(dish, wanted, DISH_DEGREES);
  }

  private static PanelAim.Side sideOf(Direction facing) {
    return switch (facing) {
      case SOUTH -> PanelAim.Side.SOUTH;
      case EAST -> PanelAim.Side.EAST;
      case WEST -> PanelAim.Side.WEST;
      default -> PanelAim.Side.NORTH;
    };
  }

  /** The turn from the front of the block, in the space of the model. */
  private static float yawTo(BlockPos from, BlockPos to, Direction facing) {
    int x = to.getX() - from.getX();
    int z = to.getZ() - from.getZ();
    Direction side = facing.getClockWise();
    int ahead = x * facing.getStepX() + z * facing.getStepZ();
    int aside = x * side.getStepX() + z * side.getStepZ();
    float yaw = (float) Math.toDegrees(Math.atan2(-aside, ahead));
    return Mth.clamp(yaw, -DISH_MOST, DISH_MOST);
  }

  public boolean linked() {
    return server.isPresent();
  }

  public boolean transmitting() {
    return transmitting;
  }

  public float tilt(float partialTick) {
    return Mth.lerp(partialTick, lastTilt, tilt);
  }

  public float roll(float partialTick) {
    return Mth.lerp(partialTick, lastRoll, roll);
  }

  public float dish(float partialTick) {
    return Mth.lerp(partialTick, lastDish, dish);
  }
}
