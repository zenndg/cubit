package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskSpec;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.component.ItemContainerContents;

/** The input, the output and the energy slot of a desk, and how they are kept. */
final class DeskInventories {
  private static final String SLOT_KEY = "Slot";
  private static final String INPUT_KEY = "Input";
  private static final String OUTPUT_KEY = "Output";

  private final SimpleContainer input;
  private final SimpleContainer output;
  private final SimpleContainer energySlot;

  DeskInventories(Runnable changed) {
    input = container(DeskSpec.MAX_SLOTS, changed);
    output = container(DeskSpec.MAX_SLOTS, changed);
    energySlot = container(1, changed);
  }

  private static SimpleContainer container(int size, Runnable changed) {
    return new SimpleContainer(size) {
      @Override
      public void setChanged() {
        changed.run();
      }
    };
  }

  SimpleContainer input() {
    return input;
  }

  SimpleContainer output() {
    return output;
  }

  SimpleContainer energySlot() {
    return energySlot;
  }

  void save(CompoundTag tag, HolderLookup.Provider registries) {
    DeskSaves.saveItems(tag, INPUT_KEY, input, registries);
    DeskSaves.saveItems(tag, OUTPUT_KEY, output, registries);
    DeskSaves.saveItems(tag, SLOT_KEY, energySlot, registries);
  }

  void load(CompoundTag tag, HolderLookup.Provider registries) {
    DeskSaves.loadItems(tag, INPUT_KEY, input, registries);
    DeskSaves.loadItems(tag, OUTPUT_KEY, output, registries);
    DeskSaves.loadItems(tag, SLOT_KEY, energySlot, registries);
  }

  void apply(ItemContainerContents savedInput, ItemContainerContents savedOutput) {
    DeskSaves.apply(savedInput, input);
    DeskSaves.apply(savedOutput, output);
  }

  void collect(DataComponentMap.Builder components) {
    components.set(CubitDataComponents.DESK_INPUT.get(), DeskSaves.contents(input));
    components.set(CubitDataComponents.DESK_OUTPUT.get(), DeskSaves.contents(output));
  }
}
