package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.domain.network.ChunkCall;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.SignalSound;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * The Signal Antenna: the Network Server it was linked to, with the name it had then, and the
 * channel it takes there, also in the data of the world, so a Server far away knows its antennas
 * (Delivery 4, details 6 and 19). Its battery has no tier (answer 2199). Switched off, it neither
 * follows the signal nor spends, and lets its chunk go (answer 2548).
 */
public final class SignalAntennaBlockEntity extends NetworkBlockEntity {
  private static final String SERVER_KEY = "Server";
  private static final String SERVER_NAME_KEY = "ServerName";
  private static final String CHANNEL_KEY = "Channel";

  private final IItemHandler itemHandler =
      InputFirstItems.of(energySlot(), (EmptyItemHandler) EmptyItemHandler.INSTANCE);
  private final AntennaUplink uplink = new AntennaUplink();
  private final HeldChunk chunk = new HeldChunk(this::setChanged);
  private final AntennaSettings settings = new AntennaSettings();
  private final AntennaClientView view = new AntennaClientView();
  private final AntennaDocks docks = new AntennaDocks(this::setChanged);
  private SavedLink link = SavedLink.NONE;
  private long placedAt = Long.MIN_VALUE;

  public SignalAntennaBlockEntity(BlockPos pos, BlockState state) {
    super(
        CubitBlockEntities.SIGNAL_ANTENNA.get(),
        pos,
        state,
        NetworkParts.ANTENNA_ID,
        NetworkParts.antennaStart());
  }

  static void serverTick(
      Level level, BlockPos pos, BlockState state, SignalAntennaBlockEntity antenna) {
    antenna.battery().tick();
    ServerLevel server = (ServerLevel) level;
    if (antenna.settings.enabled()) {
      antenna.follow(server, pos);
    }
    antenna.work(server);
    antenna.battery().set(AntennaDockFeed.tick(server, antenna, antenna.battery().stock()));
  }

  /** The signal, with its sound, heard by everyone near (answers 2552, 2553 and 2561). */
  private void follow(ServerLevel level, BlockPos pos) {
    AntennaUplink.Change change = uplink.tick(level, pos, link);
    change.sound().ifPresent(sound -> ServerShow.play(level, pos, soundOf(sound)));
    if (change.changed()) {
      chunk.recheck();
      changeShown();
    }
  }

  private static SoundEvent soundOf(SignalSound sound) {
    return switch (sound) {
      case LOCK -> CubitSounds.ANTENNA_LOCK.get();
      case LOST -> CubitSounds.ANTENNA_LOST.get();
      case REGAINED -> CubitSounds.ANTENNA_REGAINED.get();
    };
  }

  /**
   * The chunk is paid first, and then what it spends for being on and for the distance, while it
   * works. Stopped or switched off, it lets the chunk go and spends nothing (answers 2435, 2439 and
   * 2553).
   */
  private void work(ServerLevel level) {
    boolean on = settings.enabled();
    boolean works = uplink.state().works();
    Energy upkeep =
        NetworkParts.antenna().spending(on, works, false, Energy.ZERO, uplink.distanceCost());
    ChunkCall.Reach reach = new ChunkCall.Reach(uplink.sharesChunk(worldPosition), works);
    HeldChunk.Asked asked =
        new HeldChunk.Asked(owner(), on && settings.chunkLoading(), upkeep, reach);
    EnergyStock left = chunk.tick(level, worldPosition, asked, battery().stock());
    EnergyStock.Spent spent = left.spend(upkeep);
    battery().set(spent.stock());
    if (!spent.paid().isZero()) {
      setChanged();
    }
  }

  /** What it spends now, with its chunk, for the screen (answers 2321 and 2435). */
  public Energy spending() {
    return NetworkParts.antenna()
        .spending(
            settings.enabled(),
            uplink.state().works(),
            chunk.state().holds(),
            chunk.cost(),
            uplink.distanceCost());
  }

  /** The blocks to its Server on the plane, for the screen (answer 2310). */
  public long distance() {
    return uplink.distance();
  }

  /** Whether it holds its chunk, and why not. */
  public ChunkState chunkState() {
    return chunk.state();
  }

  /** What its chunk costs now per tick, which its Server sends first (answer 2302). */
  Energy chunkCost() {
    return chunk.cost();
  }

  /** Only an antenna on, with signal, takes energy from its Server (Delivery 4, detail 16). */
  boolean receivesFromNetwork() {
    return !isRemoved() && settings.enabled() && uplink.state() == AntennaSignal.State.LINKED;
  }

  /** What it can still take in this tick, from the network and the cables together (2551). */
  Energy roomFromNetwork() {
    return battery().receive(inputPerTick(), true);
  }

  /** What it kept of a share of its Server, which loses exactly that. */
  Energy takeFromNetwork(Energy share) {
    return battery().receive(share, false);
  }

  public AntennaSignal.State signal() {
    return uplink.state();
  }

  /** The whole minutes left without signal before it stops (answer 2552). */
  public int minutesLeft() {
    return uplink.minutesLeft();
  }

  public boolean enabled() {
    return settings.enabled();
  }

  /** The general switch of the screen (answer 2548). */
  public void setEnabled(boolean on) {
    settings.setEnabled(on);
    chunk.recheck();
    changeShown();
  }

  /** The switch of the owner, as in the Server (answer 2213). */
  public boolean chunkLoading() {
    return settings.chunkLoading();
  }

  public void setChunkLoading(boolean on) {
    settings.setChunkLoading(on);
    chunk.recheck();
    setChanged();
  }

  /** Whether its owner sees the wall around its chunk (Delivery 4, detail 1). */
  public boolean showsWall() {
    return settings.showWall();
  }

  public void setShowWall(boolean shown) {
    settings.setShowWall(shown);
    changeShown();
  }

  public UpsReserve ups() {
    return settings.ups();
  }

  public void setUps(UpsReserve chosen) {
    settings.setUps(chosen);
    setChanged();
  }

  @Override
  public Energy capacity() {
    return NetworkParts.antenna().capacity();
  }

  @Override
  public Energy inputPerTick() {
    return NetworkParts.antenna().inputPerTick();
  }

  /** The Server it was linked to and its channel there. */
  public SavedLink link() {
    return link;
  }

  /** The arrows of the screen: the next free channel to one side. False when none is free. */
  public boolean stepChannel(int direction) {
    return AntennaPlacing.step(this, direction);
  }

  /** The channel it takes now, which the item keeps as the last one. */
  void take(AntennaChannel channel) {
    link = link.withChannel(channel);
    changeShown();
  }

  @Override
  void switchChunkOff() {
    setChunkLoading(false);
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
    return EnergySlotItems.insert(energySlot(), stack);
  }

  /**
   * Its channel goes back to its Server, and it is listed again when placed (answer 2547). Its mast
   * rises, and it seeks until it finds its Server (answer 2557).
   */
  @Override
  void placed(Player player) {
    AntennaPlacing.placed(this, player);
    uplink.placed();
    if (level != null) {
      placedAt = level.getGameTime();
      ServerShow.play(level, worldPosition, CubitSounds.ANTENNA_RAISE.get());
      changeShown();
    }
  }

  /** Whether it feeds the Dock at this place, of its own chunk (answer 2589). */
  public boolean feeds(BlockPos dock) {
    return docks.feeds(dock);
  }

  /** The Docks it feeds, in the order they came. */
  public List<BlockPos> fedDocks() {
    return docks.docks();
  }

  /** On and working, it gives its Docks what passes its reserve (detail 12 of Delivery 5). */
  boolean givesToDocks() {
    return settings.enabled() && uplink.state().works();
  }

  /** True when it changed. */
  public boolean feed(BlockPos dock) {
    return level != null && docks.feed(level, dock);
  }

  /** True when it changed. */
  public boolean unfeed(BlockPos dock) {
    return level != null && docks.unfeed(level, dock);
  }

  /** What the client shows of it. */
  public AntennaClientView view() {
    return view;
  }

  @Override
  public void onLoad() {
    super.onLoad();
    AntennaPlacing.listed(this);
  }

  @Override
  void gone() {
    if (level instanceof ServerLevel server) {
      chunk.release(server, worldPosition);
    }
    AntennaPlacing.unlisted(this);
  }

  @Override
  SavedNetwork kept() {
    return new SavedNetwork(
        RobotTier.BASE,
        energy().amount(),
        List.of(),
        settings.kept(),
        settings.chunkLoading(),
        settings.showWall(),
        link,
        docks.kept());
  }

  @Override
  void restore(SavedNetwork saved) {
    link = saved.link();
    settings.restore(saved);
    docks.restore(saved.docks(), worldPosition);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    link.server().ifPresent(server -> tag.putUUID(SERVER_KEY, server));
    link.serverName().ifPresent(name -> tag.putString(SERVER_NAME_KEY, name));
    link.channel().ifPresent(channel -> tag.putString(CHANNEL_KEY, channel.key()));
    uplink.save(tag);
    settings.save(tag);
    chunk.save(tag);
    docks.save(tag);
  }

  /** A broken channel is dropped, and the antenna takes the first free one again (rule 17). */
  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    link =
        new SavedLink(
            tag.hasUUID(SERVER_KEY) ? Optional.of(tag.getUUID(SERVER_KEY)) : Optional.empty(),
            tag.contains(SERVER_NAME_KEY)
                ? Optional.of(tag.getString(SERVER_NAME_KEY))
                : Optional.empty(),
            AntennaChannel.of(tag.getString(CHANNEL_KEY)));
    uplink.load(tag);
    settings.load(tag);
    chunk.load(tag);
    docks.load(tag);
    view.read(tag);
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    AntennaClientView.write(tag, this, placedAt, settings.showWall());
    return tag;
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    NetworkAnimations.register(controllers, this);
  }
}
