package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.domain.desk.DrawerPace;
import io.github.zenndg.cubit.registry.CubitSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * What the server shows of a desk: the start up, the drawers, the item ready, the people at the
 * screen and the updates the clients need (answers 1466, 1471, 1508, 1511 and 1512). The desk keeps
 * the rules of the work, and this class only tells the clients.
 */
public final class DeskShow {
  /** Where the claw stands, in pixels from the center of the left half (answer 1471). */
  private static final double CLAW_RIGHT_PIXELS = 20.5;

  private static final double CLAW_FRONT_PIXELS = 2.25;
  private static final double CLAW_HEIGHT_PIXELS = 19.5;
  private static final double PIXELS = 16;
  private static final int SPARKS = 8;
  private static final double SPARK_SPREAD = 0.08;
  private static final double SPARK_SPEED = 0.1;
  private static final float VOLUME = 1f;
  private static final float PITCH = 1f;

  private boolean startUpPending;
  private boolean clientsBehind;
  private int viewers;
  private int crafts;
  private int inputCount;
  private int outputCount;
  private DrawerPace inputDrawer = DrawerPace.NEVER;
  private DrawerPace outputDrawer = DrawerPace.NEVER;

  /** On the next tick, when the clients already have the block (answer 1466). */
  void startUp() {
    startUpPending = true;
  }

  /** Someone opened the screen: the lid opens while anyone is at it (answer 1512). */
  public void viewerJoined() {
    viewers++;
    clientsBehind = true;
  }

  public void viewerLeft() {
    viewers = Math.max(0, viewers - 1);
    clientsBehind = true;
  }

  /** The progress or the recipe changed in a way the clients cannot guess. */
  void tellClients() {
    clientsBehind = true;
  }

  /**
   * Before the work of a tick: what came into the input or left the output since the last tick
   * opens a drawer. What the craft itself moves happens after this and is not counted.
   */
  void beforeWork(EngineeringDeskBlockEntity desk, long gameTime) {
    if (count(desk.input()) > inputCount && inputDrawer.allows(gameTime)) {
      inputDrawer = inputDrawer.open(gameTime);
      desk.triggerAnim(DeskAnimations.ACTIONS, DeskAnimations.DRAWER_IN);
    }
    if (count(desk.output()) < outputCount && outputDrawer.allows(gameTime)) {
      outputDrawer = outputDrawer.open(gameTime);
      desk.triggerAnim(DeskAnimations.ACTIONS, DeskAnimations.DRAWER_OUT);
    }
  }

  void afterWork(
      EngineeringDeskBlockEntity desk, Level level, BlockPos pos, BlockState state, DeskLook look) {
    inputCount = count(desk.input());
    outputCount = count(desk.output());
    if (startUpPending) {
      startUpPending = false;
      desk.triggerAnim(DeskAnimations.ACTIONS, DeskAnimations.STARTUP);
      play(level, pos, CubitSounds.DESK_STARTUP.get());
    }
    if (state.getValue(EngineeringDeskBlock.LOOK).look() != look) {
      EngineeringDeskBlock.showLook(level, pos, state, look);
      clientsBehind = true;
    }
    if (clientsBehind) {
      clientsBehind = false;
      level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
  }

  /** The hologram shines, the claw sparks and the right drawer keeps the item (answer 1471). */
  void crafted(EngineeringDeskBlockEntity desk, Level level, BlockPos pos, BlockState state) {
    crafts++;
    clientsBehind = true;
    desk.triggerAnim(DeskAnimations.ACTIONS, DeskAnimations.DONE);
    play(level, pos, CubitSounds.DESK_DONE.get());
    if (level instanceof ServerLevel server) {
      Vec3 claw = claw(pos, state);
      server.sendParticles(
          ParticleTypes.ELECTRIC_SPARK,
          claw.x,
          claw.y,
          claw.z,
          SPARKS,
          SPARK_SPREAD,
          SPARK_SPREAD,
          SPARK_SPREAD,
          SPARK_SPEED);
    }
  }

  /** What the clients get, beside the owner, the tier and the order. */
  void write(CompoundTag tag) {
    tag.putInt(DeskClientView.VIEWERS_KEY, viewers);
    tag.putInt(DeskClientView.CRAFTS_KEY, crafts);
  }

  int viewers() {
    return viewers;
  }

  private static void play(Level level, BlockPos pos, SoundEvent sound) {
    level.playSound(null, pos, sound, SoundSource.BLOCKS, VOLUME, PITCH);
  }

  /** The model is drawn from the left half, with the arm over the right one, at the front. */
  private static Vec3 claw(BlockPos left, BlockState state) {
    Direction right = EngineeringDeskBlock.towardRight(state);
    Direction front = state.getValue(EngineeringDeskBlock.FACING);
    return Vec3.atBottomCenterOf(left)
        .add(Vec3.atLowerCornerOf(right.getNormal()).scale(CLAW_RIGHT_PIXELS / PIXELS))
        .add(Vec3.atLowerCornerOf(front.getNormal()).scale(CLAW_FRONT_PIXELS / PIXELS))
        .add(0, CLAW_HEIGHT_PIXELS / PIXELS, 0);
  }

  private static int count(Container container) {
    int total = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      total += container.getItem(slot).getCount();
    }
    return total;
  }
}
