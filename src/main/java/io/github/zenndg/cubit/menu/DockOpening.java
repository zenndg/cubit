package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.config.CubitConfig;
import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;

/** The values of a dock that travel once, when its screen opens. */
record DockOpening(
    String ownerName,
    long inputLimit,
    int callRadius,
    int storageSlots,
    int tier,
    UUID dockId,
    String dockName) {
  private static final String NO_NAME = "";
  private static final UUID NO_ID = new UUID(0L, 0L);

  /** Only the client reads it, so the menu on the server keeps nothing. */
  static final DockOpening NONE =
      new DockOpening(NO_NAME, 0, 0, DockStorageRules.MAX_SLOTS, 0, NO_ID, NO_NAME);

  /** The button names the radius of the tier Base, the only one in this phase (answer 693). */
  static DockOpening of(InductionDockBlockEntity dock) {
    return new DockOpening(
        dock.ownerName().orElse(NO_NAME),
        CubitMod.CONFIG.dock().inputPerTick(),
        CubitMod.CONFIG
            .tierCatalog()
            .spec(CubitConfig.GENERAL_ROBOT, RobotTier.BASE)
            .callRadiusBlocks(),
        dock.storageSlots(),
        dock.tier().ordinal(),
        dock.identity().id(),
        dock.identity().name().value());
  }

  static DockOpening read(RegistryFriendlyByteBuf data) {
    return new DockOpening(
        data.readUtf(),
        data.readVarLong(),
        data.readVarInt(),
        data.readVarInt(),
        data.readVarInt(),
        data.readUUID(),
        data.readUtf());
  }

  void write(RegistryFriendlyByteBuf data) {
    data.writeUtf(ownerName);
    data.writeVarLong(inputLimit);
    data.writeVarInt(callRadius);
    data.writeVarInt(storageSlots);
    data.writeVarInt(tier);
    data.writeUUID(dockId);
    data.writeUtf(dockName);
  }
}
