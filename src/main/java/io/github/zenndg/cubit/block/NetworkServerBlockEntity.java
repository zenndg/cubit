package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.ModuleItem;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * The center of a network: its tier, its battery with a reserve for the chunk, its storage and the
 * Solar Relay of its chunk, which brings the sun.
 */
public final class NetworkServerBlockEntity extends NetworkBlockEntity {
  private static final String UPS_KEY = "Ups";
  private static final String CHUNK_KEY = "ChunkLoading";

  private final BlockTier<ServerSpec> tier = NetworkParts.serverTier();
  private final ServerStorage storage = new ServerStorage(this::setChanged, this::storageSlots);
  private final IItemHandler itemHandler = InputFirstItems.of(energySlot(), storage.handler());
  private UpsReserve ups = CubitMod.CONFIG.network().chunks().serverUps();
  private boolean chunkLoading = true;
  private boolean first;
  private SolarRelayBlockEntity relay;

  public NetworkServerBlockEntity(BlockPos pos, BlockState state) {
    super(
        CubitBlockEntities.NETWORK_SERVER.get(),
        pos,
        state,
        NetworkParts.SERVER_ID,
        NetworkParts.serverStart());
  }

  static void serverTick(
      Level level, BlockPos pos, BlockState state, NetworkServerBlockEntity server) {
    BlockEnergy battery = server.battery();
    battery.tick();
    server.storage.dropRefused(level, pos);
    Energy rate = server.bankRate().min(server.ups.spare(server.energy(), server.capacity()));
    battery.set(server.storage.charge(battery.stock(), rate, server::setChanged));
  }

  /** At the speed of the Dock of its tier, and never out of the reserve (answer 2373). */
  private Energy bankRate() {
    return CubitMod.CONFIG.dockCatalog().dock(tier()).rechargePerTick();
  }

  @Override
  public Energy capacity() {
    return tier.spec().capacity();
  }

  @Override
  public Energy inputPerTick() {
    return tier.spec().inputPerTick();
  }

  public RobotTier tier() {
    return tier.tier();
  }

  public ServerSpec spec() {
    return tier.spec();
  }

  public int storageSlots() {
    return tier.spec().storageSlots();
  }

  public Container storage() {
    return storage.container();
  }

  public UpsReserve ups() {
    return ups;
  }

  /** The owner chose a reserve in the screen. */
  public void setUps(UpsReserve chosen) {
    ups = chosen;
    setChanged();
  }

  public boolean chunkLoading() {
    return chunkLoading;
  }

  public void setChunkLoading(boolean on) {
    chunkLoading = on;
    setChanged();
  }

  /** Whether it is the one that counts in its chunk (Delivery 3, detail 1). */
  public boolean isFirst() {
    return first;
  }

  public Optional<SolarRelayBlockEntity> relay() {
    return Optional.ofNullable(relay);
  }

  void pairWith(Optional<SolarRelayBlockEntity> next, boolean firstOfChunk) {
    first = firstOfChunk;
    if (next.orElse(null) != relay) {
      relay = next.orElse(null);
      changeShown();
    }
  }

  /** The rates of its Solar Relay, at its tier (answer 2220). */
  SolarOutput sun() {
    return tier.spec().relay();
  }

  /** The sun goes straight to the battery, apart from the input of the cables (detail 18). */
  Energy takeSun(Energy offered) {
    EnergyStock.Received received = battery().stock().receive(offered, offered);
    battery().set(received.stock());
    if (!received.accepted().isZero()) {
      setChanged();
    }
    return received.accepted();
  }

  /** The battery keeps its energy and grows to the new tier, and both halves show it. */
  void upgradeTo(RobotTier next) {
    tier.set(next);
    battery().set(EnergyStock.of(capacity(), energy()));
    changeShown();
    if (level != null) {
      NetworkBlock.showTier(level, worldPosition, next);
      NetworkChunk.pair(level, worldPosition);
    }
  }

  @Override
  public IItemHandler itemHandler() {
    return itemHandler;
  }

  @Override
  boolean use(Player player, ItemStack stack) {
    if (stack.getItem() instanceof ModuleItem) {
      NetworkUpgrade.apply(this, player, stack);
      return true;
    }
    return storage.insertByClick(stack, energySlot());
  }

  @Override
  SavedNetwork kept() {
    return new SavedNetwork(
        tier(), energy().amount(), storage.contents(), Optional.of(ups), chunkLoading);
  }

  @Override
  void restore(SavedNetwork saved) {
    tier.set(saved.tier());
    storage.restore(saved.items(), worldPosition);
    ups = saved.ups().orElseGet(() -> CubitMod.CONFIG.network().chunks().serverUps());
    chunkLoading = saved.chunkLoading();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tier.save(tag);
    storage.save(tag, registries);
    tag.putInt(UPS_KEY, ups.percent());
    tag.putBoolean(CHUNK_KEY, chunkLoading);
  }

  /** The tier first, because it says how much the battery and the storage keep. */
  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    tier.load(tag, worldPosition);
    super.loadAdditional(tag, registries);
    storage.load(tag, registries, worldPosition);
    ups = tag.contains(UPS_KEY) ? UpsReserve.of(tag.getInt(UPS_KEY)) : ups;
    chunkLoading = !tag.contains(CHUNK_KEY) || tag.getBoolean(CHUNK_KEY);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    // The animations come with the renderer.
  }
}
