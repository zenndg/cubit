package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.block.DisassemblyRecipes;
import io.github.zenndg.cubit.block.DisassemblyWork;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.DisassemblyRefusal;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The Disassemble tab in the menu of the desk: its slot, what the server found and its two buttons
 * (answers 2527, 2528 and 2534). The server decides, and the client only shows (rule 19).
 */
public final class DisassemblyMenuPart {
  public static final int START = 200;
  public static final int CANCEL = 201;
  private static final int RUNNING = 0;
  private static final int REFUSAL = 1;
  private static final int TOTAL = 2;
  private static final int DONE = 3;
  private static final int COUNT = 4;
  private static final int SLOT = 0;

  private final EngineeringDeskBlockEntity desk;
  private final Container slot;
  private final ContainerData data = new SimpleContainerData(COUNT);

  private DisassemblyMenuPart(EngineeringDeskBlockEntity desk, Container slot) {
    this.desk = desk;
    this.slot = slot;
  }

  static DisassemblyMenuPart onServer(EngineeringDeskBlockEntity desk) {
    DisassemblyMenuPart part = new DisassemblyMenuPart(desk, desk.disassembly().slot());
    part.refresh();
    return part;
  }

  static DisassemblyMenuPart onClient() {
    return new DisassemblyMenuPart(null, new SimpleContainer(1));
  }

  ContainerData data() {
    return data;
  }

  Container slot() {
    return slot;
  }

  static boolean owns(int id) {
    return id == START || id == CANCEL;
  }

  void refresh() {
    if (desk == null || desk.getLevel() == null) {
      return;
    }
    data.set(RUNNING, desk.disassembly().isRunning() ? 1 : 0);
    data.set(REFUSAL, DisassemblyRecipes.refusal(desk, desk.getLevel()).ordinal());
    data.set(TOTAL, desk.disassembly().units().total());
    data.set(DONE, desk.disassembly().units().done());
  }

  /** The Start button checks everything again, and Cancel only stops a running one. */
  boolean click(ServerPlayer player, int id) {
    if (id == START && !running()) {
      DisassemblyWork.start(desk, player.level());
    } else if (id == CANCEL && running()) {
      DisassemblyWork.cancel(desk);
    }
    return true;
  }

  public boolean running() {
    return desk == null ? data.get(RUNNING) == 1 : desk.disassembly().isRunning();
  }

  public DisassemblyRefusal refusal() {
    return ScreenEnums.of(DisassemblyRefusal.values(), data.get(REFUSAL));
  }

  /** The tab opens from the tier that has it (answer 2531). */
  public boolean allowed() {
    return refusal() != DisassemblyRefusal.LOCKED_TIER;
  }

  public int left() {
    return Math.max(0, data.get(TOTAL) - data.get(DONE));
  }

  public ItemStack item() {
    return slot.getItem(SLOT);
  }

  /** What comes back of one unit of the item in the slot, as the recipes say. */
  public List<ItemStack> parts(Level level) {
    return DisassemblyRecipes.plan(level, item())
        .map(DisassemblyRecipes.Plan::parts)
        .orElse(List.of());
  }

  /**
   * Only an item that comes apart goes in, and nothing moves while it runs (2535, details 2, 3).
   */
  boolean accepts(Level level, ItemStack stack) {
    return !running() && DisassemblyRecipes.plan(level, stack).isPresent();
  }
}
