package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.energy.Energy;
import java.util.ArrayList;
import java.util.List;

/**
 * How a Network Server shares what it may send in a tick among its antennas: first what each one
 * spends on its chunk, in the order of the channels, and then the rest in equal parts, each up to
 * the room it has (answers 2302 and 2551). What one cannot take goes to the others, and the shares
 * never add up to more than what was offered (rule 13).
 */
public final class EnergyShare {
  private EnergyShare() {}

  /**
   * The share of each antenna, in the order given, which is the order of the channels.
   *
   * @param offered what the Server may send in this tick
   * @param needs what each antenna spends on its chunk and the room it has this tick
   */
  public static List<Energy> of(Energy offered, List<Need> needs) {
    long[] given = new long[needs.size()];
    long left = chunksFirst(offered.amount(), needs, given);
    evenly(left, needs.stream().mapToLong(need -> need.room().amount()).toArray(), given);
    List<Energy> shares = new ArrayList<>(needs.size());
    for (long amount : given) {
      shares.add(new Energy(amount));
    }
    return List.copyOf(shares);
  }

  private static long chunksFirst(long left, List<Need> needs, long[] given) {
    for (int index = 0; index < needs.size() && left > 0; index++) {
      Need need = needs.get(index);
      long take = Math.min(left, Math.min(need.chunk().amount(), need.room().amount()));
      given[index] = take;
      left -= take;
    }
    return left;
  }

  /**
   * Equal parts in rounds, each up to its room, so what a full one leaves goes to the others. It
   * adds to what was already given and returns what is left (rule 13).
   */
  static long evenly(long offered, long[] rooms, long[] given) {
    long left = offered;
    while (left > 0) {
      int hungry = 0;
      for (int index = 0; index < rooms.length; index++) {
        hungry += given[index] < rooms[index] ? 1 : 0;
      }
      if (hungry == 0) {
        return left;
      }
      long part = Math.max(1, left / hungry);
      for (int index = 0; index < rooms.length && left > 0; index++) {
        long take = Math.min(part, Math.min(left, Math.max(0, rooms[index] - given[index])));
        given[index] += take;
        left -= take;
      }
    }
    return left;
  }

  /** What an antenna spends on its chunk in this tick and how much more it can take. */
  public record Need(Energy chunk, Energy room) {}
}
