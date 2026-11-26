package io.github.zenndg.cubit.block;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.desk.DeskCheck;
import io.github.zenndg.cubit.domain.desk.DeskStatus;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

/**
 * One tick of a disassembly: it pauses as an order does, and ends a unit at once, taking the items
 * from the slot, putting all the parts in the output and paying the energy in the same tick (rule
 * 13 and answers 2529 and 2535). What the item keeps is checked again then, and anything that stops
 * it ends the work, with the item kept in the slot.
 */
public final class DisassemblyWork {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String STOPPED = "The desk at {} stopped taking {} apart: {}";

  private DisassemblyWork() {}

  /** The Start button, checked again on the server (rule 19). Nothing changes when refused. */
  public static DisassemblyRefusal start(EngineeringDeskBlockEntity desk, Level level) {
    DisassemblyRefusal refusal = DisassemblyRecipes.refusal(desk, level);
    if (refusal.allows()) {
      DisassemblyRecipes.Plan plan =
          DisassemblyRecipes.plan(level, desk.disassembly().item()).orElseThrow();
      desk.disassembly().start(desk.disassembly().item().getCount() / plan.yield());
      desk.workChanged();
    }
    return refusal;
  }

  public static void cancel(EngineeringDeskBlockEntity desk) {
    desk.disassembly().cancel();
    desk.workChanged();
  }

  static DeskStatus tick(EngineeringDeskBlockEntity desk, Level level, boolean redstone) {
    DisassemblyRefusal refusal = DisassemblyRecipes.refusal(desk, level);
    if (refusal != DisassemblyRefusal.NONE && refusal != DisassemblyRefusal.BUSY) {
      LOGGER.warn(STOPPED, desk.getBlockPos(), desk.disassembly().item(), refusal);
      cancel(desk);
      return DeskStatus.IDLE;
    }
    DisassemblyRecipes.Plan plan =
        DisassemblyRecipes.plan(level, desk.disassembly().item()).orElseThrow();
    boolean fits = DeskOutput.fits(desk.output(), desk.slots(), plan.parts());
    DeskStatus status =
        DeskStatus.decide(new DeskCheck(true, redstone, true, true, desk.canPayCraft(), fits));
    if (status.isWorking() && desk.advance()) {
      DeskOutput.insert(desk.output(), desk.slots(), plan.parts());
      desk.disassembly().unitDone(plan.yield());
      desk.disassembled();
    }
    return status;
  }
}
