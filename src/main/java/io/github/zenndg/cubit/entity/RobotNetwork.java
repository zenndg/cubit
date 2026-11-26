package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.LinkSpots;
import io.github.zenndg.cubit.domain.network.LinkLimits;
import io.github.zenndg.cubit.domain.network.LinkSpot;
import io.github.zenndg.cubit.domain.network.NetworkLinks;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/**
 * What the Network app links to the Cubit: its antennas, Docks and chests (answers 2585 and 2588),
 * in its NBT only. Picking it up and death leave them behind, as the other marks in the world
 * (answers 2251 and 2304). Using only the marked ones comes in Delivery 6 (answer 2585).
 */
public final class RobotNetwork {
  private static final String ANTENNAS_KEY = "NetworkAntennas";
  private static final String DOCKS_KEY = "NetworkDocks";
  private static final String CHESTS_KEY = "NetworkChests";
  private static final String DIMENSION_KEY = "Dimension";
  private static final String POS_KEY = "Pos";

  private final RobotEntity robot;
  private NetworkLinks links = NetworkLinks.NONE;

  RobotNetwork(RobotEntity robot) {
    this.robot = robot;
  }

  /**
   * Its links, with only the first chests up to the limit it has now: a card taken out of an
   * Organizer leaves the others out (answer 2602).
   */
  public NetworkLinks links() {
    return links.chestsFitting(chestLimit());
  }

  public void set(NetworkLinks next) {
    links = next;
  }

  /** What its tier links, with more chests for an Organizer (answer 2602). */
  public LinkLimits limits() {
    return CubitMod.CONFIG.linkCatalog().links(robot.state().tier());
  }

  public int chestLimit() {
    return limits().chestsFor(organizer());
  }

  /** Whether this Dock, in the dimension of the Cubit, is marked for it. */
  public boolean marksDock(BlockPos dock) {
    return links().docks().contains(LinkSpots.of(robot.level(), dock));
  }

  private boolean organizer() {
    return robot.workArea().work().job().filter(RobotJob.ORGANIZER::equals).isPresent();
  }

  void save(CompoundTag tag) {
    NetworkLinks kept = links();
    put(tag, ANTENNAS_KEY, kept.antennas());
    put(tag, DOCKS_KEY, kept.docks());
    put(tag, CHESTS_KEY, kept.chests());
  }

  void load(CompoundTag tag) {
    links = new NetworkLinks(read(tag, ANTENNAS_KEY), read(tag, DOCKS_KEY), read(tag, CHESTS_KEY));
  }

  private static void put(CompoundTag tag, String key, List<LinkSpot> spots) {
    if (spots.isEmpty()) {
      return;
    }
    ListTag list = new ListTag();
    for (LinkSpot spot : spots) {
      CompoundTag entry = new CompoundTag();
      entry.putString(DIMENSION_KEY, spot.dimension());
      entry.putLong(POS_KEY, LinkSpots.pos(spot).asLong());
      list.add(entry);
    }
    tag.put(key, list);
  }

  /** A broken entry is skipped (rule 17). */
  private static List<LinkSpot> read(CompoundTag tag, String key) {
    List<LinkSpot> spots = new ArrayList<>();
    for (Tag entry : tag.getList(key, Tag.TAG_COMPOUND)) {
      spotOf((CompoundTag) entry).ifPresent(spots::add);
    }
    return spots;
  }

  private static Optional<LinkSpot> spotOf(CompoundTag saved) {
    String dimension = saved.getString(DIMENSION_KEY);
    if (dimension.isBlank() || !saved.contains(POS_KEY, Tag.TAG_LONG)) {
      return Optional.empty();
    }
    BlockPos pos = BlockPos.of(saved.getLong(POS_KEY));
    return Optional.of(new LinkSpot(dimension, pos.getX(), pos.getY(), pos.getZ()));
  }
}
