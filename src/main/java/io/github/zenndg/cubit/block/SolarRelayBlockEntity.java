package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.SolarOutput;
import io.github.zenndg.cubit.domain.energy.SolarPanel;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.item.EnergySlotItems;
import io.github.zenndg.cubit.item.ModuleItem;
import io.github.zenndg.cubit.item.SavedNetwork;
import io.github.zenndg.cubit.registry.CubitBlockEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * The Solar Relay: it generates with the sun, straight to the server of its chunk, and follows its
 * tier. Its own battery, fed by cables and redstone, waits for the antennas (detail 18). Without a
 * server it keeps the battery and does nothing else (answer 2374).
 */
public final class SolarRelayBlockEntity extends NetworkBlockEntity {
  private static final String LINK_KEY = "Link";
  private static final String TRANSMITS_KEY = "Transmits";

  private final RelayPanel panel = new RelayPanel();
  private final RelayClientView view = new RelayClientView();
  private final IItemHandler itemHandler =
      InputFirstItems.of(energySlot(), (EmptyItemHandler) EmptyItemHandler.INSTANCE);
  private NetworkServerBlockEntity server;

  public SolarRelayBlockEntity(BlockPos pos, BlockState state) {
    super(
        CubitBlockEntities.SOLAR_RELAY.get(),
        pos,
        state,
        NetworkParts.RELAY_ID,
        NetworkParts.relayStart());
  }

  static void serverTick(Level level, BlockPos pos, BlockState state, SolarRelayBlockEntity relay) {
    relay.battery().tick();
    Energy offered = relay.panel.tick(level, pos, relay.rates(), relay::setChanged);
    relay.panel.gave(relay.server().map(paired -> paired.takeSun(offered)).orElse(Energy.ZERO));
    if (relay.panel.switched()) {
      relay.changeShown();
    }
  }

  private Optional<SolarOutput> rates() {
    return server().map(NetworkServerBlockEntity::sun);
  }

  @Override
  public Energy capacity() {
    return CubitMod.CONFIG.network().relayCapacity();
  }

  @Override
  public Energy inputPerTick() {
    return CubitMod.CONFIG.network().relayInputPerTick();
  }

  public Optional<NetworkServerBlockEntity> server() {
    return Optional.ofNullable(server);
  }

  /** The tier of its server, or the grey of the Base without one (answers 2217 and 2374). */
  public RobotTier tier() {
    return server().map(NetworkServerBlockEntity::tier).orElse(RobotTier.BASE);
  }

  /** What the client shows of it. */
  public RelayClientView view() {
    return view;
  }

  public SolarReading solar() {
    boolean day = level != null && SolarPanel.isDay(level.getDayTime());
    return panel.reading(rates(), day);
  }

  void pairWith(Optional<NetworkServerBlockEntity> next) {
    boolean changed = next.orElse(null) != server;
    server = next.orElse(null);
    if (level != null) {
      NetworkBlock.showTier(level, worldPosition, tier());
    }
    if (changed) {
      changeShown();
    }
  }

  @Override
  public IItemHandler itemHandler() {
    return itemHandler;
  }

  @Override
  boolean use(Player player, ItemStack stack) {
    if (stack.getItem() instanceof ModuleItem) {
      NetworkUpgrade.tellRelay(player);
      return true;
    }
    return EnergySlotItems.insert(energySlot(), stack);
  }

  @Override
  void gone() {
    // It holds nothing outside of itself, and its pair is made again by the block.
  }

  @Override
  SavedNetwork kept() {
    return SavedNetwork.onlyEnergy(energy().amount());
  }

  @Override
  void restore(SavedNetwork saved) {
    // Only the battery is kept, and the base class gives it back.
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    panel.save(tag);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    panel.load(tag);
    panel.read(tag);
    Optional<BlockPos> link =
        tag.contains(LINK_KEY) ? Optional.of(BlockPos.of(tag.getLong(LINK_KEY))) : Optional.empty();
    view.receive(link, tag.getBoolean(TRANSMITS_KEY));
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    panel.write(tag);
    server().ifPresent(paired -> tag.putLong(LINK_KEY, paired.getBlockPos().asLong()));
    tag.putBoolean(TRANSMITS_KEY, panel.isShining());
    return tag;
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    NetworkAnimations.register(controllers, this);
  }
}
