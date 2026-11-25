package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockSpec;
import io.github.zenndg.cubit.domain.dock.DockStatus;
import io.github.zenndg.cubit.domain.dock.DockStorageRules;
import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.SavedDockXp;
import io.github.zenndg.cubit.item.SavedEnergy;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
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
public final class InductionDockBlockEntity extends BlockEntity
    implements GeoBlockEntity, NamedBlock {
  private static final String ENERGY_KEY = "Energy";
  private static final String SLOT_KEY = "Slot";
  private static final int SLOT = 0;

  private final EnergySlot slot = new EnergySlot(this::setChanged);
  private final BlockTier<DockSpec> tier = DockParts.tier();
  private final DockSolarPanel panel = new DockSolarPanel();
  private final BlockIdentity identity = DockParts.identity(this::setChanged);
  private final DockXp xp = new DockXp(this::setChanged, () -> tier.spec().xp(), this::isOwner);

  /** The Toolbox and the Power Banks, which the stock charges (answers 947 to 959). */
  private final DockStorage storage =
      new DockStorage(this::setChanged, () -> tier.spec().storageSlots());

  private final BlockEnergy energy =
      new BlockEnergy(start(), InductionDockBlockEntity::inputPerTick, slot, this::setChanged);

  private final IEnergyStorage energyStorage = new InputOnlyEnergy(energy);

  private final IItemHandler itemHandler = InputFirstItems.of(slot, storage.handler());

  private final BlockOwner owner = new BlockOwner();
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
  private final DockPresence presence = new DockPresence();

  public InductionDockBlockEntity(BlockPos pos, BlockState state) {
    super(CubitBlockEntities.INDUCTION_DOCK.get(), pos, state);
  }

  private static BlockEnergy.Start start() {
    Energy capacity = CubitMod.CONFIG.dockCatalog().dock(RobotTier.BASE).capacity();
    return new BlockEnergy.Start(capacity, inputPerTick());
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

  public SolarReading solar() {
    return panel.reading(tier.spec().solar());
  }

  /** Its own id and the name the owner gave it (answers 1867 to 1870). */
  @Override
  public BlockIdentity identity() {
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
    energy.set(EnergyStock.of(capacity(), energy()));
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
    dock.energy.tick();
    dock.storage.dropRefused(level, pos);
    boolean chargesBanks =
        DockStorageRules.chargesBanks(dock.isOccupied(), dock.presence.robotFull());
    EnergyStock stock =
        dock.storage.charge(
            dock.energy.stock(), chargesBanks, dock.rechargePerTick(), dock::setChanged);
    SolarOutput rates = dock.tier.spec().solar();
    stock = dock.panel.generate(level, pos, stock, rates, dock::setChanged);
    dock.energy.set(dock.xp.convert(stock));
    if (dock.panel.switched()) {
      level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
    DockStatusProperty.show(level, pos, state, dock.status());
  }

  public DockStatus status() {
    return DockStatus.decide(
        energy.stock().energy().isEmpty(), isOccupied(), isOccupied() && presence.robotFull());
  }

  /** What entered by cables and redstone, per tick, on average over the last second. */
  public Energy inputShown() {
    return energy.inputShown();
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

  @Override
  public DataComponentType<UUID> idComponent() {
    return identity.component();
  }

  @Override
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
    EnergyStock.Transfer transfer = energy.stock().charge(robot, rate);
    energy.set(transfer.stock());
    setChanged();
    return transfer.robot();
  }

  /** The XP of the Cubit in the dock, which always takes all of it (answer 1831). */
  public void receiveXp(int points) {
    DockXp.Taken taken = xp.take(energy.stock(), points);
    energy.set(taken.stock());
    if (taken.tells() && DockXpTelling.tell(level, worldPosition, owner(), identity.name())) {
      xp.told();
    }
  }

  public Vec3 robotSpot() {
    return DockShapes.robotSpot(getBlockState().getValue(InductionDockBlock.FACING), worldPosition);
  }

  public Energy energy() {
    return energy.energy();
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
    return energy.stock().energy().level(steps);
  }

  boolean insert(ItemStack stack) {
    return storage.insertByClick(stack, slot);
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
    energy.set(EnergyStock.of(capacity(), SavedEnergy.of(tag.getLong(ENERGY_KEY))));
    slot.setItem(SLOT, DockShapes.savedStack(tag, SLOT_KEY, registries));
    storage.load(tag, registries, worldPosition);
  }

  @Override
  protected void applyImplicitComponents(DataComponentInput input) {
    super.applyImplicitComponents(input);
    tier.set(input.getOrDefault(CubitDataComponents.DOCK_TIER.get(), RobotTier.BASE));
    long saved = input.getOrDefault(CubitDataComponents.DOCK_ENERGY.get(), 0L);
    energy.set(EnergyStock.of(capacity(), SavedEnergy.of(saved)));
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
    components.set(CubitDataComponents.DOCK_TIER.get(), tier());
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
