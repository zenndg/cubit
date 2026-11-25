package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.network.ServerSpec;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * How strong each light of the Network Server is, from 0 to 1: the icons of the columns, the disks,
 * which blink, faster with energy passing, and the link antennas, lit only with the relay. The
 * light of the power comes up in steps and flickers as it goes away (answers 2338, 2341 and 2375).
 */
final class ServerLights {
  private static final String CELL = "icon_cell";
  private static final String ORB = "icon_orb";
  private static final String CHEST = "icon_chest";
  private static final String LINK = "link_";
  private static final String DISK = "hdd";
  private static final float DIM = 0.25f;
  private static final float FULL = 1f;
  private static final float ICONS_AT = 0.3f;
  private static final float LINK_AT = 0.75f;
  private static final int CALM_BLINK = 23;
  private static final int BUSY_BLINK = 7;
  private static final int BLINK_STEP = 5;
  private static final int BLINK_OFF_TICKS = 3;
  private static final float DISK_DIM = 0.15f;
  private static final int FLICKER_TICKS = 4;
  private static final float FLICKER_LOW = 0.35f;
  private static final float MAX_COLOR = 255f;

  private ServerLights() {}

  static boolean isLight(GeoBone bone) {
    String name = bone.getName();
    return CELL.equals(name) || ORB.equals(name) || CHEST.equals(name) || name.startsWith(LINK);
  }

  /** In the glow: a part without a light of its own follows the power. */
  static float of(GeoBone bone, ServerShown shown) {
    String name = bone.getName();
    float power = power(shown);
    if (CELL.equals(name)) {
      return after(shown, ICONS_AT, power);
    }
    if (CHEST.equals(name)) {
      return shown.bars().items() > 0 ? after(shown, ICONS_AT, power) : 0;
    }
    if (ORB.equals(name)) {
      // The XP of the server comes with Delivery 7, and its column stays dark until then (2370).
      return 0;
    }
    if (name.startsWith(LINK)) {
      return shown.look().linked() ? after(shown, LINK_AT, power) : 0;
    }
    return name.startsWith(DISK) ? power * disk(name, shown) : power;
  }

  /** The whole light, with the flicker of a server that loses its power. */
  static float power(ServerShown shown) {
    boolean low = shown.falling() && shown.age() / FLICKER_TICKS % 2 == 0;
    return shown.light() * (low ? FLICKER_LOW : FULL);
  }

  private static float after(ServerShown shown, float start, float power) {
    return shown.light() >= start ? power : 0;
  }

  private static float disk(String name, ServerShown shown) {
    int index = name.charAt(name.length() - 1) - '0';
    int period = (shown.look().flowing() ? BUSY_BLINK : CALM_BLINK) + index * BLINK_STEP;
    long phase = Math.floorMod(shown.age() + (long) index * ServerSpec.MAX_CHANNELS, period);
    return phase < BLINK_OFF_TICKS ? DISK_DIM : FULL;
  }

  /** An unlit lens keeps its own dim glass. */
  static int colour(float light, int colour) {
    return FastColor.ARGB32.multiply(colour, grey(DIM + (FULL - DIM) * light));
  }

  static int grey(float strength) {
    int value = Math.round(MAX_COLOR * Mth.clamp(strength, 0, FULL));
    return FastColor.ARGB32.color(value, value, value);
  }
}
