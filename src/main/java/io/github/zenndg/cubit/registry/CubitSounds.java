package io.github.zenndg.cubit.registry;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.ContinuousSound;
import io.github.zenndg.cubit.domain.behavior.PatrolSound;
import io.github.zenndg.cubit.domain.behavior.PowerSound;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The sounds, named by robot type, so another robot can have its own without a migration. */
public final class CubitSounds {
  public static final String TRACKS_NAME = "tracks";
  public static final String HOSTILE_MOB_ALERT_NAME = "hostile_mob_alert";
  public static final String RARE_ORE_ALERT_NAME = "rare_ore_alert";
  public static final String EXPRESSION_BEEP_NAME = "expression_beep";
  public static final String SHORT_CIRCUIT_NAME = "short_circuit";
  public static final String POWER_ON_NAME = "power_on";
  public static final String POWER_OFF_NAME = "power_off";
  public static final String RECHARGING_NAME = "recharging";
  public static final String PLACED_NAME = "placed";
  public static final String SHOCK_ATTACK_NAME = "shock_attack";
  public static final String AGGRESSIVE_MODE_NAME = "aggressive_mode";
  public static final String DESK_CRAFTING_NAME = "crafting";
  public static final String DESK_DONE_NAME = "done";
  public static final String DESK_STARTUP_NAME = "startup";
  public static final String DOCK_PANEL_DEPLOY_NAME = "panel_deploy";
  public static final String DOCK_PANEL_LOCK_NAME = "panel_lock";
  public static final String DOCK_PANEL_RETRACT_NAME = "panel_retract";
  public static final String PATROL_PISTOL_SHOT_NAME = "pistol_shot";
  public static final String PATROL_ORB_BURST_NAME = "orb_burst";
  public static final String PATROL_BLADE_IGNITE_NAME = "blade_ignite";
  public static final String PATROL_BLADE_SWING_NAME = "blade_swing";
  public static final String PATROL_BLADE_HIT_NAME = "blade_hit";
  public static final String PATROL_BLADE_OFF_NAME = "blade_off";
  public static final String PATROL_WINGS_OPEN_NAME = "wings_open";
  public static final String PATROL_WINGS_CLOSE_NAME = "wings_close";
  public static final String PATROL_JUMP_NAME = "jump";
  public static final String PATROL_VISOR_DOWN_NAME = "visor_down";
  public static final String PATROL_VISOR_UP_NAME = "visor_up";
  private static final String GENERAL_ROBOT_PREFIX = "general_robot.";
  private static final String PATROLLER_PREFIX = "patroller.";
  private static final String DESK_PREFIX = "engineering_desk.";
  private static final String DOCK_PREFIX = "induction_dock.";
  private static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(Registries.SOUND_EVENT, CubitMod.MOD_ID);

  public static final Supplier<SoundEvent> TRACKS = generalRobot(TRACKS_NAME);
  public static final Supplier<SoundEvent> HOSTILE_MOB_ALERT = generalRobot(HOSTILE_MOB_ALERT_NAME);
  public static final Supplier<SoundEvent> RARE_ORE_ALERT = generalRobot(RARE_ORE_ALERT_NAME);
  public static final Supplier<SoundEvent> EXPRESSION_BEEP = generalRobot(EXPRESSION_BEEP_NAME);
  public static final Supplier<SoundEvent> SHORT_CIRCUIT = generalRobot(SHORT_CIRCUIT_NAME);
  public static final Supplier<SoundEvent> POWER_ON = generalRobot(POWER_ON_NAME);
  public static final Supplier<SoundEvent> POWER_OFF = generalRobot(POWER_OFF_NAME);
  public static final Supplier<SoundEvent> RECHARGING = generalRobot(RECHARGING_NAME);
  public static final Supplier<SoundEvent> PLACED = generalRobot(PLACED_NAME);
  public static final Supplier<SoundEvent> SHOCK_ATTACK = generalRobot(SHOCK_ATTACK_NAME);
  public static final Supplier<SoundEvent> AGGRESSIVE_MODE = generalRobot(AGGRESSIVE_MODE_NAME);

  /** The sounds of the Engineering Desk, which a pack can swap (answers 1450 to 1452). */
  public static final Supplier<SoundEvent> DESK_CRAFTING = desk(DESK_CRAFTING_NAME);

  public static final Supplier<SoundEvent> DESK_DONE = desk(DESK_DONE_NAME);
  public static final Supplier<SoundEvent> DESK_STARTUP = desk(DESK_STARTUP_NAME);

  /** The panel of the Induction Dock, which a pack can swap (answers 1861 and 1921). */
  public static final Supplier<SoundEvent> DOCK_PANEL_DEPLOY = dock(DOCK_PANEL_DEPLOY_NAME);

  public static final Supplier<SoundEvent> DOCK_PANEL_LOCK = dock(DOCK_PANEL_LOCK_NAME);
  public static final Supplier<SoundEvent> DOCK_PANEL_RETRACT = dock(DOCK_PANEL_RETRACT_NAME);

  /** The sounds of the Galactic Patroller, in a folder of their own (answers 1575 and 1791). */
  public static final Supplier<SoundEvent> PATROL_PISTOL_SHOT = patroller(PATROL_PISTOL_SHOT_NAME);

  public static final Supplier<SoundEvent> PATROL_ORB_BURST = patroller(PATROL_ORB_BURST_NAME);
  public static final Supplier<SoundEvent> PATROL_BLADE_IGNITE =
      patroller(PATROL_BLADE_IGNITE_NAME);
  public static final Supplier<SoundEvent> PATROL_BLADE_SWING = patroller(PATROL_BLADE_SWING_NAME);
  public static final Supplier<SoundEvent> PATROL_BLADE_HIT = patroller(PATROL_BLADE_HIT_NAME);
  public static final Supplier<SoundEvent> PATROL_BLADE_OFF = patroller(PATROL_BLADE_OFF_NAME);
  public static final Supplier<SoundEvent> PATROL_WINGS_OPEN = patroller(PATROL_WINGS_OPEN_NAME);
  public static final Supplier<SoundEvent> PATROL_WINGS_CLOSE = patroller(PATROL_WINGS_CLOSE_NAME);
  public static final Supplier<SoundEvent> PATROL_JUMP = patroller(PATROL_JUMP_NAME);
  public static final Supplier<SoundEvent> PATROL_VISOR_DOWN = patroller(PATROL_VISOR_DOWN_NAME);
  public static final Supplier<SoundEvent> PATROL_VISOR_UP = patroller(PATROL_VISOR_UP_NAME);

  private CubitSounds() {}

  public static void register(IEventBus modBus) {
    SOUNDS.register(modBus);
  }

  public static SoundEvent of(PowerSound sound) {
    return switch (sound) {
      case POWER_ON -> POWER_ON.get();
      case POWER_OFF -> POWER_OFF.get();
    };
  }

  public static SoundEvent of(ContinuousSound sound) {
    return switch (sound) {
      case TRACKS -> TRACKS.get();
      case SHORT_CIRCUIT -> SHORT_CIRCUIT.get();
      case RECHARGING -> RECHARGING.get();
    };
  }

  public static SoundEvent of(PatrolSound sound) {
    return switch (sound) {
      case VISOR_DOWN -> PATROL_VISOR_DOWN.get();
      case VISOR_UP -> PATROL_VISOR_UP.get();
      case BLADE_IGNITE -> PATROL_BLADE_IGNITE.get();
      case BLADE_OFF -> PATROL_BLADE_OFF.get();
      case WINGS_OPEN -> PATROL_WINGS_OPEN.get();
      case WINGS_CLOSE -> PATROL_WINGS_CLOSE.get();
    };
  }

  private static Supplier<SoundEvent> generalRobot(String name) {
    return sound(GENERAL_ROBOT_PREFIX + name);
  }

  private static Supplier<SoundEvent> patroller(String name) {
    return generalRobot(PATROLLER_PREFIX + name);
  }

  private static Supplier<SoundEvent> dock(String name) {
    return sound(DOCK_PREFIX + name);
  }

  private static Supplier<SoundEvent> desk(String name) {
    return sound(DESK_PREFIX + name);
  }

  private static Supplier<SoundEvent> sound(String path) {
    return SOUNDS.register(
        path,
        () ->
            SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path)));
  }
}
