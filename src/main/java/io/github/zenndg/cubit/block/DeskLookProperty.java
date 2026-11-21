package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.desk.DeskLook;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

/** What the desk shows, as a block state. The lowercase names are part of the saves (1508). */
public enum DeskLookProperty implements StringRepresentable {
  IDLE(DeskLook.IDLE),
  CRAFTING(DeskLook.CRAFTING),
  PAUSED(DeskLook.PAUSED);

  private final DeskLook look;

  DeskLookProperty(DeskLook look) {
    this.look = look;
  }

  static DeskLookProperty of(DeskLook look) {
    for (DeskLookProperty property : values()) {
      if (property.look == look) {
        return property;
      }
    }
    return IDLE;
  }

  public DeskLook look() {
    return look;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
