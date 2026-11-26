package io.github.zenndg.cubit.client;

import io.github.zenndg.cubit.block.ServerClientView;
import io.github.zenndg.cubit.domain.network.ServerLook;

/**
 * What is drawn of a Network Server in a frame: its look, the bars on their way, how strong the
 * lights are, the age for what blinks and whether the power is going away.
 */
record ServerShown(
    ServerLook look, ServerLook.Columns bars, float light, long age, boolean falling) {
  static ServerShown of(ServerClientView view, long age, float partialTick) {
    return new ServerShown(
        view.look(), view.bars(), view.light(partialTick), age, view.isFalling());
  }

  /** An item does not move. */
  static ServerShown still(ServerLook look) {
    return new ServerShown(look, look.columns(), look.powered() ? 1f : 0f, 0, false);
  }
}
