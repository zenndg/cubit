package io.github.zenndg.cubit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.ShowsName;
import io.github.zenndg.cubit.domain.ownership.FloatingName;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4fStack;

/**
 * The names over the Docks, the Network Servers and the Solar Relays of the player, with the Tablet
 * in a hand, in any app, up to 32 blocks away (answers 2469 and 2512 to 2517). The client looks for
 * them every ten ticks, only in the chunks already loaded around the player, and never on a frame.
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID, value = Dist.CLIENT)
public final class FloatingNames {
  private static final int CHUNKS_AROUND = SectionPos.blockToSectionCoord(FloatingName.REACH) + 1;

  private static final List<ShowsName> FOUND = new ArrayList<>();

  private FloatingNames() {}

  @SubscribeEvent
  public static void onTick(ClientTickEvent.Post event) {
    Minecraft game = Minecraft.getInstance();
    if (game.player == null || game.level == null || !NetworkAppHeld.by(game.player)) {
      FOUND.clear();
      return;
    }
    if (FloatingName.looksFor(game.level.getGameTime())) {
      lookAround(game.level, game.player);
    }
  }

  @SubscribeEvent
  public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
    FOUND.clear();
  }

  private static void lookAround(ClientLevel level, LocalPlayer player) {
    FOUND.clear();
    int middleX = SectionPos.blockToSectionCoord(player.getBlockX());
    int middleZ = SectionPos.blockToSectionCoord(player.getBlockZ());
    for (int x = middleX - CHUNKS_AROUND; x <= middleX + CHUNKS_AROUND; x++) {
      for (int z = middleZ - CHUNKS_AROUND; z <= middleZ + CHUNKS_AROUND; z++) {
        if (level.hasChunk(x, z)) {
          collect(level.getChunk(x, z).getBlockEntities().values());
        }
      }
    }
  }

  private static void collect(Iterable<BlockEntity> blocks) {
    for (BlockEntity block : blocks) {
      if (block instanceof ShowsName named) {
        FOUND.add(named);
      }
    }
  }

  @SubscribeEvent
  public static void render(RenderLevelStageEvent event) {
    Minecraft game = Minecraft.getInstance();
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL || game.player == null) {
      return;
    }
    List<ShowsName> seen = seen(game, game.player);
    if (!seen.isEmpty()) {
      draw(event, game, seen);
    }
  }

  /** The ones the player sees, the farthest first, so the nearer ones are drawn over them. */
  private static List<ShowsName> seen(Minecraft game, LocalPlayer player) {
    Vec3 eyes = player.getEyePosition();
    boolean tablet = NetworkAppHeld.by(player);
    boolean screen = game.screen != null;
    boolean hidden = game.options.hideGui;
    return FOUND.stream()
        .filter(named -> !named.isRemoved())
        .filter(named -> !covered(named))
        .filter(named ->
            named.nameSpot().seenBy(viewer(named.isOwner(player), tablet, screen, hidden, eyes)))
        .sorted(Comparator.comparingDouble((ShowsName named) -> squaredDistance(named, eyes)))
        .toList()
        .reversed();
  }

  /** A block with an arrow of the Network app, which already shows its name. */
  private static boolean covered(ShowsName named) {
    return named instanceof BlockEntity block && TabletNetworkMarks.covers(block.getBlockPos());
  }

  private static FloatingName.Viewer viewer(
      boolean owner, boolean tablet, boolean screen, boolean hidden, Vec3 eyes) {
    return new FloatingName.Viewer(owner, tablet, screen, hidden, eyes.x, eyes.y, eyes.z);
  }

  private static double squaredDistance(ShowsName named, Vec3 eyes) {
    FloatingName spot = named.nameSpot();
    return eyes.distanceToSqr(spot.x(), spot.y(), spot.z());
  }

  private static void draw(RenderLevelStageEvent event, Minecraft game, List<ShowsName> seen) {
    Matrix4fStack view = RenderSystem.getModelViewStack();
    view.pushMatrix();
    view.mul(event.getModelViewMatrix());
    RenderSystem.applyModelViewMatrix();
    NamePainter painter =
        new NamePainter(game.renderBuffers().bufferSource(), game.font, event.getCamera());
    for (ShowsName named : seen) {
      painter.throughBlocks(named.nameSpot(), named.shownName().value());
    }
    for (ShowsName named : seen) {
      painter.inFront(named.nameSpot(), named.shownName().value());
    }
    view.popMatrix();
    RenderSystem.applyModelViewMatrix();
  }
}
