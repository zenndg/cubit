package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.desk.DeskLook;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * The colors that change with what the desk does: the screen of the laptop, blue with a soft pulse
 * while crafting, amber in a pause and dark when parked, and the lamp, lit, at half light or off
 * (answers 1454, 1470 and 1510). They tint the model and its glow alike.
 */
final class DeskGlow {
  static final String SCREEN = "laptop_screen";
  static final String LAMP = "lamp_light";
  static final int CRAFTING_BLUE = 0x4FC3F7;
  static final int PAUSE_AMBER = 0xFFB300;
  private static final int SCREEN_OFF = 0x1A1A1A;
  private static final int LAMP_ON = 0xFFFFFF;
  private static final int LAMP_HALF = 0x8C8C8C;
  private static final int LAMP_OFF = 0x3A3A3A;
  private static final float PULSE_TICKS = 40f;
  private static final float PULSE_DEPTH = 0.2f;
  private static final float FULL = 1f;

  private DeskGlow() {}

  static int colour(GeoBone bone, DeskLook look, float ticks, int colour) {
    String name = bone.getName();
    if (SCREEN.equals(name)) {
      return tint(colour, screen(look, ticks));
    }
    return LAMP.equals(name) ? tint(colour, lamp(look)) : colour;
  }

  /** The item in the hand is a parked desk. */
  static int parked(GeoBone bone, int colour) {
    return colour(bone, DeskLook.IDLE, 0, colour);
  }

  private static int screen(DeskLook look, float ticks) {
    return switch (look) {
      case IDLE -> SCREEN_OFF;
      case PAUSED -> PAUSE_AMBER;
      case CRAFTING -> scaled(CRAFTING_BLUE, pulse(ticks));
    };
  }

  private static int lamp(DeskLook look) {
    return switch (look) {
      case IDLE -> LAMP_OFF;
      case PAUSED -> LAMP_HALF;
      case CRAFTING -> LAMP_ON;
    };
  }

  private static float pulse(float ticks) {
    float wave = Mth.sin(ticks * Mth.TWO_PI / PULSE_TICKS);
    return FULL - PULSE_DEPTH + PULSE_DEPTH * wave;
  }

  static int scaled(int rgb, float factor) {
    return FastColor.ARGB32.color(
        (int) (FastColor.ARGB32.red(rgb) * factor),
        (int) (FastColor.ARGB32.green(rgb) * factor),
        (int) (FastColor.ARGB32.blue(rgb) * factor));
  }

  private static int tint(int colour, int rgb) {
    return FastColor.ARGB32.multiply(colour, FastColor.ARGB32.opaque(rgb));
  }
}
