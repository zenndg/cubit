package io.github.zenndg.cubit.registry;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DeskFaces;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.block.InductionDockBlock;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.block.NetworkBlock;
import io.github.zenndg.cubit.block.NetworkBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.block.SolarRelayBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Block entities and the capabilities they offer to other mods. */
public final class CubitBlockEntities {
  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
      DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CubitMod.MOD_ID);

  public static final Supplier<BlockEntityType<InductionDockBlockEntity>> INDUCTION_DOCK =
      BLOCK_ENTITIES.register(
          CubitBlocks.INDUCTION_DOCK_ID,
          () ->
              BlockEntityType.Builder.of(
                      InductionDockBlockEntity::new, CubitBlocks.INDUCTION_DOCK.get())
                  .build(null));

  public static final Supplier<BlockEntityType<EngineeringDeskBlockEntity>> ENGINEERING_DESK =
      BLOCK_ENTITIES.register(
          CubitBlocks.ENGINEERING_DESK_ID,
          () ->
              BlockEntityType.Builder.of(
                      EngineeringDeskBlockEntity::new, CubitBlocks.ENGINEERING_DESK.get())
                  .build(null));

  public static final Supplier<BlockEntityType<NetworkServerBlockEntity>> NETWORK_SERVER =
      BLOCK_ENTITIES.register(
          CubitBlocks.NETWORK_SERVER_ID,
          () ->
              BlockEntityType.Builder.of(
                      NetworkServerBlockEntity::new, CubitBlocks.NETWORK_SERVER.get())
                  .build(null));

  public static final Supplier<BlockEntityType<SolarRelayBlockEntity>> SOLAR_RELAY =
      BLOCK_ENTITIES.register(
          CubitBlocks.SOLAR_RELAY_ID,
          () ->
              BlockEntityType.Builder.of(SolarRelayBlockEntity::new, CubitBlocks.SOLAR_RELAY.get())
                  .build(null));

  public static final Supplier<BlockEntityType<SignalAntennaBlockEntity>> SIGNAL_ANTENNA =
      BLOCK_ENTITIES.register(
          CubitBlocks.SIGNAL_ANTENNA_ID,
          () ->
              BlockEntityType.Builder.of(
                      SignalAntennaBlockEntity::new, CubitBlocks.SIGNAL_ANTENNA.get())
                  .build(null));

  private CubitBlockEntities() {}

  public static void register(IEventBus modBus) {
    BLOCK_ENTITIES.register(modBus);
    modBus.addListener(CubitBlockEntities::registerCapabilities);
  }

  private static void registerCapabilities(RegisterCapabilitiesEvent event) {
    // Registered by block, because the upper part of the dock has no block entity of its own.
    event.registerBlock(
        Capabilities.EnergyStorage.BLOCK,
        (level, pos, state, entity, side) -> {
          InductionDockBlockEntity dock = InductionDockBlock.dockAt(level, pos, state);
          return dock == null ? null : dock.energyStorage();
        },
        CubitBlocks.INDUCTION_DOCK.get());
    event.registerBlock(
        Capabilities.ItemHandler.BLOCK,
        (level, pos, state, entity, side) -> {
          InductionDockBlockEntity dock = InductionDockBlock.dockAt(level, pos, state);
          return dock == null ? null : dock.itemHandler();
        },
        CubitBlocks.INDUCTION_DOCK.get());
    event.registerBlock(
        Capabilities.FluidHandler.BLOCK,
        (level, pos, state, entity, side) -> {
          InductionDockBlockEntity dock = InductionDockBlock.dockAt(level, pos, state);
          return dock == null ? null : dock.xp().pipes();
        },
        CubitBlocks.INDUCTION_DOCK.get());
    DeskFaces.register(event, CubitBlocks.ENGINEERING_DESK.get());
    registerNetwork(event);
  }

  /** By any face and any half, and no energy ever leaves by a cable (answer 2256). */
  private static void registerNetwork(RegisterCapabilitiesEvent event) {
    Block[] blocks = {
      CubitBlocks.NETWORK_SERVER.get(),
      CubitBlocks.SOLAR_RELAY.get(),
      CubitBlocks.SIGNAL_ANTENNA.get()
    };
    event.registerBlock(
        Capabilities.EnergyStorage.BLOCK,
        (level, pos, state, entity, side) -> {
          NetworkBlockEntity network = NetworkBlock.networkAt(level, pos, state);
          return network == null ? null : network.energyStorage();
        },
        blocks);
    event.registerBlock(
        Capabilities.ItemHandler.BLOCK,
        (level, pos, state, entity, side) -> {
          NetworkBlockEntity network = NetworkBlock.networkAt(level, pos, state);
          return network == null ? null : network.itemHandler();
        },
        blocks);
  }
}
