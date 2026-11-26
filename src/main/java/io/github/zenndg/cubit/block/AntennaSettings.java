package io.github.zenndg.cubit.block;

import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.item.SavedNetwork;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

/**
 * What the owner chooses on the screen of a Signal Antenna: the general switch, the switch of the
 * chunk, the wall and the reserve (answers 2213, 2448 and 2548, and Delivery 4, detail 7). The
 * switches start on, and the reserve is the one of the config.
 */
final class AntennaSettings {
  private static final String ENABLED_KEY = "Enabled";
  private static final String CHUNK_KEY = "ChunkLoading";
  private static final String WALL_KEY = "ShowWall";
  private static final String UPS_KEY = "Ups";

  private boolean enabled = true;
  private boolean chunkLoading = true;
  private boolean showWall;
  private UpsReserve ups = NetworkParts.antenna().ups();

  boolean enabled() {
    return enabled;
  }

  void setEnabled(boolean on) {
    enabled = on;
  }

  boolean chunkLoading() {
    return chunkLoading;
  }

  void setChunkLoading(boolean on) {
    chunkLoading = on;
  }

  boolean showWall() {
    return showWall;
  }

  void setShowWall(boolean shown) {
    showWall = shown;
  }

  UpsReserve ups() {
    return ups;
  }

  void setUps(UpsReserve chosen) {
    ups = chosen;
  }

  /** What a placed item gives back; the general switch is not kept, so it starts on. */
  void restore(SavedNetwork saved) {
    chunkLoading = saved.chunkLoading();
    showWall = saved.showWall();
    ups = saved.ups().orElseGet(() -> NetworkParts.antenna().ups());
  }

  Optional<UpsReserve> kept() {
    return Optional.of(ups);
  }

  void save(CompoundTag tag) {
    tag.putBoolean(ENABLED_KEY, enabled);
    tag.putBoolean(CHUNK_KEY, chunkLoading);
    tag.putBoolean(WALL_KEY, showWall);
    tag.putInt(UPS_KEY, ups.percent());
  }

  /** A switch that is not saved is on, and a broken reserve is made valid (rule 17). */
  void load(CompoundTag tag) {
    enabled = !tag.contains(ENABLED_KEY) || tag.getBoolean(ENABLED_KEY);
    chunkLoading = !tag.contains(CHUNK_KEY) || tag.getBoolean(CHUNK_KEY);
    showWall = tag.getBoolean(WALL_KEY);
    ups = tag.contains(UPS_KEY) ? UpsReserve.of(tag.getInt(UPS_KEY)) : ups;
  }
}
