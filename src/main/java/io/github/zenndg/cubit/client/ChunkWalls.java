package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.AntennaClientView;
import io.github.zenndg.cubit.block.NetworkBlockEntity;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.block.SignalAntennaBlockEntity;
import io.github.zenndg.cubit.domain.network.ChunkWall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

/**
 * The walls around the chunks of the servers and the antennas of the player: translucent, one
 * pixel, in a light cyan for a server and in the color of the channel for an antenna, only with the
 * Tablet in a hand (answers 2447, 2448 and 2542). The blocks tell they are there on each of their
 * ticks, so no block is ever searched.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class ChunkWalls {
  private static final double HALF_PIXEL = 1.0 / 32;

  /** A bit lighter than the cyan of the channels (answer 2447). */
  private static final AreaLinePainter.Paint CYAN =
      new AreaLinePainter.Paint(0.55f, 0.92f, 0.98f, 0.22f);

  private static final Map<NetworkBlockEntity, AreaLinePainter.Paint> SHOWN = new HashMap<>();

  private ChunkWalls() {}

  static void tick(NetworkServerBlockEntity server) {
    if (server.showsWall()) {
      SHOWN.put(server, CYAN);
    } else {
      SHOWN.remove(server);
    }
  }

  static void tick(SignalAntennaBlockEntity antenna) {
    AntennaClientView view = antenna.view();
    if (view.showsWall() && view.channel().isPresent()) {
      SHOWN.put(antenna, ChannelTint.wall(view.channel().get()));
    } else {
      SHOWN.remove(antenna);
    }
  }

  @SubscribeEvent
  public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
    SHOWN.clear();
  }

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || player == null) {
      return;
    }
    SHOWN.keySet().removeIf(block -> block.isRemoved() || block.getLevel() != player.level());
    Map<AreaLinePainter.Paint, List<AABB>> walls = new HashMap<>();
    SHOWN.forEach((block, paint) -> {
      if (sees(player, block)) {
        walls.computeIfAbsent(paint, each -> new ArrayList<>()).addAll(sides(wallOf(block)));
      }
    });
    if (!walls.isEmpty()) {
      draw(event, walls);
    }
  }

  private static boolean sees(LocalPlayer player, NetworkBlockEntity block) {
    Vec3 eyes = player.getEyePosition();
    return wallOf(block)
        .seenBy(
            new ChunkWall.Viewer(
                block.isOwner(player), NetworkAppHeld.by(player), eyes.x, eyes.y, eyes.z));
  }

  private static ChunkWall wallOf(NetworkBlockEntity block) {
    BlockPos pos = block.getBlockPos();
    return ChunkWall.around(pos.getX(), pos.getY(), pos.getZ());
  }

  private static List<AABB> sides(ChunkWall wall) {
    double low = wall.bottom();
    double high = wall.top();
    return List.of(
        alongX(wall, wall.minZ(), low, high),
        alongX(wall, wall.maxZ(), low, high),
        alongZ(wall, wall.minX(), low, high),
        alongZ(wall, wall.maxX(), low, high));
  }

  private static AABB alongX(ChunkWall wall, double z, double low, double high) {
    return new AABB(wall.minX(), low, z - HALF_PIXEL, wall.maxX(), high, z + HALF_PIXEL);
  }

  private static AABB alongZ(ChunkWall wall, double x, double low, double high) {
    return new AABB(x - HALF_PIXEL, low, wall.minZ(), x + HALF_PIXEL, high, wall.maxZ());
  }

  private static void draw(
      RenderLevelStageEvent event, Map<AreaLinePainter.Paint, List<AABB>> walls) {
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    AreaLinePainter painter =
        new AreaLinePainter(
            Minecraft.getInstance().renderBuffers().bufferSource(),
            event.getCamera().getPosition());
    walls.forEach((paint, sides) -> painter.wall(sides, paint));
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }
}
