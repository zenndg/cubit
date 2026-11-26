package io.github.zenndg.cubit.domain.network;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * The Docks an antenna feeds, in the order they came (answer 2589). Marking a Dock for a Cubit also
 * feeds it, and unmarking it keeps it fed: only the click on the antenna and then on the Dock,
 * without a Cubit, takes it out.
 */
public record FedDocks(List<LinkSpot> docks) {
  public static final FedDocks NONE = new FedDocks(List.of());

  public FedDocks {
    docks = List.copyOf(new LinkedHashSet<>(docks));
  }

  public FedDocks with(LinkSpot dock) {
    if (docks.contains(dock)) {
      return this;
    }
    List<LinkSpot> next = new ArrayList<>(docks);
    next.add(dock);
    return new FedDocks(next);
  }

  public FedDocks without(LinkSpot dock) {
    List<LinkSpot> next = new ArrayList<>(docks);
    return next.remove(dock) ? new FedDocks(next) : this;
  }

  public boolean feeds(LinkSpot dock) {
    return docks.contains(dock);
  }
}
