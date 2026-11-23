package io.github.zenndg.cubit.registry;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.EngineeringDeskBlock;
import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockTopBlock;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Blocks. The IDs are part of the saves, so they cannot change without a migration. */
public final class CubitBlocks {
  public static final String INDUCTION_DOCK_ID = "induction_dock";
  public static final String INDUCTION_DOCK_TOP_ID = "induction_dock_top";
  public static final String ENGINEERING_DESK_ID = "engineering_desk";
  private static final float DOCK_HARDNESS = 3.5f;

  /** As strong as bedrock: no explosion breaks a block that only its owner may break. */
  private static final float DOCK_EXPLOSION_RESISTANCE = 3_600_000f;

  private static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(Registries.BLOCK, CubitMod.MOD_ID);

  public static final Supplier<Block> INDUCTION_DOCK =
      BLOCKS.register(INDUCTION_DOCK_ID, () -> new InductionDockBlock(dockProperties()));

  /** The third block of the dock, where the panel goes; it drops nothing of its own (1896). */
  public static final Supplier<Block> INDUCTION_DOCK_TOP =
      BLOCKS.register(
          INDUCTION_DOCK_TOP_ID, () -> new InductionDockTopBlock(dockProperties().noLootTable()));

  /** The crafting desk of the mod, two blocks wide (answers 1428 and 1456). */
  public static final Supplier<Block> ENGINEERING_DESK =
      BLOCKS.register(ENGINEERING_DESK_ID, () -> new EngineeringDeskBlock(deskProperties()));

  private CubitBlocks() {}

  /** Like the dock: it resists explosions, and only its owner breaks it (answer 1445). */
  private static BlockBehaviour.Properties deskProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.METAL)
        .strength(DOCK_HARDNESS, DOCK_EXPLOSION_RESISTANCE)
        .sound(SoundType.METAL)
        .lightLevel(EngineeringDeskBlock::lightOf)
        .noOcclusion();
  }

  private static BlockBehaviour.Properties dockProperties() {
    return BlockBehaviour.Properties.of()
        .mapColor(MapColor.METAL)
        .strength(DOCK_HARDNESS, DOCK_EXPLOSION_RESISTANCE)
        .sound(SoundType.METAL)
        .noOcclusion();
  }

  public static void register(IEventBus modBus) {
    BLOCKS.register(modBus);
  }
}
