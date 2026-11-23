package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.domain.dock.DockDoorLight;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.FastColor;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * The lights on the edges of the door leaves of the two cabinets, in the color the domain picks,
 * lit in the dark (answers 1853 and 1863). The yellow one spends half of each blink dim.
 */
final class DockLights {
  private static final String RIGHT_UP = "door_r_up_light";
  private static final String RIGHT_DOWN = "door_r_down_light";
  private static final String LEFT_UP = "door_l_up_light";
  private static final String LEFT_DOWN = "door_l_down_light";
  private static final Set<String> BONES = Set.of(RIGHT_UP, RIGHT_DOWN, LEFT_UP, LEFT_DOWN);
  private static final Map<DockDoorLight, Integer> COLORS =
      Map.of(
          DockDoorLight.RED, 0xFF3B30,
          DockDoorLight.YELLOW, 0xFFC21A,
          DockDoorLight.GREEN, 0x3BE070);
  private static final int DIM_YELLOW = 0x4A3A0C;

  private DockLights() {}

  static boolean isLight(GeoBone bone) {
    return BONES.contains(bone.getName());
  }

  static int colour(DockDoorLight light, long ticks, int colour) {
    int rgb = light.isLit(ticks) ? COLORS.get(light) : DIM_YELLOW;
    return FastColor.ARGB32.multiply(colour, FastColor.ARGB32.opaque(rgb));
  }
}
