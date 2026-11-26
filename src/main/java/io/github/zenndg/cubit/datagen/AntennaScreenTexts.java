package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.menu.AntennaScreenKeys;
import java.util.function.BiConsumer;

/** The screen of the Signal Antenna, in English, the base language. */
final class AntennaScreenTexts {
  private static final String RENAME = "Rename the Signal Antenna";
  private static final String CHANNEL = "Channel";
  private static final String SERVER = "Server";
  private static final String SERVER_VALUE = "%s · %s";
  private static final String NO_SERVER = "No Server";
  private static final String SIGNAL = "Signal";
  private static final String LINKED = "Linked";
  private static final String NO_SIGNAL = "No signal";
  private static final String NO_SIGNAL_LEFT = "No signal · stops in %s min";
  private static final String STOPPED = "Stopped";
  private static final String STOPPED_LINE = "Stopped · no signal";
  private static final String OFF = "Off";
  private static final String SPENDING = "Spending";
  private static final String SPEND_VALUE = "%s blocks · −%s FE/t";
  private static final String SPEND_NONE = "%s blocks · 0 FE/t";
  private static final String ON = "On";
  private static final String SHOW_WALL = "Show the wall";
  private static final String LINKS = "Links";
  private static final String LINKS_VALUE = "Cubits: %s · Docks: %s";
  private static final String LINKS_NONE = "none";
  private static final String ANTENNA_OFF = "Released: antenna off";

  private AntennaScreenTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(AntennaScreenKeys.RENAME, RENAME);
    add.accept(AntennaScreenKeys.CHANNEL, CHANNEL);
    add.accept(AntennaScreenKeys.SERVER, SERVER);
    add.accept(AntennaScreenKeys.SERVER_VALUE, SERVER_VALUE);
    add.accept(AntennaScreenKeys.NO_SERVER, NO_SERVER);
    add.accept(AntennaScreenKeys.SIGNAL, SIGNAL);
    add.accept(AntennaScreenKeys.LINKED, LINKED);
    add.accept(AntennaScreenKeys.NO_SIGNAL, NO_SIGNAL);
    add.accept(AntennaScreenKeys.NO_SIGNAL_LEFT, NO_SIGNAL_LEFT);
    add.accept(AntennaScreenKeys.STOPPED, STOPPED);
    add.accept(AntennaScreenKeys.STOPPED_LINE, STOPPED_LINE);
    add.accept(AntennaScreenKeys.OFF, OFF);
    add.accept(AntennaScreenKeys.SPENDING, SPENDING);
    add.accept(AntennaScreenKeys.SPEND_VALUE, SPEND_VALUE);
    add.accept(AntennaScreenKeys.SPEND_NONE, SPEND_NONE);
    add.accept(AntennaScreenKeys.ON, ON);
    add.accept(AntennaScreenKeys.SHOW_WALL, SHOW_WALL);
    add.accept(AntennaScreenKeys.LINKS, LINKS);
    add.accept(AntennaScreenKeys.LINKS_VALUE, LINKS_VALUE);
    add.accept(AntennaScreenKeys.LINKS_NONE, LINKS_NONE);
    add.accept(AntennaScreenKeys.ANTENNA_OFF, ANTENNA_OFF);
  }
}
