package io.github.zenndg.cubit.entity;

import com.mojang.logging.LogUtils;
import io.github.zenndg.cubit.domain.behavior.RobotScreenStatus;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.AreaColumn;
import io.github.zenndg.cubit.domain.robot.AreaPath;
import io.github.zenndg.cubit.domain.robot.AreaShape;
import io.github.zenndg.cubit.domain.robot.WorkArea;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

/**
 * The area the owner drew with the Area app of the Tablet, where the Cubit works instead of
 * following (answers 1051 to 1064). It is saved with the Cubit and lost when it is picked up.
 */
public final class RobotWorkArea {
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String BROKEN = "Cubit {} has a broken area in its save, which is dropped";
  private static final String KEY = "DrawnArea";
  private static final String POINTS_KEY = "Points";
  private static final String LOW_KEY = "Low";
  private static final String HIGH_KEY = "High";
  private static final String DIMENSION_KEY = "Dimension";

  private final RobotEntity robot;
  private final RobotWork work;
  private Optional<WorkArea> area = Optional.empty();

  RobotWorkArea(RobotEntity robot) {
    this.robot = robot;
    this.work = new RobotWork(robot);
  }

  /** The job from the card and the hibernation away from the Dock (answers 1162 and 1164). */
  public RobotWork work() {
    return work;
  }

  public Optional<WorkArea> area() {
    return area;
  }

  /** The area in the dimension where the Cubit is, the only one it can work in. */
  public Optional<WorkArea> here() {
    String dimension = robot.level().dimension().location().toString();
    return area.filter(found -> found.isIn(dimension));
  }

  /** Active in its area, so it works there instead of following (answer 1061). */
  public boolean working() {
    return robot.powerState() == PowerState.ACTIVE && here().isPresent();
  }

  /** A new area replaces the old one, and the Cubit gives up the place to follow (1051, 1060). */
  public void assign(WorkArea next, Player owner) {
    area = Optional.of(next);
    RobotFollowing.release(robot, owner);
    robot.getNavigation().stop();
  }

  /** `Follow` takes the Cubit out of its area (answer 1055). */
  public void clear() {
    area = Optional.empty();
  }

  /**
   * The owner erased the area: the Cubit stops and, following nobody, switches off as any other
   * without an area (answers 2299 and 2353).
   */
  public void erase() {
    work.wake();
    work.escort().stop();
    clear();
    robot.getNavigation().stop();
    if (robot.powerState() == PowerState.ACTIVE) {
      robot.setPowerState(PowerState.INACTIVE_SWITCHED_OFF);
    }
  }

  RobotScreenStatus.AreaWork areaStatus() {
    return new RobotScreenStatus.AreaWork(
        working(), robot.robotInventory().isFull(), work.job().isPresent());
  }

  void save(CompoundTag tag) {
    work.save(tag);
    area.ifPresent(found -> {
      CompoundTag saved = new CompoundTag();
      saved.putLongArray(
          POINTS_KEY,
          found.shape().corners().stream()
              .mapToLong(corner -> BlockPos.asLong(corner.x(), found.low(), corner.z()))
              .toArray());
      saved.putInt(LOW_KEY, found.low());
      saved.putInt(HIGH_KEY, found.high());
      saved.putString(DIMENSION_KEY, found.dimension());
      tag.put(KEY, saved);
    });
  }

  /**
   * A save without the key has no area, and one with a broken one drops it (rule 17). The box of
   * before the drawing, in its own key, is not read (answer 2283).
   */
  void load(CompoundTag tag) {
    work.load(tag);
    area = tag.contains(KEY) ? read(tag.getCompound(KEY)) : Optional.empty();
  }

  private Optional<WorkArea> read(CompoundTag saved) {
    String dimension = saved.getString(DIMENSION_KEY);
    int low = saved.getInt(LOW_KEY);
    int high = saved.getInt(HIGH_KEY);
    List<AreaColumn> points =
        Arrays.stream(saved.getLongArray(POINTS_KEY)).mapToObj(RobotWorkArea::column).toList();
    Optional<WorkArea> found =
        low <= high && !dimension.isBlank()
            ? AreaShape.of(new AreaPath(points))
                .map(shape -> new WorkArea(shape, low, high, dimension))
            : Optional.empty();
    if (found.isEmpty()) {
      LOGGER.warn(BROKEN, robot.getUUID());
    }
    return found;
  }

  private static AreaColumn column(long packed) {
    return new AreaColumn(BlockPos.getX(packed), BlockPos.getZ(packed));
  }
}
