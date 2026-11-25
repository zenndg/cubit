package io.github.zenndg.cubit.domain.network;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Which chunks the mod holds, of which player, in the order they came, and which ones wait for
 * their owner to come back (answers 2210 and 2215). Who came first stays inside the limits.
 *
 * @param <P> the place of the block that holds the chunk
 */
public final class ChunkRoster<P> {
  private final Map<P, Entry<P>> entries = new LinkedHashMap<>();

  /** True when it changed. */
  public boolean hold(P place, UUID owner) {
    Entry<P> next = new Entry<>(place, owner, false);
    return !next.equals(entries.put(place, next));
  }

  /** Nothing is held while the owner is away, and the place in line is kept. */
  public boolean await(P place) {
    Entry<P> entry = entries.get(place);
    if (entry == null || entry.waiting()) {
      return false;
    }
    entries.put(place, new Entry<>(place, entry.owner(), true));
    return true;
  }

  public boolean release(P place) {
    return entries.remove(place) != null;
  }

  public boolean holds(P place) {
    Entry<P> entry = entries.get(place);
    return entry != null && !entry.waiting();
  }

  public int heldBy(UUID owner) {
    return count(owner, null).ofPlayer();
  }

  public List<P> waitingOf(UUID owner) {
    return entries.values().stream()
        .filter(entry -> entry.waiting() && entry.owner().equals(owner))
        .map(Entry::place)
        .toList();
  }

  /** Whether the limits leave room for this place, with what came before it. */
  public ChunkCall.Room room(P place, UUID owner, ChunkLimits limits) {
    Count others = count(owner, holds(place) ? place : null);
    return new ChunkCall.Room(
        limits.fitsPlayer(others.ofPlayer()), limits.fitsWorld(others.ofWorld()));
  }

  /** All of them, or only the ones that came before a place. */
  private Count count(UUID owner, P before) {
    int ofPlayer = 0;
    int ofWorld = 0;
    for (Entry<P> entry : entries.values()) {
      if (entry.place().equals(before)) {
        break;
      }
      if (!entry.waiting()) {
        ofWorld++;
        ofPlayer += entry.owner().equals(owner) ? 1 : 0;
      }
    }
    return new Count(ofPlayer, ofWorld);
  }

  public List<Entry<P>> entries() {
    return new ArrayList<>(entries.values());
  }

  public void restore(Entry<P> entry) {
    entries.put(entry.place(), entry);
  }

  /** A chunk held, or kept for when its owner comes back. */
  public record Entry<P>(P place, UUID owner, boolean waiting) {}

  private record Count(int ofPlayer, int ofWorld) {}
}
