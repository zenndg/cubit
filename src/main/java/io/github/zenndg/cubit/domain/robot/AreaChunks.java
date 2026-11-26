package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.network.LinkChunk;
import java.util.Collection;
import java.util.Set;

/**
 * Where a drawing may go: anywhere for a Cubit without antennas, as before the Network, or only
 * inside the chunks of its antennas, in the dimension of the drawing (answers 2586 and 2601).
 */
public record AreaChunks(boolean anywhere, String dimension, Set<LinkChunk> chunks) {
  public static final AreaChunks ANYWHERE = new AreaChunks(true, "", Set.of());

  public AreaChunks {
    chunks = Set.copyOf(chunks);
  }

  /** A Cubit with at least one antenna, in any dimension, draws only inside their chunks. */
  public static AreaChunks of(String dimension, Collection<LinkChunk> antennas) {
    return antennas.isEmpty() ? ANYWHERE : new AreaChunks(false, dimension, Set.copyOf(antennas));
  }

  public boolean holds(AreaColumn column) {
    return anywhere || chunks.contains(LinkChunk.of(dimension, column.x(), column.z()));
  }

  /** Every block of a straight line, ends included. */
  public boolean holdsLine(AreaColumn from, AreaColumn to) {
    int stepX = Integer.signum(to.x() - from.x());
    int stepZ = Integer.signum(to.z() - from.z());
    AreaColumn at = from;
    while (!at.equals(to)) {
      if (!holds(at)) {
        return false;
      }
      at = new AreaColumn(at.x() + stepX, at.z() + stepZ);
    }
    return holds(to);
  }

  /** Every column of a closed shape, its inside too, so no chunk without an antenna is left. */
  public boolean holdsShape(AreaShape shape) {
    if (anywhere) {
      return true;
    }
    for (int index = 0; index < shape.columnCount(); index++) {
      if (!holds(shape.column(index))) {
        return false;
      }
    }
    return true;
  }
}
