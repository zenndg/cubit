package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskOrder;
import io.github.zenndg.cubit.domain.desk.DeskProgress;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/** The recipe a desk has chosen, the order of it and the progress of the craft. */
final class DeskJob {
  static final String PROGRESS_KEY = "Progress";
  private static final String ORDER_KEY = "Order";

  private ResourceLocation recipe;
  private DeskOrder order = DeskOrder.NONE;
  private DeskProgress progress = DeskProgress.NONE;

  Optional<ResourceLocation> recipe() {
    return Optional.ofNullable(recipe);
  }

  DeskOrder order() {
    return order;
  }

  DeskProgress progress() {
    return progress;
  }

  void choose(ResourceLocation chosen) {
    recipe = chosen;
    cancel();
  }

  /** Without an order, only plus starts one, for the chosen recipe. True when it changed. */
  boolean adjust(int delta, int maxOrder) {
    if (recipe == null || (!order.isActive() && delta <= 0)) {
      return false;
    }
    order = order.adjust(delta, maxOrder);
    if (!order.isActive()) {
      progress = DeskProgress.NONE;
    }
    return true;
  }

  void cancel() {
    order = DeskOrder.NONE;
    progress = DeskProgress.NONE;
  }

  void limitTo(int maxOrder) {
    order = order.limitedTo(maxOrder);
  }

  boolean advance(int craftTicks) {
    progress = progress.advance();
    return progress.isReady(craftTicks);
  }

  void crafted() {
    order = order.crafted();
    progress = DeskProgress.NONE;
  }

  /** The craft of a cascade ends without counting an order. */
  void restart() {
    progress = DeskProgress.NONE;
  }

  Optional<SavedDeskOrder> saved() {
    return recipe().map(id -> new SavedDeskOrder(id, order.total(), order.done()));
  }

  void use(Optional<SavedDeskOrder> saved, int maxOrder) {
    recipe = saved.map(SavedDeskOrder::recipe).orElse(null);
    order = saved.map(SavedDeskOrder::order).orElse(DeskOrder.NONE).limitedTo(maxOrder);
  }

  /** The clients see the task of a plan in place of the order. */
  void save(CompoundTag tag, Optional<SavedDeskOrder> shown) {
    DeskSaves.saveOrder(tag, ORDER_KEY, shown);
    tag.putInt(PROGRESS_KEY, progress.ticks());
  }

  void load(CompoundTag tag, BlockPos pos, int maxOrder) {
    use(DeskSaves.loadOrder(tag, ORDER_KEY, pos), maxOrder);
    progress = new DeskProgress(Math.max(0, tag.getInt(PROGRESS_KEY)));
  }
}
