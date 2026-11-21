package io.github.zenndg.cubit.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;

/**
 * The `desk_category` field of the recipes of the desk. It is optional, and an unknown value goes
 * to the tools with a line in the log, so a typo in a pack never removes a recipe (rule 17, answers
 * 1521 and 1522). The game's own `category` stays the one of the recipe book.
 */
public final class DeskCategoryCodecs {
  public static final String FIELD = "desk_category";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String UNKNOWN = "Unknown desk_category {}, using {}.";

  private static final Codec<DeskCategory> CODEC =
      Codec.STRING.xmap(DeskCategoryCodecs::read, DeskCategory::id);

  /** Always written by the datagen, so each JSON shows its category. */
  public static final MapCodec<DeskCategory> MAP_CODEC =
      CODEC.optionalFieldOf(FIELD).xmap(found -> found.orElse(DeskCategory.DEFAULT), Optional::of);

  public static final StreamCodec<ByteBuf, DeskCategory> STREAM_CODEC =
      ByteBufCodecs.STRING_UTF8.map(DeskCategoryCodecs::read, DeskCategory::id);

  private DeskCategoryCodecs() {}

  private static DeskCategory read(String id) {
    Optional<DeskCategory> found = DeskCategory.byId(id);
    if (found.isEmpty()) {
      LOGGER.warn(UNKNOWN, id, DeskCategory.DEFAULT.id());
    }
    return found.orElse(DeskCategory.DEFAULT);
  }

  /** The category of any recipe of the desk. One that does not say goes to the tools. */
  public static DeskCategory of(Recipe<?> recipe) {
    return recipe instanceof DeskCategorized categorized
        ? categorized.deskCategory()
        : DeskCategory.DEFAULT;
  }
}
