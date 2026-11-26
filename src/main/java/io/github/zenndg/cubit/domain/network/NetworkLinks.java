package io.github.zenndg.cubit.domain.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * What the Network app links to a Cubit: its antennas, all of one Server and in chunks that share a
 * side, and the Docks and chests marked for it inside their chunks (answers 2249, 2257, 2258, 2588
 * and 2600). Each list keeps the order of the clicks.
 */
public record NetworkLinks(List<LinkSpot> antennas, List<LinkSpot> docks, List<LinkSpot> chests) {
  public static final NetworkLinks NONE = new NetworkLinks(List.of(), List.of(), List.of());

  /** What a click did, or why it did nothing. */
  public enum Step {
    LINKED,
    UNLINKED,
    CONFIRM,
    FULL,
    OTHER_SERVER,
    NOT_BESIDE,
    SPLITS,
    OUTSIDE
  }

  /** The links after a click, and what the click did. */
  public record Click(NetworkLinks links, Step step) {}

  /** A spot saved twice counts once (rule 17). */
  public NetworkLinks {
    antennas = distinct(antennas);
    docks = distinct(docks);
    chests = distinct(chests);
  }

  /**
   * A click on an antenna of the owner. A new one links while the tier allows, from the Server of
   * the others and beside one of them. A linked one asks for the second click, unless taking it out
   * would split the others in two (answer 2590).
   *
   * @param server the Server of the antenna clicked
   * @param linked the Server of the antennas already linked, when one of them still knows it
   */
  public Click antenna(LinkSpot antenna, UUID server, Optional<UUID> linked, int limit) {
    if (antennas.contains(antenna)) {
      return new Click(this, splits(antenna) ? Step.SPLITS : Step.CONFIRM);
    }
    if (antennas.size() >= limit) {
      return new Click(this, Step.FULL);
    }
    if (linked.filter(other -> !other.equals(server)).isPresent()) {
      return new Click(this, Step.OTHER_SERVER);
    }
    if (!antennas.isEmpty() && antennas.stream().noneMatch(next -> besides(next, antenna))) {
      return new Click(this, Step.NOT_BESIDE);
    }
    return new Click(new NetworkLinks(added(antennas, antenna), docks, chests), Step.LINKED);
  }

  /** The confirmed antenna leaves, and the Docks and chests of its chunk with it (answer 2600). */
  public NetworkLinks without(LinkSpot antenna) {
    LinkChunk chunk = antenna.chunk();
    List<LinkSpot> left = new ArrayList<>(antennas);
    left.remove(antenna);
    return new NetworkLinks(left, outside(docks, chunk), outside(chests, chunk));
  }

  /** A click on a Dock of the owner: marks it inside the chunks while the tier allows, or not. */
  public Click dock(LinkSpot dock, int limit) {
    Marked marked = mark(docks, dock, limit);
    return new Click(new NetworkLinks(antennas, marked.spots(), chests), marked.step());
  }

  /** A click on a block with an inventory the owner opens, as a Dock. */
  public Click chest(LinkSpot chest, int limit) {
    Marked marked = mark(chests, chest, limit);
    return new Click(new NetworkLinks(antennas, docks, marked.spots()), marked.step());
  }

  /** A lower limit, as a card taken out of an Organizer, keeps the first chests (answer 2602). */
  public NetworkLinks chestsFitting(int limit) {
    return chests.size() <= limit
        ? this
        : new NetworkLinks(antennas, docks, chests.subList(0, Math.max(limit, 0)));
  }

  /** The chunks of the antennas, where the Cubit may work. */
  public Set<LinkChunk> chunks() {
    return antennas.stream().map(LinkSpot::chunk).collect(Collectors.toUnmodifiableSet());
  }

  public boolean covers(LinkSpot spot) {
    return chunks().contains(spot.chunk());
  }

  public boolean isEmpty() {
    return antennas.isEmpty() && docks.isEmpty() && chests.isEmpty();
  }

  private Marked mark(List<LinkSpot> spots, LinkSpot spot, int limit) {
    if (spots.contains(spot)) {
      List<LinkSpot> left = new ArrayList<>(spots);
      left.remove(spot);
      return new Marked(left, Step.UNLINKED);
    }
    if (!covers(spot)) {
      return new Marked(spots, Step.OUTSIDE);
    }
    return spots.size() >= limit
        ? new Marked(spots, Step.FULL)
        : new Marked(added(spots, spot), Step.LINKED);
  }

  /** Whether the antennas left without this one would not all reach each other by their sides. */
  private boolean splits(LinkSpot antenna) {
    List<LinkSpot> left = new ArrayList<>(antennas);
    left.remove(antenna);
    if (left.size() < 2) {
      return false;
    }
    Set<LinkSpot> reached = new LinkedHashSet<>();
    Deque<LinkSpot> next = new ArrayDeque<>(List.of(left.getFirst()));
    while (!next.isEmpty()) {
      LinkSpot at = next.pop();
      if (reached.add(at)) {
        left.stream().filter(other -> besides(at, other)).forEach(next::push);
      }
    }
    return reached.size() < left.size();
  }

  private static boolean besides(LinkSpot one, LinkSpot other) {
    return one.chunk().besides(other.chunk());
  }

  private static List<LinkSpot> outside(List<LinkSpot> spots, LinkChunk chunk) {
    return spots.stream().filter(spot -> !spot.chunk().equals(chunk)).toList();
  }

  private static List<LinkSpot> added(List<LinkSpot> spots, LinkSpot spot) {
    List<LinkSpot> next = new ArrayList<>(spots);
    next.add(spot);
    return next;
  }

  private static List<LinkSpot> distinct(List<LinkSpot> spots) {
    return List.copyOf(new LinkedHashSet<>(spots));
  }

  private record Marked(List<LinkSpot> spots, Step step) {}
}
