package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.menu.AntennaScreenKeys;
import java.util.function.BiConsumer;

/** The screen of the Signal Antenna, in Brazilian Portuguese. */
final class PortugueseAntennaScreenTexts {
  private static final String RENAME = "Renomear a Antena de Sinal";
  private static final String CHANNEL = "Canal";
  private static final String SERVER = "Servidor";
  private static final String SERVER_VALUE = "%s · %s";
  private static final String NO_SERVER = "Sem Servidor";
  private static final String SIGNAL = "Sinal";
  private static final String LINKED = "Ligada";
  private static final String NO_SIGNAL = "Sem sinal";
  private static final String NO_SIGNAL_LEFT = "Sem sinal · para em %s min";
  private static final String STOPPED = "Parada";
  private static final String STOPPED_LINE = "Parada · sem sinal";
  private static final String OFF = "Desligada";
  private static final String SPENDING = "Gasto";
  private static final String SPEND_VALUE = "%s blocos · −%s FE/t";
  private static final String SPEND_NONE = "%s blocos · 0 FE/t";
  private static final String ON = "Ligada";
  private static final String SHOW_WALL = "Mostrar a parede";
  private static final String LINKS = "Ligações";
  private static final String LINKS_VALUE = "Cubits: %s · Docks: %s";
  private static final String LINKS_NONE = "nenhuma";
  private static final String ANTENNA_OFF = "Solto: antena desligada";

  private PortugueseAntennaScreenTexts() {}

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
