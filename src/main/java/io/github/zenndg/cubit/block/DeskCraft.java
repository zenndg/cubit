package io.github.zenndg.cubit.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

/** What one craft would take and give, read again on the tick it ends. */
record DeskCraft(DeskMaterials.Pick pick, List<ItemStack> outputs) {
  /** Nothing when the recipe does not take the picked items. */
  static Optional<DeskCraft> of(CraftingRecipe recipe, DeskMaterials.Pick pick, Level level) {
    if (!recipe.matches(pick.input(), level)) {
      return Optional.empty();
    }
    ItemStack result = recipe.assemble(pick.input(), level.registryAccess());
    if (result.isEmpty()) {
      return Optional.empty();
    }
    List<ItemStack> outputs = new ArrayList<>();
    outputs.add(result);
    recipe.getRemainingItems(pick.input()).stream()
        .filter(stack -> !stack.isEmpty())
        .forEach(outputs::add);
    return Optional.of(new DeskCraft(pick, outputs));
  }
}
