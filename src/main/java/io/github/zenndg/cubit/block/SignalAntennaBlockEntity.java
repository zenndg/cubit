package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.network.AntennaChannel;
import io.github.zenndg.cubit.domain.network.AntennaSignal;
import io.github.zenndg.cubit.domain.network.ChunkCall;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.SavedLink;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
 * (Delivery 4, details 6 and 19). Its battery has no tier (answer 2199).
 */
public final class SignalAntennaBlockEntity extends NetworkBlockEntity {
  private static final String SERVER_KEY = "Server";
  private static final String SERVER_NAME_KEY = "ServerName";
  private static final String CHANNEL_KEY = "Channel";

  private final IItemHandler itemHandler =
      InputFirstItems.of(energySlot(), (EmptyItemHandler) EmptyItemHandler.INSTANCE);
  private static final String CHUNK_KEY = "ChunkLoading";

  private final AntennaUplink uplink = new AntennaUplink();
  private final HeldChunk chunk = new HeldChunk(this::setChanged);
  private boolean chunkLoading = true;
  private SavedLink link = SavedLink.NONE;

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
    if (antenna.uplink.tick(server, pos, antenna.link)) {
      antenna.chunk.recheck();
      antenna.changeShown();
    }
    antenna.work(server);
  }

  /**
   * The chunk is paid first, and then what it spends for being on and for the distance, while it
   * works. Stopped, it lets the chunk go and spends nothing (answers 2435, 2439 and 2553).
   */
  private void work(ServerLevel level) {
    boolean works = uplink.state().works();
    Energy upkeep =
        NetworkParts.antenna().spending(true, works, false, Energy.ZERO, uplink.distanceCost());
    ChunkCall.Reach reach = new ChunkCall.Reach(uplink.sharesChunk(worldPosition), works);
    HeldChunk.Asked asked = new HeldChunk.Asked(owner(), chunkLoading, upkeep, reach);
    EnergyStock left = chunk.tick(level, worldPosition, asked, battery().stock());
    EnergyStock.Spent spent = left.spend(upkeep);
    battery().set(spent.stock());
    if (!spent.paid().isZero()) {
      setChanged();
    }
  }

  /** Whether it holds its chunk, and why not. */
  public ChunkState chunkState() {
    return chunk.state();
  }

  /** What its chunk costs now per tick, which its Server sends first (answer 2302). */
  Energy chunkCost() {
    return chunk.cost();
  }

  /** The switch of the owner, as in the Server (answer 2213). */
  public boolean chunkLoading() {
    return chunkLoading;
  }

  /** Only an antenna with signal takes energy from its Server (Delivery 4, detail 16). */
  boolean receivesFromNetwork() {
    return !isRemoved() && uplink.state() == AntennaSignal.State.LINKED;
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

  /** The channel it takes now, which the item keeps as the last one. */
  void take(AntennaChannel channel) {
    link = link.withChannel(channel);
    changeShown();
  }

  @Override
  public IItemHandler itemHandler() {
    return itemHandler;
  }

  /** Its screen comes with its menu, later in this delivery (Delivery 4, detail 7). */
  @Override
  void openScreen(Player player) {
    // Nothing opens yet.
  }

  @Override
  boolean use(Player player, ItemStack stack) {
    return EnergySlotItems.insert(energySlot(), stack);
  }

  /** Its channel goes back to its Server, and it is listed again when placed (answer 2547). */
  @Override
  void placed(Player player) {
    AntennaPlacing.placed(this, player);
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
        RobotTier.BASE, energy().amount(), List.of(), Optional.empty(), chunkLoading, false, link);
  }

  @Override
  void restore(SavedNetwork saved) {
    link = saved.link();
    chunkLoading = saved.chunkLoading();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    link.server().ifPresent(server -> tag.putUUID(SERVER_KEY, server));
    link.serverName().ifPresent(name -> tag.putString(SERVER_NAME_KEY, name));
    link.channel().ifPresent(channel -> tag.putString(CHANNEL_KEY, channel.key()));
    uplink.save(tag);
    tag.putBoolean(CHUNK_KEY, chunkLoading);
    chunk.save(tag);
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
    chunkLoading = !tag.contains(CHUNK_KEY) || tag.getBoolean(CHUNK_KEY);
    chunk.load(tag);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    // The animations come with its model, later in this delivery.
  }
}
