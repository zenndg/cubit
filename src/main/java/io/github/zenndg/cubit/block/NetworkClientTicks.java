package io.github.zenndg.cubit.block;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the client does on each tick of a block of the Network: its view moves on, and its loop of
 * sound may start. The client sets the sounds at start up, so common code never loads a class of
 * the client (rule 23).
 */
public final class NetworkClientTicks {
  private static Consumer<NetworkServerBlockEntity> serverSounds = server -> {};
  private static Consumer<SolarRelayBlockEntity> relaySounds = relay -> {};

  private NetworkClientTicks() {}

  public static void set(
      Consumer<NetworkServerBlockEntity> servers, Consumer<SolarRelayBlockEntity> relays) {
    serverSounds = servers;
    relaySounds = relays;
  }

  static void tick(Level level, BlockPos pos, BlockState state, NetworkServerBlockEntity server) {
    server.view().tick();
    serverSounds.accept(server);
  }

  static void tick(Level level, BlockPos pos, BlockState state, SolarRelayBlockEntity relay) {
    relay.view().tick(pos, state.getValue(NetworkBlock.FACING), level.getDayTime());
    relaySounds.accept(relay);
  }
}
