package io.github.zenndg.cubit.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Gives the item of the dock its GeckoLib renderer, made once, on first use. */
final class DockItemExtensions implements IClientItemExtensions {
  static final DockItemExtensions INSTANCE = new DockItemExtensions();
  private DockItemRenderer renderer;

  private DockItemExtensions() {}

  @Override
  public BlockEntityWithoutLevelRenderer getCustomRenderer() {
    if (renderer == null) {
      renderer = new DockItemRenderer();
    }
    return renderer;
  }
}
