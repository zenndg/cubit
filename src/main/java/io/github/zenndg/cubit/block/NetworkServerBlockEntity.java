package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.NetworkRoster;
import io.github.zenndg.cubit.domain.network.ServerSpec;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.ModuleItem;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.item.SignalAntennaItem;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
  private static final String WALL_KEY = "ShowWall";

  private final BlockTier<ServerSpec> tier = NetworkParts.serverTier();
  private final ServerStorage storage = new ServerStorage(this::setChanged, this::storageSlots);
  private final IItemHandler itemHandler = InputFirstItems.of(energySlot(), storage.handler());
  private final ServerShow show = new ServerShow();
  private UpsReserve ups = CubitMod.CONFIG.network().chunks().serverUps();
  private final HeldChunk chunk = new HeldChunk(this::setChanged);
  private final AntennaFeed antennas = new AntennaFeed();
  private boolean chunkLoading = true;
  private boolean showWall;
  private Energy bankGiven = Energy.ZERO;
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
    server.feedAntennas((ServerLevel) level);
    server.chargeBanks();
    server.payUpkeep((ServerLevel) level);
    server.show.passes(battery.enteredNow());
    if (server.show.tick(level, pos, server.reading())) {
      server.changeShown();
    }
  }

  private ServerShow.Reading reading() {
    Energy upkeep = chunkCost().plus(standbyCost()).plus(relayCost());
    return new ServerShow.Reading(energy(), ups, spec(), storage.used(), relay != null, upkeep);
  }

  /**
   * Through its relay, up to what it sends per tick and only above its reserve, before the Power
   * Banks (answers 2276, 2550 and 2571).
   */
  private void feedAntennas(ServerLevel level) {
    Energy offered =
        relay == null ? Energy.ZERO : spec().outputPerTick().min(ups.spare(energy(), capacity()));
    boolean reaches = relay != null && !energy().isZero();
    battery().set(antennas.tick(level, identity().id(), battery().stock(), offered, reaches));
    Energy sent = antennas.sent();
    show.passes(sent);
    if (!sent.isZero()) {
      setChanged();
    }
    if (relay != null) {
      relay.sends(!sent.isZero());
    }
  }

  /** What its antennas kept in the last tick, for the screen (answer 2545). */
  public Energy antennasSent() {
    return antennas.sent();
  }

  /** How many of its antennas took energy in the last tick. */
  public int antennasReceiving() {
    return antennas.receiving();
  }

  /** How many antennas it has, wherever they are. */
  public int antennasListed() {
    return antennas.listed();
  }

  /** One at a time, with what passes the reserve (answer 2373). */
  private void chargeBanks() {
    Energy held = energy();
    Energy rate = bankRate().min(ups.spare(held, capacity()));
    battery().set(storage.charge(battery().stock(), rate, this::setChanged));
    bankGiven = held.minus(energy());
    show.passes(bankGiven);
  }

  /** The chunk is paid first, and then what the server spends for being on (answer 2439). */
  private void payUpkeep(ServerLevel level) {
    Energy standby = spec().standby().total(relay != null);
    HeldChunk.Asked asked = new HeldChunk.Asked(owner(), chunkLoading && first, standby);
    EnergyStock left = chunk.tick(level, worldPosition, asked, battery().stock());
    EnergyStock.Spent spent = left.spend(standby);
    battery().set(spent.stock());
    if (!spent.paid().isZero()) {
      setChanged();
    }
  }

  /** What it spends for being on now, and nothing without energy (answers 2435 and 2439). */
  public Energy standbyCost() {
    return energy().isZero() ? Energy.ZERO : spec().standby().server();
  }

  /** What it pays for its relay now (answer 2436). */
  public Energy relayCost() {
    return energy().isZero() ? Energy.ZERO : spec().standby().relay(relay != null);
  }

  /** At the speed of the Dock of its tier (answer 2373). */
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

  public ServerSlots storage() {
    return storage.container();
  }

  /** What the Power Banks took in the last tick, for the screen. */
  public Energy bankGiven() {
    return bankGiven;
  }

  /** What the client shows of it. */
  public ServerClientView view() {
    return show.view();
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

  /** The switch of the owner (answer 2213). */
  public void setChunkLoading(boolean on) {
    chunkLoading = on;
    chunk.recheck();
    setChanged();
  }

  /** Whether its owner sees the wall around its chunk (answer 2448). */
  public boolean showsWall() {
    return showWall;
  }

  public void setShowWall(boolean shown) {
    showWall = shown;
    changeShown();
  }

  /** Whether it holds its chunk, and why not. */
  public ChunkState chunkState() {
    return chunk.state();
  }

  /** What the chunk costs now per tick. */
  public Energy chunkCost() {
    return chunk.cost();
  }

  /** Whether it is the one that counts in its chunk (Delivery 3, detail 1). */
  public boolean isFirst() {
    return first;
  }

  public Optional<SolarRelayBlockEntity> relay() {
    return Optional.ofNullable(relay);
  }

  void pairWith(Optional<SolarRelayBlockEntity> next, boolean firstOfChunk) {
    listed();
    if (first != firstOfChunk) {
      first = firstOfChunk;
      chunk.recheck();
    }
    if (next.orElse(null) != relay) {
      relay = next.orElse(null);
      changeShown();
    }
  }

  /**
   * Its place and its tier in the data of the world, for its antennas far away. It follows each
   * time the chunk is paired: when placed, loaded or upgraded.
   */
  private void listed() {
    if (!(level instanceof ServerLevel server) || owner().isEmpty()) {
      return;
    }
    NetworkData data = NetworkData.of(server.getServer());
    GlobalPos place = GlobalPos.of(level.dimension(), worldPosition);
    if (data.roster()
        .putServer(new NetworkRoster.Server<>(identity().id(), owner().get(), place, tier()))) {
      data.setDirty();
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
    show.passes(received.accepted());
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
      ServerShow.play(level, worldPosition, CubitSounds.SERVER_UPGRADE.get());
      NetworkBlock.showTier(level, worldPosition, next);
      NetworkChunk.pair(level, worldPosition);
    }
  }

  @Override
  public IItemHandler itemHandler() {
    return itemHandler;
  }

  @Override
  void openScreen(Player player) {
    NetworkMenus.open(this, player);
  }

  @Override
  boolean use(Player player, ItemStack stack) {
    if (stack.getItem() instanceof ModuleItem) {
      NetworkUpgrade.apply(this, player, stack);
      return true;
    }
    if (stack.getItem() instanceof SignalAntennaItem) {
      AntennaLinking.link(this, player, stack);
      return true;
    }
    return storage.insertByClick(stack, energySlot());
  }

  @Override
  void gone() {
    if (level instanceof ServerLevel server) {
      chunk.release(server, worldPosition);
      NetworkData data = NetworkData.of(server.getServer());
      if (data.roster().removeServer(identity().id())) {
        data.setDirty();
      }
    }
  }

  @Override
  SavedNetwork kept() {
    return new SavedNetwork(
        tier(),
        energy().amount(),
        storage.contents(),
        Optional.of(ups),
        chunkLoading,
        showWall,
        SavedLink.NONE);
  }

  @Override
  void restore(SavedNetwork saved) {
    tier.set(saved.tier());
    storage.restore(saved.items(), worldPosition);
    ups = saved.ups().orElseGet(() -> CubitMod.CONFIG.network().chunks().serverUps());
    chunkLoading = saved.chunkLoading();
    showWall = saved.showWall();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tier.save(tag);
    storage.save(tag, registries);
    tag.putInt(UPS_KEY, ups.percent());
    tag.putBoolean(CHUNK_KEY, chunkLoading);
    tag.putBoolean(WALL_KEY, showWall);
    chunk.save(tag);
  }

  /** The tier first, because it says how much the battery and the storage keep. */
  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    tier.load(tag, worldPosition);
    super.loadAdditional(tag, registries);
    storage.load(tag, registries, worldPosition);
    ups = tag.contains(UPS_KEY) ? UpsReserve.of(tag.getInt(UPS_KEY)) : ups;
    chunkLoading = !tag.contains(CHUNK_KEY) || tag.getBoolean(CHUNK_KEY);
    showWall = tag.getBoolean(WALL_KEY);
    chunk.load(tag);
    show.read(tag);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    show.write(tag);
    tag.putBoolean(WALL_KEY, showWall);
    return tag;
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    NetworkAnimations.register(controllers, this);
  }
}
