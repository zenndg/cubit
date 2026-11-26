package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;
import java.util.ArrayList;
import java.util.List;

/**
 * How an antenna feeds its Docks: only what passes its UPS and the floor no Dock takes, first to
 * the Docks charging a Cubit and then evenly, each up to the room it has (answers 2302, 2322, 2587
 * and 2599). The shares never add up to more than what was offered (rule 13).
 */
public final class DockFeed {
  private DockFeed() {}

  /** What the antenna may give its Docks: above the larger of its UPS and its floor. */
  public static Energy spare(Energy charge, Energy capacity, UpsReserve ups, Percentage floor) {
    long kept =
        Math.max(ups.of(capacity).amount(), capacity.amount() * floor.value() / Percentage.MAX);
    return charge.amount() > kept ? new Energy(charge.amount() - kept) : Energy.ZERO;
  }

  /** The share of each Dock, in the order given. */
  public static List<Energy> of(Energy offered, List<Need> needs) {
    long[] given = new long[needs.size()];
    long left = EnergyShare.evenly(offered.amount(), rooms(needs, true), given);
    EnergyShare.evenly(left, rooms(needs, false), given);
    List<Energy> shares = new ArrayList<>(needs.size());
    for (long amount : given) {
      shares.add(new Energy(amount));
    }
    return List.copyOf(shares);
  }

  /** The room of the Docks of one kind, and none for the others. */
  private static long[] rooms(List<Need> needs, boolean charging) {
    long[] rooms = new long[needs.size()];
    for (int index = 0; index < needs.size(); index++) {
      Need need = needs.get(index);
      rooms[index] = need.charging() == charging ? need.room().amount() : 0;
    }
    return rooms;
  }

  /** Whether a Dock charges a Cubit now, and how much more it takes in this tick. */
  public record Need(boolean charging, Energy room) {}
}
