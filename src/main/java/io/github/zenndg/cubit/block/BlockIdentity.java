package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.ownership.BlockName;
import io.github.zenndg.cubit.domain.robot.RobotName;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Who a block with an owner is: its own id, drawn when it is placed or, for an old one, when it
 * loads, and the name the owner gave it, shown as its kind and eight digits without one (answers
 * 1867 to 1870). The id never changes when the block is broken and placed again.
 */
public final class BlockIdentity {
  private static final String NAME_KEY = "CustomName";

  private final Kept kept;
  private final Runnable changed;
  private UUID id;
  private Optional<String> given = Optional.empty();

  BlockIdentity(Kept kept, Runnable changed) {
    this.kept = kept;
    this.changed = changed;
  }

  /** An old block without an id draws one now, and changes nothing else (rule 18). */
  public UUID id() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    return id;
  }

  public BlockName name() {
    return BlockName.of(kept.kind(), id(), given.flatMap(RobotName::typed));
  }

  /** The rules of the name of the Cubit; an empty name goes back to the standard (1868). */
  public boolean rename(String typed) {
    Optional<RobotName> name = RobotName.typed(typed);
    if (name.isEmpty()) {
      return false;
    }
    given = BlockName.kept(name);
    changed.run();
    return true;
  }

  void save(CompoundTag tag) {
    tag.putUUID(kept.idKey(), id());
    given.ifPresent(name -> tag.putString(NAME_KEY, name));
  }

  void load(CompoundTag tag) {
    id = tag.hasUUID(kept.idKey()) ? tag.getUUID(kept.idKey()) : id;
    given =
        tag.contains(NAME_KEY) ? BlockName.kept(RobotName.typed(tag.getString(NAME_KEY))) : given;
  }

  /** The item gives back the id and the name it kept, as an anvil name does. */
  void apply(UUID saved, Component name) {
    if (saved != null) {
      id = saved;
    }
    if (name != null) {
      given = BlockName.kept(RobotName.typed(name.getString()));
    }
  }

  void collect(DataComponentMap.Builder components) {
    components.set(kept.component().get(), id());
    given.ifPresent(name -> components.set(DataComponents.CUSTOM_NAME, Component.literal(name)));
  }

  /** What each block calls its id: the start of the name, the key of the save and the component. */
  record Kept(BlockName.Kind kind, String idKey, Supplier<DataComponentType<UUID>> component) {}
}
