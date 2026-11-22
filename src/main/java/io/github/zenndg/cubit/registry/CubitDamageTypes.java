package io.github.zenndg.cubit.registry;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

/** The damage of the robot's shock. The ID is part of the saves and of the death messages. */
public final class CubitDamageTypes {
  private static final String SHOCK_ID = "shock";
  public static final ResourceKey<DamageType> SHOCK =
      ResourceKey.create(
          Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, SHOCK_ID));

  /** The game builds the death message key as death.attack. followed by this id. */
  public static final String SHOCK_MESSAGE_ID = CubitMod.MOD_ID + "." + SHOCK_ID;

  private static final String LASER_ORB_ID = "laser_orb";
  private static final String PLASMA_BLADE_ID = "plasma_blade";

  /** The orb of the pistol and the blade of the Galactic Patroller (answers 1551 and 1554). */
  public static final ResourceKey<DamageType> LASER_ORB = key(LASER_ORB_ID);

  public static final ResourceKey<DamageType> PLASMA_BLADE = key(PLASMA_BLADE_ID);
  public static final String LASER_ORB_MESSAGE_ID = CubitMod.MOD_ID + "." + LASER_ORB_ID;
  public static final String PLASMA_BLADE_MESSAGE_ID = CubitMod.MOD_ID + "." + PLASMA_BLADE_ID;

  private CubitDamageTypes() {}

  private static ResourceKey<DamageType> key(String id) {
    return ResourceKey.create(
        Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, id));
  }

  public static DamageSource shock(Entity robot) {
    return source(robot, SHOCK, robot);
  }

  /** The orb hits, and the robot that shot it gets the kill. */
  public static DamageSource laserOrb(Entity orb, Entity robot) {
    return source(robot, LASER_ORB, orb);
  }

  public static DamageSource plasmaBlade(Entity robot) {
    return source(robot, PLASMA_BLADE, robot);
  }

  private static DamageSource source(Entity robot, ResourceKey<DamageType> type, Entity direct) {
    return new DamageSource(
        robot
            .level()
            .registryAccess()
            .registryOrThrow(Registries.DAMAGE_TYPE)
            .getHolderOrThrow(type),
        direct,
        robot);
  }
}
