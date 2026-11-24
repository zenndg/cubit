package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.CubitMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

/** The textures of the Cubit XP Fluid, which tanks and pipes of other mods also draw (1845). */
enum XpFluidLook implements IClientFluidTypeExtensions {
  INSTANCE;

  static final ResourceLocation STILL = texture("block/xp_fluid_still");
  private static final ResourceLocation FLOW = texture("block/xp_fluid_flow");

  private static ResourceLocation texture(String path) {
    return ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
  }

  @Override
  public ResourceLocation getStillTexture() {
    return STILL;
  }

  @Override
  public ResourceLocation getFlowingTexture() {
    return FLOW;
  }
}
