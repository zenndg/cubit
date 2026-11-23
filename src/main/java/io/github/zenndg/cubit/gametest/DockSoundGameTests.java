package io.github.zenndg.cubit.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The three sounds of the panel of the Dock: a dedicated server hears nothing, so these check what
 * reaches the client, the names, the subtitles and the files (answers 1861 and 1921).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockSoundGameTests {
  private static final String ARENA = "arena";
  private static final String SOUNDS_FILE = "/assets/cubit/sounds.json";
  private static final String PREFIX = "induction_dock.";
  private static final String FOLDER = "/assets/cubit/sounds/induction_dock/";
  private static final String EXTENSION = ".ogg";
  private static final String SUBTITLE = "subtitle";
  private static final String SUBTITLE_PREFIX = "subtitles.cubit.";
  private static final List<String> NAMES =
      List.of(
          CubitSounds.DOCK_PANEL_DEPLOY_NAME,
          CubitSounds.DOCK_PANEL_LOCK_NAME,
          CubitSounds.DOCK_PANEL_RETRACT_NAME);
  private static final String NOT_REGISTERED = "Sound not registered: ";
  private static final String NOT_DEFINED = "Sound not in sounds.json: ";
  private static final String WRONG_SUBTITLE = "Wrong subtitle for: ";
  private static final String NO_FILE = "Sound file missing: ";
  private static final String BAD_FILE = "sounds.json could not be read";

  private DockSoundGameTests() {}

  @GameTest(template = ARENA)
  public static void theThreeDockSoundsAreRegistered(GameTestHelper helper) {
    for (String name : NAMES) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, PREFIX + name);
      helper.assertTrue(BuiltInRegistries.SOUND_EVENT.containsKey(id), NOT_REGISTERED + name);
    }
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void theSoundsFileDefinesTheDockSoundsWithSubtitles(GameTestHelper helper) {
    JsonObject sounds = soundsFile(helper);
    for (String name : NAMES) {
      helper.assertTrue(sounds.has(PREFIX + name), NOT_DEFINED + name);
      String subtitle = sounds.getAsJsonObject(PREFIX + name).get(SUBTITLE).getAsString();
      helper.assertTrue(subtitle.equals(SUBTITLE_PREFIX + PREFIX + name), WRONG_SUBTITLE + name);
    }
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void everyDockSoundHasItsFile(GameTestHelper helper) {
    for (String name : NAMES) {
      try (InputStream file =
          DockSoundGameTests.class.getResourceAsStream(FOLDER + name + EXTENSION)) {
        helper.assertTrue(file != null, NO_FILE + name);
      } catch (IOException exception) {
        helper.fail(NO_FILE + name);
      }
    }
    helper.succeed();
  }

  private static JsonObject soundsFile(GameTestHelper helper) {
    try (InputStream file = DockSoundGameTests.class.getResourceAsStream(SOUNDS_FILE)) {
      if (file == null) {
        helper.fail(BAD_FILE);
      }
      return JsonParser.parseReader(new InputStreamReader(file, StandardCharsets.UTF_8))
          .getAsJsonObject();
    } catch (IOException exception) {
      helper.fail(BAD_FILE);
      return new JsonObject();
    }
  }
}
