package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.function.Supplier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

/** Volume and range stay in this file's output, where a resource pack can change them. */
final class CubitSoundProvider extends SoundDefinitionsProvider {
  private static final String ROBOT = "general_robot";
  private static final String DESK = "engineering_desk";
  private static final String SUBTITLE_PREFIX = "subtitles.cubit.";
  private static final char FOLDER_END = '/';
  private static final char KEY_END = '.';
  private static final int RANGE_BLOCKS = 16;

  CubitSoundProvider(PackOutput output, ExistingFileHelper existingFiles) {
    super(output, CubitMod.MOD_ID, existingFiles);
  }

  static String subtitleKey(String name) {
    return SUBTITLE_PREFIX + ROBOT + KEY_END + name;
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
  }

  private void addSound(Supplier<SoundEvent> event, String name) {
    add(event, ROBOT, name, subtitleKey(name));
  }

  /** In their own folder, for a pack to swap them (answer 1451). */
  private void addDeskSound(Supplier<SoundEvent> event, String name) {
    add(event, DESK, name, deskSubtitleKey(name));
  }

  private void add(Supplier<SoundEvent> event, String folder, String name, String subtitle) {
    ResourceLocation file =
        ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, folder + FOLDER_END + name);
    add(event, definition().subtitle(subtitle).with(sound(file).attenuationDistance(RANGE_BLOCKS)));
  }
}
