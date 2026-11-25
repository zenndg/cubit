package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.dock.DockXpNotice;
import io.github.zenndg.cubit.domain.dock.DockXpRules;
import io.github.zenndg.cubit.domain.dock.DockXpSpec;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.EnergyStock;
import io.github.zenndg.cubit.domain.xp.XpConversion;
import io.github.zenndg.cubit.domain.xp.XpFlow;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.item.SavedDockXp;
import io.github.zenndg.cubit.menu.DockXpControl;
import io.github.zenndg.cubit.player.PlayerXpTrade;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * The XP of a Dock: its tank, its mode and the energy it made, kept in the block and in its item as
 * `cubit:dock_xp` (answers 1831 and 1838). The rules live in the domain, the capacity follows the
 * tier, and a smaller one loses nothing (answer 1937).
 */
public final class DockXp implements DockXpControl {
  private static final String KEY = "XpTank";
  private static final String MODE_KEY = "XpMode";
  private static final String MADE_KEY = "XpEnergyMade";

  private final Runnable changed;
  private final Supplier<DockXpSpec> spec;
  private final Predicate<Player> owns;
  private final IFluidHandler pipes = new DockXpPipes(this);
  private SavedDockXp state = SavedDockXp.EMPTY;
  private XpFlow flow = XpFlow.START;
  private DockXpNotice notice = DockXpNotice.START;

  DockXp(Runnable changed, Supplier<DockXpSpec> spec, Predicate<Player> owns) {
    this.changed = changed;
    this.spec = spec;
    this.owns = owns;
  }

  @Override
  public XpTank tank() {
    return new XpTank(state.fluid(), spec.get().tankCapacity());
  }

  public void setTank(XpTank next) {
    set(new SavedDockXp(next.fluid(), state.mode(), state.energyMade()));
  }

  private void set(SavedDockXp next) {
    if (!next.equals(state)) {
      state = next;
      notice = notice.update(tank());
      changed.run();
    }
  }

  /** What a docked Cubit passes each tick, and the pace of the conversion (1835 and 1939). */
  public int transferPerTick() {
    return spec.get().transferPerTick();
  }

  /** What other mods see through the capability of fluids (answer 1818). */
  public IFluidHandler pipes() {
    return pipes;
  }

  /** The XP of a docked Cubit, all of it (answer 1831). */
  Taken take(EnergyStock stock, int points) {
    DockXpRules.Received got =
        DockXpRules.receive(state.mode(), tank(), stock, points, conversion());
    keep(got.tank(), got.converted());
    return new Taken(got.stock(), notice.tells(got.overflowed()));
  }

  /** The stock after the XP that came in, and whether the owner should read of the full tank. */
  record Taken(EnergyStock stock, boolean tells) {}

  void told() {
    notice = notice.afterTelling();
  }

  /** On each tick, with XP in Convert all only, at the pace of the tier (answers 1939 and 2104). */
  EnergyStock convert(EnergyStock stock) {
    if (state.mode() != DockXpMode.CONVERT_ALL || state.fluid() == 0) {
      flow = XpFlow.START;
      return stock;
    }
    XpConversion conversion = conversion();
    XpFlow.Budget budget = flow.tick(transferPerTick(), conversion.rate());
    flow = budget.flow();
    DockXpRules.Converted done =
        DockXpRules.convert(state.mode(), tank(), stock, budget.points(), conversion);
    keep(done.tank(), done.points());
    return done.stock();
  }

  private void keep(XpTank tank, int converted) {
    Energy more = conversion().energyOf(converted);
    set(new SavedDockXp(tank.fluid(), state.mode(), state.energyMade().plus(more)));
  }

  private static XpConversion conversion() {
    return CubitMod.CONFIG.xp().dockConversion();
  }

  @Override
  public DockXpMode mode() {
    return state.mode();
  }

  @Override
  public Energy energyMade() {
    return state.energyMade().made();
  }

  @Override
  public boolean choose(Player player, DockXpMode mode) {
    if (!owns.test(player) || mode == state.mode()) {
      return false;
    }
    set(new SavedDockXp(state.fluid(), mode, state.energyMade()));
    return true;
  }

  @Override
  public boolean send(Player player) {
    return owns.test(player)
        && keepTraded(PlayerXpTrade.send(player, tank(), CubitMod.CONFIG.xp().rate()));
  }

  @Override
  public boolean receive(Player player) {
    return owns.test(player)
        && keepTraded(PlayerXpTrade.receive(player, tank(), CubitMod.CONFIG.xp().rate()));
  }

  private boolean keepTraded(XpTank next) {
    boolean moved = !next.equals(tank());
    setTank(next);
    return moved;
  }

  void save(CompoundTag tag) {
    tag.putInt(KEY, state.fluid());
    tag.putString(MODE_KEY, state.mode().key());
    tag.putLong(MADE_KEY, state.energyMade().made().amount());
  }

  /** A Dock from before the XP, or before its modes, starts as a new one (answer 1832). */
  void load(CompoundTag tag) {
    String mode = tag.contains(MODE_KEY) ? tag.getString(MODE_KEY) : DockXpMode.INITIAL.key();
    state = SavedDockXp.safe(tag.getInt(KEY), mode, tag.getLong(MADE_KEY));
  }

  void apply(SavedDockXp saved) {
    state = saved;
  }

  void collect(DataComponentMap.Builder components) {
    components.set(CubitDataComponents.DOCK_XP.get(), state);
  }
}
