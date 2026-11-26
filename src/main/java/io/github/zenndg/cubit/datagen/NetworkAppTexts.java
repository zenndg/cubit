package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.player.NetworkAppKeys;
import java.util.function.BiConsumer;

/** The Network app of the Tablet, in English, the base language (answers 2588 to 2604). */
final class NetworkAppTexts {
  private static final String APP = "Network";
  private static final String PICK = "Click your Cubit, or an antenna and then a Dock";
  private static final String ONLY_YOURS = "Only your own Cubits, antennas and Docks";
  private static final String ONLY_BLOCKS = "Click an antenna, a Dock or a block with an inventory";
  private static final String CHOSEN = "%s chosen: click its antennas, Docks and chests";
  private static final String NOT_LINKED = "This antenna is not linked to a Server";
  private static final String ANTENNA_LINKED = "%s linked to %s";
  private static final String ANTENNA_CONFIRM = "Click %s again to unlink it from %s";
  private static final String ANTENNA_CONFIRM_AREA = "Click %s again: %s also loses its area";
  private static final String ANTENNA_UNLINKED = "%s unlinked from %s";
  private static final String ANTENNA_UNLINKED_AREA = "%s unlinked, and %s lost its area";
  private static final String ANTENNA_FULL = "%s links up to %s antennas";
  private static final String OTHER_SERVER = "All the antennas of %s are of one Server";
  private static final String NOT_BESIDE = "It must share a side with an antenna of %s";
  private static final String SPLITS = "Unlink the antennas at the ends first";
  private static final String OUTSIDE = "Outside the chunks of the antennas of %s";
  private static final String DOCK_MARKED = "%s marked for %s";
  private static final String DOCK_UNMARKED = "%s unmarked from %s, still fed";
  private static final String DOCK_FULL = "%s uses up to %s Docks";
  private static final String CHEST_MARKED = "Chest marked for %s";
  private static final String CHEST_UNMARKED = "Chest unmarked from %s";
  private static final String CHEST_FULL = "%s uses up to %s chests";
  private static final String FEED_NEXT = "Now click a Dock in the chunk of %s";
  private static final String FED = "%s now feeds %s";
  private static final String FEED_CONFIRM = "Click %s again to cut its energy: %s loaded Cubits";
  private static final String UNFED = "%s no longer feeds %s";
  private static final String NOT_IN_CHUNK = "%s is not in the chunk of %s";

  private NetworkAppTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(TabletItem.appKey(TabletApp.NETWORK), APP);
    add.accept(NetworkAppKeys.PICK, PICK);
    add.accept(NetworkAppKeys.ONLY_YOURS, ONLY_YOURS);
    add.accept(NetworkAppKeys.ONLY_BLOCKS, ONLY_BLOCKS);
    add.accept(NetworkAppKeys.CHOSEN, CHOSEN);
    add.accept(NetworkAppKeys.NOT_LINKED, NOT_LINKED);
    add.accept(NetworkAppKeys.ANTENNA_LINKED, ANTENNA_LINKED);
    add.accept(NetworkAppKeys.ANTENNA_CONFIRM, ANTENNA_CONFIRM);
    add.accept(NetworkAppKeys.ANTENNA_CONFIRM_AREA, ANTENNA_CONFIRM_AREA);
    add.accept(NetworkAppKeys.ANTENNA_UNLINKED, ANTENNA_UNLINKED);
    add.accept(NetworkAppKeys.ANTENNA_UNLINKED_AREA, ANTENNA_UNLINKED_AREA);
    add.accept(NetworkAppKeys.ANTENNA_FULL, ANTENNA_FULL);
    add.accept(NetworkAppKeys.OTHER_SERVER, OTHER_SERVER);
    add.accept(NetworkAppKeys.NOT_BESIDE, NOT_BESIDE);
    add.accept(NetworkAppKeys.SPLITS, SPLITS);
    add.accept(NetworkAppKeys.OUTSIDE, OUTSIDE);
    add.accept(NetworkAppKeys.DOCK_MARKED, DOCK_MARKED);
    add.accept(NetworkAppKeys.DOCK_UNMARKED, DOCK_UNMARKED);
    add.accept(NetworkAppKeys.DOCK_FULL, DOCK_FULL);
    add.accept(NetworkAppKeys.CHEST_MARKED, CHEST_MARKED);
    add.accept(NetworkAppKeys.CHEST_UNMARKED, CHEST_UNMARKED);
    add.accept(NetworkAppKeys.CHEST_FULL, CHEST_FULL);
    add.accept(NetworkAppKeys.FEED_NEXT, FEED_NEXT);
    add.accept(NetworkAppKeys.FED, FED);
    add.accept(NetworkAppKeys.FEED_CONFIRM, FEED_CONFIRM);
    add.accept(NetworkAppKeys.UNFED, UNFED);
    add.accept(NetworkAppKeys.NOT_IN_CHUNK, NOT_IN_CHUNK);
  }
}
