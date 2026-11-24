package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockInputMeter;
import io.github.zenndg.cubit.domain.dock.DockStatus;
import io.github.zenndg.cubit.domain.dock.DockStock;
import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.PowerBankItem;
import io.github.zenndg.cubit.item.SavedDockXp;
import io.github.zenndg.cubit.item.SavedEnergy;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * The state of a dock: its owner, its energy, its input slot and its storage. The rules live in the
 * domain.
 */
public final class InductionDockBlockEntity extends BlockEntity implements GeoBlockEntity {
  private static final String ENERGY_KEY = "Energy";
  private static final String SLOT_KEY = "Slot";
  private static final int SLOT = 0;
  private static final int SLOT_SIZE = 1;
  private static final int ONE_ITEM = 1;

  private final SimpleContainer slot =
      new SimpleContainer(SLOT_SIZE) {
        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
          return EnergySlotItems.accepts(stack);
        }

        @Override
        public void setChanged() {
          InductionDockBlockEntity.this.setChanged();
        }
      };

  private final DockTier tier = new DockTier();
  private final DockSolarPanel panel = new DockSolarPanel();
  private final DockIdentity identity = new DockIdentity(this::setChanged);
  private final DockXp xp = new DockXp(this::setChanged, () -> tier.spec().xp(), this::isOwner);

  /** The Toolbox and the Power Banks, which the stock charges (answers 947 to 959). */
  private final DockStorage storage =
      new DockStorage(this::setChanged, () -> tier.spec().storageSlots());

  private final IEnergyStorage energyStorage = new DockEnergyStorage(this);

  private final IItemHandler itemHandler = DockItemHandler.of(slot, storage.handler());

  private final DockOwner owner = new DockOwner();
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
  private final DockPresence presence = new DockPresence();
  private DockStock stock = emptyStock();
  private Energy inputLeft = inputPerTick();
  private final DockInputMeter inputMeter = new DockInputMeter();
  private Energy entered = Energy.ZERO;

  public InductionDockBlockEntity(BlockPos pos, BlockState state) {
    super(CubitBlockEntities.INDUCTION_DOCK.get(), pos, state);
  }

  private static DockStock emptyStock() {
    return DockStock.of(CubitMod.CONFIG.dockCatalog().dock(RobotTier.BASE).capacity(), Energy.ZERO);
  }

  public Energy capacity() {
    return tier.spec().capacity();
  }

  public RobotTier tier() {
    return tier.tier();
  }

  /** The recharge of the Cubit and of the stored Power Bank: the one of the dock (1375, 1376). */
  public Energy rechargePerTick() {
    return tier.spec().rechargePerTick();
  }

  public DockSolarReading solar() {
    return panel.reading(tier.spec().solar());
  }

  /** Its own id and the name the owner gave it (answers 1867 to 1870). */
  public DockIdentity identity() {
    return identity;
  }

  public DockXp xp() {
    return xp;
  }

  public int storageSlots() {
    return tier.spec().storageSlots();
  }

  /** The stock keeps its energy and grows to the new tier, and both halves show it (1373). */
  void upgradeTo(RobotTier next) {
    tier.set(next);
    stock = DockStock.of(capacity(), energy());
    setChanged();
    if (level != null) {
      DockTierStates.show(level, worldPosition, next);
    }
  }

  private static Energy inputPerTick() {
    return new Energy(CubitMod.CONFIG.dock().inputPerTick());
  }

  static void serverTick(
      Level level, BlockPos pos, BlockState state, InductionDockBlockEntity dock) {
    dock.inputLeft = inputPerTick();
    dock.inputMeter.record(dock.entered);
    dock.entered = Energy.ZERO;
    dock.useSlot();
    dock.storage.dropRefused(level, pos);
    boolean chargesBanks =
        DockStorageRules.chargesBanks(dock.isOccupied(), dock.presence.robotFull());
    dock.stock =
        dock.storage.charge(dock.stock, chargesBanks, dock.rechargePerTick(), dock::setChanged);
    SolarOutput rates = dock.tier.spec().solar();
    dock.stock = dock.panel.generate(level, pos, dock.stock, rates, dock::setChanged);
    dock.stock = dock.xp.convert(dock.stock);
    if (dock.panel.switched()) {
      level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
    DockStatusProperty.show(level, pos, state, dock.status());
  }

  /**
   * Redstone becomes stock whole. A Power Bank empties into the stock like the cables, within the
   * same input per tick, and stays in the slot when empty (answer 920).
   */
  private void useSlot() {
    ItemStack stack = slot.getItem(SLOT);
    if (PowerBankItem.is(stack)) {
      DockPowerBanks.discharge(stack, offered -> receive(offered, false));
      return;
    }
    Energy itemEnergy = EnergySlotItems.energyOf(stack);
    if (stock.canTake(itemEnergy)) {
      stack.shrink(ONE_ITEM);
      stock = stock.take(itemEnergy);
      entered = entered.plus(itemEnergy);
      setChanged();
    }
  }

  public DockStatus status() {
    return DockStatus.decide(
        stock.energy().isEmpty(), isOccupied(), isOccupied() && presence.robotFull());
  }

  /** What entered by cables and redstone, per tick, on average over the last second. */
  public Energy inputShown() {
    return inputMeter.perTick();
  }

  public int waitingCount() {
    return presence.waitingCount(level);
  }

  /** The robot in the base, read through its capability, while it is loaded. */
  public Optional<DockedRobot> dockedRobot() {
    if (!(level instanceof ServerLevel server)) {
      return Optional.empty();
    }
    return occupant()
        .map(server::getEntity)
        .map(robot -> robot.getCapability(DockCapabilities.DOCKED_ROBOT));
  }

  public Optional<String> ownerName() {
    return owner.name(level, worldPosition);
  }

  /** The sneaking click on the dock and the button on its screen call the same way. */
  public void callNearestRobot() {
    if (level instanceof ServerLevel server) {
      owner
          .get()
          .flatMap(id -> DockFinder.nearestRobot(server, worldPosition, id))
          .ifPresent(robot -> robot.callToDock(worldPosition));
    }
  }

  public Optional<UUID> owner() {
    return owner.get();
  }

  public boolean isOwner(Player player) {
    return Ownership.owns(owner(), player.getUUID());
  }

  void setOwner(Player player) {
    owner.set(player.getUUID());
    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  public void reportRobot(UUID robot, boolean full) {
    presence.reportRobot(level, robot, full);
  }

  public boolean isOccupied() {
    return presence.isOccupied(level);
  }

  Optional<UUID> occupant() {
    return presence.occupant(level);
  }

  public void reportWaiting(UUID robot) {
    presence.reportWaiting(level, robot);
  }

  public boolean isNextInLine(UUID robot) {
    return presence.isNextInLine(level, robot);
  }

  public Battery charge(Battery robot, Energy rate) {
    DockStock.Transfer transfer = stock.charge(robot, rate);
    stock = transfer.stock();
    setChanged();
    return transfer.robot();
  }

  /** The XP of the Cubit in the dock, which always takes all of it (answer 1831). */
  public void receiveXp(int points) {
    DockXp.Taken taken = xp.take(stock, points);
    stock = taken.stock();
    if (taken.tells() && DockXpTelling.tell(level, worldPosition, owner(), identity.name())) {
      xp.told();
    }
  }

  public Vec3 robotSpot() {
    return DockShapes.robotSpot(getBlockState().getValue(InductionDockBlock.FACING), worldPosition);
  }

  public Energy energy() {
    return stock.energy().charge();
  }

  public Container storage() {
    return storage.container();
  }

  public Container slot() {
    return slot;
  }

  public IEnergyStorage energyStorage() {
    return energyStorage;
  }

  public IItemHandler itemHandler() {
    return itemHandler;
  }

  public int energyLevel(int steps) {
    return stock.energy().level(steps);
  }

  boolean insert(ItemStack stack) {
    return storage.insertByClick(stack, slot);
  }

  Energy receive(Energy offered, boolean simulate) {
    DockStock.Received received = stock.receive(offered, inputLeft);
    if (!simulate) {
      stock = received.stock();
      inputLeft = inputLeft.minus(received.accepted());
      entered = entered.plus(received.accepted());
      setChanged();
    }
    return received.accepted();
  }

  long capacityAmount() {
    return stock.energy().capacity().amount();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    owner.save(tag);
    tag.putLong(ENERGY_KEY, energy().amount());
    tier.save(tag);
    ItemStack stack = slot.getItem(SLOT);
    if (!stack.isEmpty()) {
      tag.put(SLOT_KEY, stack.save(registries));
    }
    storage.save(tag, registries);
    panel.save(tag);
    identity.save(tag);
    xp.save(tag);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    owner.load(tag, level, worldPosition);
    tier.load(tag, worldPosition);
    panel.load(tag);
    panel.readSun(tag);
    identity.load(tag);
    xp.load(tag);
    stock = DockStock.of(capacity(), SavedEnergy.of(tag.getLong(ENERGY_KEY)));
    slot.setItem(SLOT, DockShapes.savedStack(tag, SLOT_KEY, registries));
    storage.load(tag, registries, worldPosition);
  }

  @Override
  protected void applyImplicitComponents(DataComponentInput input) {
    super.applyImplicitComponents(input);
    tier.set(input.getOrDefault(CubitDataComponents.DOCK_TIER.get(), RobotTier.BASE));
    long saved = input.getOrDefault(CubitDataComponents.DOCK_ENERGY.get(), 0L);
    stock = DockStock.of(capacity(), SavedEnergy.of(saved));
    storage.apply(
        input.getOrDefault(CubitDataComponents.DOCK_STORAGE.get(), ItemContainerContents.EMPTY),
        worldPosition);
    xp.apply(input.getOrDefault(CubitDataComponents.DOCK_XP.get(), SavedDockXp.EMPTY));
    identity.apply(
        input.get(CubitDataComponents.DOCK_ID.get()), input.get(DataComponents.CUSTOM_NAME));
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    components.set(CubitDataComponents.DOCK_ENERGY.get(), energy().amount());
    tier.collect(components);
    components.set(CubitDataComponents.DOCK_STORAGE.get(), storage.contents());
    identity.collect(components);
    xp.collect(components);
  }

  /** The owner, who decides who can break the block, and the panel travel to the client. */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    owner.save(tag);
    panel.writeSun(tag);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    DockAnimations.register(controllers, this);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return animationCache;
  }
}
