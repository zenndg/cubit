package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.registry.CubitFluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * How pipes see the XP tank of a dock: only the Cubit XP Fluid, in and out, by any amount and only
 * into free room, whatever the mode (answers 1818, 1841, 1936 and 2102).
 */
final class DockXpPipes implements IFluidHandler {
  private static final int ONE_TANK = 1;

  private final DockXp xp;

  DockXpPipes(DockXp xp) {
    this.xp = xp;
  }

  private static FluidStack stack(int amount) {
    return amount > 0 ? new FluidStack(CubitFluids.XP_FLUID.get(), amount) : FluidStack.EMPTY;
  }

  @Override
  public int getTanks() {
    return ONE_TANK;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return stack(xp.tank().fluid());
  }

  @Override
  public int getTankCapacity(int tank) {
    return xp.tank().capacity();
  }

  /** With nothing more on it, so no other fluid or data is lost by going in. */
  @Override
  public boolean isFluidValid(int tank, FluidStack fluid) {
    return FluidStack.isSameFluidSameComponents(fluid, stack(1));
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (!isFluidValid(0, resource)) {
      return 0;
    }
    XpTank tank = xp.tank();
    int accepted = Math.clamp(resource.getAmount(), 0, tank.freeFluid());
    move(tank.plusFluid(accepted), action);
    return accepted;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return isFluidValid(0, resource) ? drain(resource.getAmount(), action) : FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(int wanted, FluidAction action) {
    XpTank tank = xp.tank();
    int given = Math.clamp(wanted, 0, tank.fluid());
    move(tank.plusFluid(-given), action);
    return stack(given);
  }

  private void move(XpTank next, FluidAction action) {
    if (action.execute()) {
      xp.setTank(next);
    }
  }
}
