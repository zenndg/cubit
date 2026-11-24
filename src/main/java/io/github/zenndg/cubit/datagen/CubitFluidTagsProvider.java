package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitFluids;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The fluid in the common tag of experience, so other mods know it (answer 1818). */
final class CubitFluidTagsProvider extends FluidTagsProvider {
  CubitFluidTagsProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookup,
      ExistingFileHelper existingFiles) {
    super(output, lookup, CubitMod.MOD_ID, existingFiles);
  }

  @Override
  protected void addTags(HolderLookup.Provider lookup) {
    tag(Tags.Fluids.EXPERIENCE).add(CubitFluids.XP_FLUID.get());
  }
}
