package io.github.zenndg.cubit.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zenndg.cubit.domain.desk.CascadeRun;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * A plan of the cascade, kept in the desk and in its item: what was asked, the tasks by recipe and
 * the items reserved and waiting for room (answers 2010, 2028 and 2031).
 */
public record SavedCascade(
    Item item,
    int quantity,
    List<SavedStep> steps,
    List<ItemContainerContents> reserved,
    List<ItemContainerContents> waiting) {
  private static final String ITEM_FIELD = "item";
  private static final String QUANTITY_FIELD = "quantity";
  private static final String STEPS_FIELD = "steps";
  private static final String RESERVED_FIELD = "reserved";
  private static final String WAITING_FIELD = "waiting";
  private static final String RECIPE_FIELD = "recipe";
  private static final String TOTAL_FIELD = "total";
  private static final String DONE_FIELD = "done";

  /** A task of the plan: the recipe, the crafts asked and the ones done. */
  public record SavedStep(ResourceLocation recipe, int total, int done) {
    static final Codec<SavedStep> CODEC =
        RecordCodecBuilder.create(instance ->
            instance
                .group(
                    ResourceLocation.CODEC.fieldOf(RECIPE_FIELD).forGetter(SavedStep::recipe),
                    Codec.INT.fieldOf(TOTAL_FIELD).forGetter(SavedStep::total),
                    Codec.INT.fieldOf(DONE_FIELD).forGetter(SavedStep::done))
                .apply(instance, SavedStep::new));

    static final StreamCodec<RegistryFriendlyByteBuf, SavedStep> STREAM_CODEC =
        StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            SavedStep::recipe,
            ByteBufCodecs.VAR_INT,
            SavedStep::total,
            ByteBufCodecs.VAR_INT,
            SavedStep::done,
            SavedStep::new);

    private boolean isValid() {
      return total > 0 && done >= 0 && done <= total;
    }

    private CascadeRun.Step step() {
      return new CascadeRun.Step(recipe.toString(), total, done);
    }
  }

  public static final Codec<SavedCascade> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  BuiltInRegistries.ITEM
                      .byNameCodec()
                      .fieldOf(ITEM_FIELD)
                      .forGetter(SavedCascade::item),
                  Codec.INT.fieldOf(QUANTITY_FIELD).forGetter(SavedCascade::quantity),
                  SavedStep.CODEC.listOf().fieldOf(STEPS_FIELD).forGetter(SavedCascade::steps),
                  ItemContainerContents.CODEC
                      .listOf()
                      .optionalFieldOf(RESERVED_FIELD, List.of())
                      .forGetter(SavedCascade::reserved),
                  ItemContainerContents.CODEC
                      .listOf()
                      .optionalFieldOf(WAITING_FIELD, List.of())
                      .forGetter(SavedCascade::waiting))
              .apply(instance, SavedCascade::new));

  public static final StreamCodec<RegistryFriendlyByteBuf, SavedCascade> STREAM_CODEC =
      StreamCodec.composite(
          ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()),
          SavedCascade::item,
          ByteBufCodecs.VAR_INT,
          SavedCascade::quantity,
          SavedStep.STREAM_CODEC.apply(ByteBufCodecs.list()),
          SavedCascade::steps,
          ItemContainerContents.STREAM_CODEC.apply(ByteBufCodecs.list()),
          SavedCascade::reserved,
          ItemContainerContents.STREAM_CODEC.apply(ByteBufCodecs.list()),
          SavedCascade::waiting,
          SavedCascade::new);

  /** The tasks, or nothing from an edited save, whose items still come back (rule 17). */
  Optional<CascadeRun> run() {
    boolean valid = !steps.isEmpty() && steps.stream().allMatch(SavedStep::isValid);
    return valid
        ? Optional.of(new CascadeRun(steps.stream().map(SavedStep::step).toList()))
        : Optional.empty();
  }

  /** Whether the desk has items of a plan, so its item never nests (answer 1481). */
  public boolean holdsItems() {
    return hasItems(reserved) || hasItems(waiting);
  }

  private static boolean hasItems(List<ItemContainerContents> parts) {
    return parts.stream().anyMatch(DeskSaves::hasItems);
  }
}
