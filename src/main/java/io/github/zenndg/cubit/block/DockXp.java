package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockXpSpec;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.item.SavedDockXp;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;

/**
 * The XP tank of a Dock, kept in the block as `XpTank` and in its item as `cubit:dock_xp`. The
 * capacity follows the tier, and a smaller one loses nothing (answers 1833, 1838 and 1937).
 */
public final class DockXp {
  private static final String KEY = "XpTank";

  private final Runnable changed;
  private final Supplier<DockXpSpec> spec;
  private int fluid;

  DockXp(Runnable changed, Supplier<DockXpSpec> spec) {
    this.changed = changed;
    this.spec = spec;
  }

  public XpTank tank() {
    return new XpTank(fluid, spec.get().tankCapacity());
  }

  public void setTank(XpTank next) {
    if (next.fluid() != fluid) {
      fluid = next.fluid();
      changed.run();
    }
  }

  /** What a docked Cubit passes each tick (answer 1835). */
  public int transferPerTick() {
    return spec.get().transferPerTick();
  }

  void save(CompoundTag tag) {
    tag.putInt(KEY, fluid);
  }

  void load(CompoundTag tag) {
    fluid = new SavedDockXp(tag.getInt(KEY)).fluid();
  }

  void apply(SavedDockXp saved) {
    fluid = saved.fluid();
  }

  void collect(DataComponentMap.Builder components) {
    components.set(CubitDataComponents.DOCK_XP.get(), new SavedDockXp(fluid));
  }
}
