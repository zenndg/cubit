package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DeskLook;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import io.github.zenndg.cubit.domain.desk.DeskSpec;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.desk.DeskStock;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.ownership.Ownership;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.SavedEnergy;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * The state of an Engineering Desk: its owner, tier, energy, inventories and order. It lives in the
 * left half, and the rules live in the domain.
 */
public final class EngineeringDeskBlockEntity extends BlockEntity implements GeoBlockEntity {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String OWNER_KEY = "Owner";
  private static final String ENERGY_KEY = "Energy";
  private static final String GONE_RECIPE = "The desk at {} had the recipe {}, which is gone.";
  private static final int ENERGY_SLOT = 0;

  private final DeskTier tier = new DeskTier();
  private final DeskInventories items = new DeskInventories(this::setChanged);
  private final DeskEnergyStorage energyStorage = new DeskEnergyStorage(this);
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
  private final DeskShow show = new DeskShow();
  private final DeskClientView clientView = new DeskClientView();

  private UUID owner;
  private DeskStock stock = DeskStock.of(spec().capacity(), Energy.ZERO);
  private final DeskJob job = new DeskJob();
  private final CascadeState cascade = new CascadeState();
  private DeskStatus status = DeskStatus.IDLE;
  private Energy inputLeft = Energy.ZERO;
  private int signal;

  public EngineeringDeskBlockEntity(BlockPos pos, BlockState state) {
    super(CubitBlockEntities.ENGINEERING_DESK.get(), pos, state);
  }

  static void serverTick(
      Level level, BlockPos pos, BlockState state, EngineeringDeskBlockEntity desk) {
    desk.inputLeft = CubitMod.CONFIG.engineeringDesk().inputPerTick();
    desk.useEnergySlot();
    desk.show.beforeWork(desk, level.getGameTime());
    boolean redstone = EngineeringDeskBlock.hasSignal(level, pos, state);
    desk.status =
        desk.cascade.isRunning()
            ? CascadeWork.tick(desk, level, redstone)
            : DeskWork.tick(desk, level, redstone);
    CascadeWork.unload(desk, level);
    desk.showWork(level, pos, state);
    desk.show.afterWork(desk, level, pos, state, DeskLook.of(desk.status));
  }

  private void showWork(Level level, BlockPos pos, BlockState state) {
    int now = signal();
    if (now != signal) {
      signal = now;
      level.updateNeighbourForOutputSignal(pos, state.getBlock());
    }
  }

  /** Redstone becomes energy whole, from the slot or from below (answers 1425 and 1447). */
  private void useEnergySlot() {
    ItemStack stack = items.energySlot().getItem(ENERGY_SLOT);
    Energy itemEnergy = EnergySlotItems.energyOf(stack);
    if (stock.canTake(itemEnergy)) {
      stack.shrink(1);
      stock = stock.take(itemEnergy);
      setChanged();
    }
  }

  public DeskSpec spec() {
    return tier.spec();
  }

  public RobotTier tier() {
    return tier.tier();
  }

  /** The tier the client shows, read from the block state that both halves carry. */
  public RobotTier shownTier() {
    return getBlockState().getValue(EngineeringDeskBlock.TIER).tier();
  }

  public int slots() {
    return spec().storageSlots();
  }

  public Container input() {
    return items.input();
  }

  public Container output() {
    return items.output();
  }

  public Container energySlot() {
    return items.energySlot();
  }

  DeskEnergyStorage energyStorage() {
    return energyStorage;
  }

  public Energy energy() {
    return stock.charge();
  }

  public Energy capacity() {
    return stock.capacity();
  }

  public CascadeState cascade() {
    return cascade;
  }

  /** A plan started or ended, and the craft begins again from zero. */
  void cascadeChanged() {
    job.restart();
    tellClients();
  }

  public DeskOrder order() {
    return job.order();
  }

  public DeskStatus status() {
    return status;
  }

  /** What the desk shows, from the block state, the same on the server and on the clients. */
  public DeskLook look() {
    return getBlockState().getValue(EngineeringDeskBlock.LOOK).look();
  }

  public DeskClientView clientView() {
    return clientView;
  }

  public DeskShow show() {
    return show;
  }

  /** The item the hologram shows. Special recipes, as the repair, have none (answer 1509). */
  public ItemStack shownResult() {
    return level == null ? ItemStack.EMPTY : DeskRecipes.result(level, job.recipe().orElse(null));
  }

  public int progressPercent() {
    return job.progress().percent(spec().craftTicks());
  }

  public Optional<ResourceLocation> recipeId() {
    return job.recipe();
  }

  /** The chosen recipe, while it exists. One a pack removed ends the order (rule 17). */
  Optional<RecipeHolder<CraftingRecipe>> recipe() {
    if (job.recipe().isEmpty() || level == null) {
      return Optional.empty();
    }
    ResourceLocation chosen = job.recipe().get();
    Optional<RecipeHolder<CraftingRecipe>> found =
        DeskRecipes.find(level.getRecipeManager(), chosen);
    if (found.isEmpty()) {
      LOGGER.warn(GONE_RECIPE, worldPosition, chosen);
      choose(null);
    }
    return found;
  }

  boolean canPayCraft() {
    return stock.canPay(CubitMod.CONFIG.engineeringDesk().craftEnergy());
  }

  /** One tick of the craft. True when it is ready to end. */
  boolean advance() {
    setChanged();
    return job.advance(spec().craftTicks());
  }

  void crafted() {
    stock = stock.pay(CubitMod.CONFIG.engineeringDesk().craftEnergy());
    job.crafted();
    showCrafted();
  }

  void craftedInCascade() {
    stock = stock.pay(CubitMod.CONFIG.engineeringDesk().craftEnergy());
    job.restart();
    cascade.crafted();
    showCrafted();
  }

  private void showCrafted() {
    setChanged();
    if (level != null) {
      show.crafted(this, level, worldPosition, getBlockState());
    }
  }

  /**
   * Choosing a recipe only marks it, with an order of zero: the player asks the amount with plus
   * before anything is made, so nothing is crafted by accident (answer 1506).
   */
  public void choose(ResourceLocation chosen) {
    job.choose(chosen);
    tellClients();
  }

  /** The minus and plus buttons. Without an order, plus starts one for the chosen recipe. */
  public void adjustOrder(int delta) {
    if (job.adjust(delta, spec().maxOrder())) {
      tellClients();
    }
  }

  private void tellClients() {
    show.tellClients();
    setChanged();
  }

  public void cancelOrder() {
    job.cancel();
    tellClients();
  }

  void upgradeTo(RobotTier next) {
    tier.set(next);
    stock = stock.withCapacity(spec().capacity());
    job.limitTo(spec().maxOrder());
    tellClients();
    if (level != null) {
      EngineeringDeskBlock.showTier(level, worldPosition, getBlockState(), next);
    }
  }

  Energy receive(Energy offered, boolean simulate) {
    DeskStock.Received received = stock.receive(offered, inputLeft);
    if (!simulate) {
      stock = received.stock();
      inputLeft = inputLeft.minus(received.accepted());
      setChanged();
    }
    return received.accepted();
  }

  public Optional<UUID> owner() {
    return Optional.ofNullable(owner);
  }

  public boolean isOwner(Player player) {
    return Ownership.owns(owner(), player.getUUID());
  }

  void setOwner(Player player) {
    owner = player.getUUID();
    setChanged();
    if (level != null) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
  }

  public int signal() {
    return cascade.isRunning()
        ? cascade.run().orElseThrow().comparatorSignal()
        : job.order().comparatorSignal();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    if (owner != null) {
      tag.putUUID(OWNER_KEY, owner);
    }
    tag.putLong(ENERGY_KEY, energy().amount());
    tier.save(tag);
    items.save(tag, registries);
    job.save(tag, job.saved());
    DeskSaves.saveCascade(tag, cascade.saved(), registries);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    owner = tag.hasUUID(OWNER_KEY) ? tag.getUUID(OWNER_KEY) : null;
    tier.load(tag, worldPosition);
    stock = DeskStock.of(spec().capacity(), SavedEnergy.of(tag.getLong(ENERGY_KEY)));
    items.load(tag, registries);
    job.load(tag, worldPosition, spec().maxOrder());
    cascade.load(DeskSaves.loadCascade(tag, registries, worldPosition));
    if (level != null && level.isClientSide()) {
      clientView.receive(tag, level.getGameTime());
    }
  }

  @Override
  protected void applyImplicitComponents(DataComponentInput components) {
    super.applyImplicitComponents(components);
    tier.set(components.getOrDefault(CubitDataComponents.DESK_TIER.get(), RobotTier.BASE));
    long saved = components.getOrDefault(CubitDataComponents.DESK_ENERGY.get(), 0L);
    stock = DeskStock.of(spec().capacity(), SavedEnergy.of(saved));
    items.apply(
        components.get(CubitDataComponents.DESK_INPUT.get()),
        components.get(CubitDataComponents.DESK_OUTPUT.get()));
    job.use(
        Optional.ofNullable(components.get(CubitDataComponents.DESK_ORDER.get())),
        spec().maxOrder());
    cascade.load(Optional.ofNullable(components.get(CubitDataComponents.DESK_CASCADE.get())));
  }

  @Override
  protected void collectImplicitComponents(DataComponentMap.Builder components) {
    super.collectImplicitComponents(components);
    components.set(CubitDataComponents.DESK_TIER.get(), tier());
    components.set(CubitDataComponents.DESK_ENERGY.get(), energy().amount());
    items.collect(components);
    job.saved().ifPresent(saved -> components.set(CubitDataComponents.DESK_ORDER.get(), saved));
    cascade
        .saved()
        .ifPresent(saved -> components.set(CubitDataComponents.DESK_CASCADE.get(), saved));
  }

  /**
   * The owner decides who breaks the block, and the order, the progress and the people at the
   * screen give the hologram and the lid (answer 1508). The items and the energy stay on the
   * server.
   */
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = new CompoundTag();
    if (owner != null) {
      tag.putUUID(OWNER_KEY, owner);
    }
    tier.save(tag);
    job.save(tag, cascade.shownOrder().or(job::saved));
    show.write(tag);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    DeskAnimations.register(controllers, this);
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return animationCache;
  }
}
