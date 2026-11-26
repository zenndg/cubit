package io.github.zenndg.cubit.client;

import java.util.function.Supplier;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Gives an item of the Network its GeckoLib renderer, made once, on first use. */
final class NetworkItemExtensions implements IClientItemExtensions {
  static final NetworkItemExtensions SERVER =
      new NetworkItemExtensions(NetworkServerItemRenderer::new);
  static final NetworkItemExtensions RELAY = new NetworkItemExtensions(SolarRelayItemRenderer::new);

  private final Supplier<BlockEntityWithoutLevelRenderer> maker;
  private BlockEntityWithoutLevelRenderer renderer;

  private NetworkItemExtensions(Supplier<BlockEntityWithoutLevelRenderer> maker) {
    this.maker = maker;
  }

  @Override
  public BlockEntityWithoutLevelRenderer getCustomRenderer() {
    if (renderer == null) {
      renderer = maker.get();
    }
    return renderer;
  }
}
