package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * The tags of the damage types of the mod: the orb is a projectile for the game, so the enderman
 * dodges it as it dodges arrows (answer 1695).
 */
final class CubitDamageTypeTagsProvider extends TagsProvider<DamageType> {
  CubitDamageTypeTagsProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookup,
      ExistingFileHelper existingFiles) {
    super(output, Registries.DAMAGE_TYPE, lookup, CubitMod.MOD_ID, existingFiles);
  }

  @Override
  protected void addTags(HolderLookup.Provider lookup) {
    tag(DamageTypeTags.IS_PROJECTILE).add(CubitDamageTypes.LASER_ORB);
  }
}
