package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * The extra armor of the card of the Galactic Patroller, as an attribute of the game, so the game
 * reduces the damage. It comes and goes with the card (answers 1552 and 1555).
 */
final class PatrolArmor {
  private static final String MODIFIER_PATH = "patroller_armor";
  private static final ResourceLocation MODIFIER_ID =
      ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, MODIFIER_PATH);

  private PatrolArmor() {}

  static void apply(RobotEntity robot, int armor) {
    AttributeInstance attribute = robot.getAttribute(Attributes.ARMOR);
    if (attribute == null) {
      return;
    }
    AttributeModifier current = attribute.getModifier(MODIFIER_ID);
    if (armor <= 0) {
      if (current != null) {
        attribute.removeModifier(MODIFIER_ID);
      }
      return;
    }
    if (current == null || current.amount() != armor) {
      attribute.addOrUpdateTransientModifier(
          new AttributeModifier(MODIFIER_ID, armor, AttributeModifier.Operation.ADD_VALUE));
    }
  }
}
