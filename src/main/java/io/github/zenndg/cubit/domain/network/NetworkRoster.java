package io.github.zenndg.cubit.domain.network;

import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The Network Servers and the Signal Antennas of the world, known also with their chunks unloaded:
 * where each Server is, with the tier that opens its channels, and which channel of which Server
 * each antenna takes (Delivery 4, details 6, 14 and 19).
 *
 * @param <P> the place of a block
 */
public final class NetworkRoster<P> {
  private final Map<UUID, Server<P>> servers = new LinkedHashMap<>();
  private final Map<P, Antenna<P>> antennas = new LinkedHashMap<>();

  /** True when it changed. */
  public boolean putServer(Server<P> server) {
    return !server.equals(servers.put(server.id(), server));
  }

  public boolean removeServer(UUID id) {
    return servers.remove(id) != null;
  }

  public Optional<Server<P>> server(UUID id) {
    return Optional.ofNullable(servers.get(id));
  }

  public Collection<Server<P>> servers() {
    return Collections.unmodifiableCollection(servers.values());
  }

  /** One antenna per place, so the one already there gives its channel back. True when changed. */
  public boolean putAntenna(Antenna<P> antenna) {
    return !antenna.equals(antennas.put(antenna.place(), antenna));
  }

  public boolean removeAntenna(P place) {
    return antennas.remove(place) != null;
  }

  /** The antenna at this place, known also with its chunk unloaded. */
  public Optional<Antenna<P>> antenna(P place) {
    return Optional.ofNullable(antennas.get(place));
  }

  public Collection<Antenna<P>> antennas() {
    return Collections.unmodifiableCollection(antennas.values());
  }

  /** The channels of a Server that its antennas take. */
  public Set<AntennaChannel> taken(UUID server) {
    Set<AntennaChannel> taken = EnumSet.noneOf(AntennaChannel.class);
    antennas.values().stream()
        .filter(antenna -> antenna.server().equals(server))
        .forEach(antenna -> taken.add(antenna.channel()));
    return taken;
  }

  /** The channel a placed antenna takes: its last one while free, or the first free open one. */
  public Optional<AntennaChannel> channelFor(UUID server, Optional<AntennaChannel> last, int open) {
    return AntennaChannel.pick(last, open, taken(server));
  }

  /** A Server, by its own id, with its owner, its place and its tier. */
  public record Server<P>(UUID id, UUID owner, P place, RobotTier tier) {
    public Server {
      Objects.requireNonNull(id);
      Objects.requireNonNull(owner);
      Objects.requireNonNull(place);
      Objects.requireNonNull(tier);
    }
  }

  /** An antenna, by its place, with its owner, its Server and its channel. */
  public record Antenna<P>(P place, UUID owner, UUID server, AntennaChannel channel) {
    public Antenna {
      Objects.requireNonNull(place);
      Objects.requireNonNull(owner);
      Objects.requireNonNull(server);
      Objects.requireNonNull(channel);
    }
  }
}
