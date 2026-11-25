package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.item.SavedEnergy;
import io.github.zenndg.cubit.item.SavedNetwork;
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
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * What every block of the Network has: an owner, an id with a name, a battery fed by cables and by
 * its slot, and the other blocks of its chunk. The rules live in the domain.
 */
public abstract class NetworkBlockEntity extends BlockEntity implements GeoBlockEntity, NamedBlock {
  private static final String ENERGY_KEY = "Energy";
  private static final String SLOT_KEY = "Slot";
  private static final int SLOT = 0;

  private final EnergySlot slot = new EnergySlot(this::setChanged);
  private final BlockOwner owner = new BlockOwner();
  private final BlockIdentity identity;
  private final BlockEnergy energy;
  private final IEnergyStorage energyStorage;
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

  NetworkBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      BlockIdentity.Kept kept,
      BlockEnergy.Start start) {
    super(type, pos, state);
    identity = new BlockIdentity(kept, this::setChanged);
    energy = new BlockEnergy(start, this::inputPerTick, slot, this::setChanged);
    energyStorage = new InputOnlyEnergy(energy);
  }

  /** The battery, at the tier of the block. */
  public abstract Energy capacity();

  /** What cables and redstone bring at most per tick. */
  public abstract Energy inputPerTick();

  /** What the automation sees, by any face. */
  public abstract IItemHandler itemHandler();

  abstract void openScreen(Player player);

  /** What the owner does with an item in the hand. False when it does nothing. */
  abstract boolean use(Player player, ItemStack stack);

  /** The block was taken out of the world, and not only unloaded. */
  abstract void gone();

  /** What the item keeps when the block breaks. */
  abstract SavedNetwork kept();

  /** What a placed item gives back, before the energy. */
  abstract void restore(SavedNetwork saved);

  BlockEnergy battery() {
    return energy;
  }

  /** The owner names it with a name tag, as the Dock. */
  boolean click(Player player, ItemStack stack) {
    return identity.renameByTag(player, stack) || use(player, stack);
  }

  public Energy energy() {
    return energy.energy();
  }

  /** What entered by cables and redstone, per tick, on average over the last second. */
  public Energy inputShown() {
    return energy.inputShown();
  }

  public IEnergyStorage energyStorage() {
    return energyStorage;
  }

  public Container slot() {
    return slot;
  }

  final EnergySlot energySlot() {
    return slot;
  }

  @Override
  public BlockIdentity identity() {
    return identity;
  }

  public Optional<UUID> owner() {
    return owner.get();
  }

  public Optional<String> ownerName() {
    return owner.name(level, worldPosition);
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
    changeShown();
    if (level != null && !level.isClientSide()) {
      NetworkChunk.pair(level, worldPosition);
    }
  }

  /** Saved, and told to the clients. */
  void changeShown() {
    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  /** Every block of the chunk is there when the game loads this one (Delivery 3, detail 2). */
  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      NetworkChunk.pair(level, worldPosition);
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    owner.save(tag);
    identity.save(tag);
    tag.putLong(ENERGY_KEY, energy().amount());
    ItemStack stack = slot.getItem(SLOT);
    if (!stack.isEmpty()) {
      tag.put(SLOT_KEY, stack.save(registries));
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    owner.load(tag, level, worldPosition);
    identity.load(tag);
    energy.set(EnergyStock.of(capacity(), SavedEnergy.of(tag.getLong(ENERGY_KEY))));
    slot.setItem(SLOT, DockShapes.savedStack(tag, SLOT_KEY, registries));
  }

  @Override
  protected void applyImplicitComponents(DataComponentInput input) {
    super.applyImplicitComponents(input);
    SavedNetwork saved =
        input.getOrDefault(CubitDataComponents.NETWORK_STATE.get(), SavedNetwork.EMPTY);
    restore(saved);
    energy.set(EnergyStock.of(capacity(), SavedEnergy.of(saved.energy())));
    identity.apply(
        input.get(CubitDataComponents.NETWORK_ID.get()), input.get(DataComponents.CUSTOM_NAME));
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    components.set(CubitDataComponents.NETWORK_STATE.get(), kept());
    identity.collect(components);
  }

  /** The owner, who decides who can break the block, travels to the client. */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    owner.save(tag);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return animationCache;
  }
}
