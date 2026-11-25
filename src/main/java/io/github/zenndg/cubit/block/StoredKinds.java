package io.github.zenndg.cubit.block;

import static io.github.zenndg.cubit.CubitMod.MOD_ID;

import io.github.zenndg.cubit.domain.dock.StoredKind;
import io.github.zenndg.cubit.item.PowerBankItem;
import io.github.zenndg.cubit.registry.CubitItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/** What an item is to the storage of a dock or a server (answers 947, 965 and 1361). */
final class StoredKinds {
  private StoredKinds() {}

  static StoredKind of(ItemStack stack) {
    if (PowerBankItem.is(stack)) {
      return StoredKind.POWER_BANK;
    }
    if (stack.getItem() instanceof StorableRobotItem) {
      return StoredKind.ROBOT;
    }
    if (stack.is(CubitItems.TOOLBOX.get())) {
      return StoredKind.TOOLBOX;
    }
    boolean ofTheMod = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(MOD_ID);
    // A desk that keeps items never nests (answer 1481).
    return ofTheMod && stack.canFitInsideContainerItems() ? StoredKind.MOD_ITEM : StoredKind.OTHER;
  }
}
