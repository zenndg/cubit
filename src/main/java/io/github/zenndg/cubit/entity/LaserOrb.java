package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.domain.behavior.PatrolSettings;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitEntities;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * The orb of the laser pistol: it flies straight, without gravity, and only hits the mob in its
 * way, never a protected one, with a chance of slowness. Its burst of blue particles never breaks a
 * block (answers 1551 and 1558). It lives a moment and is never saved.
 */
public final class LaserOrb extends Projectile {
  private static final double SPEED = 1.6;
  private static final int LIFETIME_TICKS = 40;
  private static final Vector3f BLUE = new Vector3f(0.36f, 0.78f, 1f);
  private static final float TRAIL_SIZE = 1.2f;
  private static final float BURST_SIZE = 1.6f;
  private static final int BURST_PARTICLES = 16;
  private static final int BURST_SPARKS = 8;
  private static final double BURST_SPREAD = 0.35;
  private static final double SPARK_SPEED = 0.15;
  private static final int AMPLIFIER_OF_LEVEL = 1;

  private float damage;
  private int slownessLevel;
  private PatrolSettings settings;

  public LaserOrb(EntityType<? extends LaserOrb> type, Level level) {
    super(type, level);
    setNoGravity(true);
  }

  /** Fired by the robot from its eyes to the center of the target. */
  static void shoot(
      RobotEntity robot,
      LivingEntity target,
      float damage,
      int slownessLevel,
      PatrolSettings settings) {
    LaserOrb orb = new LaserOrb(CubitEntities.LASER_ORB.get(), robot.level());
    orb.setOwner(robot);
    orb.damage = damage;
    orb.slownessLevel = slownessLevel;
    orb.settings = settings;
    Vec3 from = robot.getEyePosition();
    Vec3 to = target.getBoundingBox().getCenter();
    orb.setPos(from);
    orb.setDeltaMovement(to.subtract(from).normalize().scale(SPEED));
    robot.level().addFreshEntity(orb);
  }

  @Override
  protected void defineSynchedData(SynchedEntityData.Builder builder) {}

  @Override
  public void tick() {
    super.tick();
    if (tickCount > LIFETIME_TICKS) {
      discard();
      return;
    }
    HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
    if (hit.getType() != HitResult.Type.MISS) {
      // As in the game: a mob that sends projectiles back, as the breeze, turns the orb (1696).
      hitTargetOrDeflectSelf(hit);
    }
    setPos(position().add(getDeltaMovement()));
    if (level().isClientSide()) {
      level()
          .addParticle(new DustParticleOptions(BLUE, TRAIL_SIZE), getX(), getY(), getZ(), 0, 0, 0);
    }
  }

  /**
   * Only a mob the Patroller attacks: it passes through the protected ones, the golems and the
   * tamed mobs, so it never hurts one (answers 1570, 1633 and 1701 to 1705).
   */
  @Override
  protected boolean canHitEntity(Entity entity) {
    return super.canHitEntity(entity)
        && entity instanceof LivingEntity living
        && getOwner() instanceof RobotEntity robot
        && PatrolProtection.isTarget(robot, living);
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    super.onHitEntity(result);
    if (!(result.getEntity() instanceof LivingEntity target)
        || !(getOwner() instanceof RobotEntity robot)
        || level().isClientSide()) {
      return;
    }
    Player owner = RobotInteractions.onlineOwner(robot);
    if (owner != null) {
      target.setLastHurtByPlayer(owner);
    }
    target.hurt(CubitDamageTypes.laserOrb(this, robot), damage);
    if (settings != null && settings.slows(random.nextInt(Percentage.MAX))) {
      target.addEffect(
          new MobEffectInstance(
              MobEffects.MOVEMENT_SLOWDOWN,
              settings.slownessTicks(),
              slownessLevel - AMPLIFIER_OF_LEVEL));
    }
  }

  @Override
  protected void onHit(HitResult result) {
    super.onHit(result);
    if (!level().isClientSide()) {
      burst();
      discard();
    }
  }

  @Override
  protected void onHitBlock(BlockHitResult result) {
    super.onHitBlock(result);
  }

  /** A ring of blue, as a splash potion breaking, with a few sparks (answer 1551). */
  private void burst() {
    if (!(level() instanceof ServerLevel server)) {
      return;
    }
    server.sendParticles(
        new DustParticleOptions(BLUE, BURST_SIZE),
        getX(),
        getY(),
        getZ(),
        BURST_PARTICLES,
        BURST_SPREAD,
        BURST_SPREAD,
        BURST_SPREAD,
        0);
    server.sendParticles(
        ParticleTypes.ELECTRIC_SPARK,
        getX(),
        getY(),
        getZ(),
        BURST_SPARKS,
        BURST_SPREAD,
        BURST_SPREAD,
        BURST_SPREAD,
        SPARK_SPEED);
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag tag) {
    discard();
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag tag) {}
}
