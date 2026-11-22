package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.entity.RobotAutoReturn;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.menu.DefenseKeys;
import io.github.zenndg.cubit.menu.TabletKeys;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.registry.CubitDamageTypes;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.function.BiConsumer;

/** The Galactic Patroller, in English, the base language (answers 1550 to 1592, 1610 and 1611). */
final class PatrollerTexts {
  private static final String JOB = "Galactic Patroller";
  private static final String LASER_DEATH = "%1$s was hit by the laser orb of %2$s";
  private static final String BLADE_DEATH = "%1$s was struck by the Plasma Blade of %2$s";
  private static final String CARD = "Machine Learning: Galactic Patroller";

  private static final String LOCKED = "Locked by the Galactic Patroller card";
  private static final String POINT = "Point %s of %s marked for %s";
  private static final String NO_AREA = "Mark the area first with the Area app";
  private static final String OUTSIDE = "Only inside the area of %s";
  private static final String NOT_SOLID = "Mark the top of a solid block";
  private static final String ESCORT_APP = "Escort";
  private static final String ESCORTING = "%s is escorting %s";
  private static final String ESCORT_STOPPED = "%s stopped the escort";
  private static final String NOT_ESCORTABLE = "%s attacks this mob, so it cannot escort it";
  private static final String ESCORTING_YOU = "%s is escorting you";
  private static final String STOPPED_YOU = "%s stopped escorting you";
  private static final String HEADING = "%s is low on power, heading to the Dock";
  private static final String NO_DOCK = "%s is low on power and has no Dock";

  private PatrollerTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(WorkKeys.job(RobotJob.PATROLLER), JOB);
    add.accept(CubitItems.PATROLLER_CARD.get().getDescriptionId(), CARD);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.LASER_ORB_MESSAGE_ID, LASER_DEATH);
    add.accept(DefenseTexts.DEATH_PREFIX + CubitDamageTypes.PLASMA_BLADE_MESSAGE_ID, BLADE_DEATH);
    add.accept(DefenseKeys.LOCKED_BY_CARD, LOCKED);
    add.accept(TabletKeys.ROUTE_POINT, POINT);
    add.accept(TabletKeys.ROUTE_NO_AREA, NO_AREA);
    add.accept(TabletKeys.ROUTE_OUTSIDE_AREA, OUTSIDE);
    add.accept(TabletKeys.ROUTE_NOT_SOLID, NOT_SOLID);
    add.accept(RobotAutoReturn.HEADING_KEY, HEADING);
    add.accept(RobotAutoReturn.NO_DOCK_KEY, NO_DOCK);
    add.accept(TabletItem.appKey(TabletApp.ESCORT), ESCORT_APP);
    add.accept(TabletKeys.ESCORTING, ESCORTING);
    add.accept(TabletKeys.ESCORT_STOPPED, ESCORT_STOPPED);
    add.accept(TabletKeys.NOT_ESCORTABLE, NOT_ESCORTABLE);
    add.accept(TabletKeys.ESCORTING_YOU, ESCORTING_YOU);
    add.accept(TabletKeys.STOPPED_ESCORTING_YOU, STOPPED_YOU);
  }
}
