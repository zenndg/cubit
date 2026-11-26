package io.github.zenndg.cubit.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * The item in the slot of the Disassemble tab and how many units of it are asked and done, kept in
 * the save of the desk as `Disassembly` and in its item as `cubit:engineering_desk_disassembly`
 * (answer 2535, detail 5). The item is never lost, also when the numbers are invalid (rule 17).
 */
public record SavedDisassembly(ItemStack item, int total, int done) {
  private static final String ITEM_FIELD = "item";
  private static final String TOTAL_FIELD = "total";
  private static final String DONE_FIELD = "done";

  public static final Codec<SavedDisassembly> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  ItemStack.OPTIONAL_CODEC.fieldOf(ITEM_FIELD).forGetter(SavedDisassembly::item),
                  Codec.INT.fieldOf(TOTAL_FIELD).forGetter(SavedDisassembly::total),
                  Codec.INT.fieldOf(DONE_FIELD).forGetter(SavedDisassembly::done))
              .apply(instance, SavedDisassembly::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, SavedDisassembly> STREAM_CODEC =
      StreamCodec.composite(
          ItemStack.OPTIONAL_STREAM_CODEC,
          SavedDisassembly::item,
          ByteBufCodecs.VAR_INT,
          SavedDisassembly::total,
          ByteBufCodecs.VAR_INT,
          SavedDisassembly::done,
          SavedDisassembly::new);

  /** Invalid numbers, from an edited save, stop the disassembly and keep the item. */
  public DeskOrder units() {
    if (total < 0 || done < 0 || done > total) {
      return DeskOrder.NONE;
    }
    return new DeskOrder(total, done);
  }
}
