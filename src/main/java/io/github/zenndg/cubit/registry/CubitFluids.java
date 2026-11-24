package io.github.zenndg.cubit.registry;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.XpFluid;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** The fluid of the XP (answers 1815 and 1816). */
public final class CubitFluids {
  private static final String XP_FLUID_NAME = "xp_fluid";
  private static final DeferredRegister<FluidType> TYPES =
      DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, CubitMod.MOD_ID);
  private static final DeferredRegister<Fluid> FLUIDS =
      DeferredRegister.create(Registries.FLUID, CubitMod.MOD_ID);

  public static final Supplier<FluidType> XP_FLUID_TYPE =
      TYPES.register(XP_FLUID_NAME, () -> new FluidType(FluidType.Properties.create()));
  public static final Supplier<Fluid> XP_FLUID = FLUIDS.register(XP_FLUID_NAME, XpFluid::new);

  private CubitFluids() {}

  public static void register(IEventBus modBus) {
    TYPES.register(modBus);
    FLUIDS.register(modBus);
  }
}
