package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.dock.DockName;
import io.github.zenndg.cubit.domain.robot.RobotName;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Who a Dock is: its own id, drawn when it is placed or, for an old Dock, when it loads, and the
 * name the owner gave it, shown as the standard "Dock" and eight digits without one (answers 1867
 * to 1870). The id never changes when the Dock is broken and placed again.
 */
public final class DockIdentity {
  private static final String ID_KEY = "DockId";
  private static final String NAME_KEY = "CustomName";

  private final Runnable changed;
  private UUID id;
  private Optional<String> given = Optional.empty();

  DockIdentity(Runnable changed) {
    this.changed = changed;
  }

  /** An old Dock without an id draws one now, and changes nothing else (rule 18). */
  public UUID id() {
    if (id == null) {
      id = UUID.randomUUID();
    }
    return id;
  }

  public DockName name() {
    return DockName.of(id(), given.flatMap(RobotName::typed));
  }

  /** The rules of the name of the Cubit; an empty name goes back to the standard (1868). */
  public boolean rename(String typed) {
    Optional<RobotName> name = RobotName.typed(typed);
    if (name.isEmpty()) {
      return false;
    }
    given = DockName.kept(name);
    changed.run();
    return true;
  }

  void save(CompoundTag tag) {
    tag.putUUID(ID_KEY, id());
    given.ifPresent(name -> tag.putString(NAME_KEY, name));
  }

  void load(CompoundTag tag) {
    id = tag.hasUUID(ID_KEY) ? tag.getUUID(ID_KEY) : id;
    given =
        tag.contains(NAME_KEY) ? DockName.kept(RobotName.typed(tag.getString(NAME_KEY))) : given;
  }

  /** The item gives back the id and the name it kept, as an anvil name does. */
  void apply(UUID kept, Component name) {
    if (kept != null) {
      id = kept;
    }
    if (name != null) {
      given = DockName.kept(RobotName.typed(name.getString()));
    }
  }

  void collect(DataComponentMap.Builder components) {
    components.set(CubitDataComponents.DOCK_ID.get(), id());
    given.ifPresent(name -> components.set(DataComponents.CUSTOM_NAME, Component.literal(name)));
  }
}
