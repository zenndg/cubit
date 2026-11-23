package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/** Volume and range stay in this file's output, where a resource pack can change them. */
final class CubitSoundProvider extends SoundDefinitionsProvider {
  private static final String ROBOT = "general_robot";
  private static final String DESK = "engineering_desk";
  private static final String PATROLLER = "patroller";
  private static final String SUBTITLE_PREFIX = "subtitles.cubit.";
  private static final char FOLDER_END = '/';
  private static final char KEY_END = '.';
  private static final int RANGE_BLOCKS = 16;
  private static final float LOW_PITCH = 0.9f;
  private static final float NORMAL_PITCH = 1.0f;
  private static final float HIGH_PITCH = 1.1f;

  /** Three tones drawn by the game, so shots and swings in a row do not tire (answer 1793). */
  private static final List<Float> VARIED_PITCHES = List.of(LOW_PITCH, NORMAL_PITCH, HIGH_PITCH);

  private static final List<Float> ONE_PITCH = List.of(NORMAL_PITCH);

  CubitSoundProvider(PackOutput output, ExistingFileHelper existingFiles) {
    super(output, CubitMod.MOD_ID, existingFiles);
  }

  static String subtitleKey(String name) {
    return SUBTITLE_PREFIX + ROBOT + KEY_END + name;
  }

  static String patrolSubtitleKey(String name) {
    return subtitleKey(PATROLLER + KEY_END + name);
  }

  static String deskSubtitleKey(String name) {
    return SUBTITLE_PREFIX + DESK + KEY_END + name;
  }

  @Override
  public void registerSounds() {
    addSound(CubitSounds.TRACKS, CubitSounds.TRACKS_NAME);
    addSound(CubitSounds.HOSTILE_MOB_ALERT, CubitSounds.HOSTILE_MOB_ALERT_NAME);
    addSound(CubitSounds.RARE_ORE_ALERT, CubitSounds.RARE_ORE_ALERT_NAME);
    addSound(CubitSounds.EXPRESSION_BEEP, CubitSounds.EXPRESSION_BEEP_NAME);
    addSound(CubitSounds.SHORT_CIRCUIT, CubitSounds.SHORT_CIRCUIT_NAME);
    addSound(CubitSounds.POWER_ON, CubitSounds.POWER_ON_NAME);
    addSound(CubitSounds.POWER_OFF, CubitSounds.POWER_OFF_NAME);
    addSound(CubitSounds.RECHARGING, CubitSounds.RECHARGING_NAME);
    addSound(CubitSounds.PLACED, CubitSounds.PLACED_NAME);
    addSound(CubitSounds.SHOCK_ATTACK, CubitSounds.SHOCK_ATTACK_NAME);
    addSound(CubitSounds.AGGRESSIVE_MODE, CubitSounds.AGGRESSIVE_MODE_NAME);
    addDeskSound(CubitSounds.DESK_CRAFTING, CubitSounds.DESK_CRAFTING_NAME);
    addDeskSound(CubitSounds.DESK_DONE, CubitSounds.DESK_DONE_NAME);
    addDeskSound(CubitSounds.DESK_STARTUP, CubitSounds.DESK_STARTUP_NAME);
    registerPatrolSounds();
  }

  private void registerPatrolSounds() {
    addPatrolSound(
        CubitSounds.PATROL_PISTOL_SHOT, CubitSounds.PATROL_PISTOL_SHOT_NAME, VARIED_PITCHES);
    addPatrolSound(CubitSounds.PATROL_ORB_BURST, CubitSounds.PATROL_ORB_BURST_NAME, ONE_PITCH);
    addPatrolSound(
        CubitSounds.PATROL_BLADE_IGNITE, CubitSounds.PATROL_BLADE_IGNITE_NAME, ONE_PITCH);
    addPatrolSound(
        CubitSounds.PATROL_BLADE_SWING, CubitSounds.PATROL_BLADE_SWING_NAME, VARIED_PITCHES);
    addPatrolSound(CubitSounds.PATROL_BLADE_HIT, CubitSounds.PATROL_BLADE_HIT_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_BLADE_OFF, CubitSounds.PATROL_BLADE_OFF_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_WINGS_OPEN, CubitSounds.PATROL_WINGS_OPEN_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_WINGS_CLOSE, CubitSounds.PATROL_WINGS_CLOSE_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_JUMP, CubitSounds.PATROL_JUMP_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_VISOR_DOWN, CubitSounds.PATROL_VISOR_DOWN_NAME, ONE_PITCH);
    addPatrolSound(CubitSounds.PATROL_VISOR_UP, CubitSounds.PATROL_VISOR_UP_NAME, ONE_PITCH);
  }

  private void addSound(Supplier<SoundEvent> event, String name) {
    add(event, ROBOT, name, subtitleKey(name));
  }

  /** In their own folder, for a pack to swap them (answer 1451). */
  private void addDeskSound(Supplier<SoundEvent> event, String name) {
    add(event, DESK, name, deskSubtitleKey(name));
  }

  /**
   * In a folder of their own, inside the robot's, with the robot's range (answers 1791 and 1796).
   */
  private void addPatrolSound(Supplier<SoundEvent> event, String name, List<Float> pitches) {
    ResourceLocation file =
        ResourceLocation.fromNamespaceAndPath(
            CubitMod.MOD_ID, ROBOT + FOLDER_END + PATROLLER + FOLDER_END + name);
    SoundDefinition definition = definition().subtitle(patrolSubtitleKey(name));
    for (float pitch : pitches) {
      definition.with(sound(file).pitch(pitch).attenuationDistance(RANGE_BLOCKS));
    }
    add(event, definition);
  }

  private void add(Supplier<SoundEvent> event, String folder, String name, String subtitle) {
    ResourceLocation file =
        ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, folder + FOLDER_END + name);
    add(event, definition().subtitle(subtitle).with(sound(file).attenuationDistance(RANGE_BLOCKS)));
  }
}
