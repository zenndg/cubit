package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.ServerStatus;
import java.util.Locale;

/** The translation keys of the screens of the Network, with those of the language file. */
public final class NetworkKeys {
  private static final String PREFIX = "gui.cubit.network.";
  private static final String STATUS_PREFIX = PREFIX + "status.";
  private static final String CHUNK_PREFIX = PREFIX + "chunk_state.";
  private static final String SKY_PREFIX = PREFIX + "sky.";
  private static final String PANEL_PREFIX = PREFIX + "panel.";

  public static final String TAB_ENERGY = PREFIX + "tab_energy";
  public static final String RENAME_SERVER = PREFIX + "rename_server";
  public static final String RENAME_RELAY = PREFIX + "rename_relay";
  public static final String FLOW = PREFIX + "flow";
  public static final String SUN = PREFIX + "sun";
  public static final String SUN_RATE = PREFIX + "sun_rate";
  public static final String CHUNK_COST = PREFIX + "chunk_cost";
  public static final String COST_CHUNK = PREFIX + "cost_chunk";
  public static final String COST_SERVER = PREFIX + "cost_server";
  public static final String COST_RELAY = PREFIX + "cost_relay";
  public static final String BANKS = PREFIX + "banks";
  public static final String ANTENNAS = PREFIX + "antennas";
  public static final String ANTENNAS_TIP = PREFIX + "antennas_tip";
  public static final String GAIN = PREFIX + "gain";
  public static final String LOSS = PREFIX + "loss";
  public static final String NOTHING = PREFIX + "nothing";
  public static final String LOAD_CHUNK = PREFIX + "load_chunk";
  public static final String VIEW = PREFIX + "view";
  public static final String VIEW_TIP = PREFIX + "view_tip";
  public static final String HOLDING_WITHOUT_LIMIT = PREFIX + "holding_without_limit";
  public static final String UPS = PREFIX + "ups";
  public static final String UPS_VALUE = PREFIX + "ups_value";
  public static final String HOLDS_MINUTES = PREFIX + "holds_minutes";
  public static final String HOLDS_HOURS = PREFIX + "holds_hours";
  public static final String MAY_DROP = PREFIX + "may_drop";
  public static final String NONE = PREFIX + "none";
  public static final String USED_SLOTS = PREFIX + "used_slots";
  public static final String SLOT_RULE = PREFIX + "slot_rule";
  public static final String BANK_CHARGING = PREFIX + "bank_charging";
  public static final String BANK_WAITING = PREFIX + "bank_waiting";
  public static final String GENERATION = PREFIX + "generation";
  public static final String SKY = PREFIX + "sky";
  public static final String NOW = PREFIX + "now";
  public static final String TIER_MOST = PREFIX + "tier_most";
  public static final String PANEL = PREFIX + "panel";
  public static final String RATE = PREFIX + "rate";
  public static final String PAIR = PREFIX + "pair";
  public static final String PAIRED = PREFIX + "paired";
  public static final String NO_SERVER = PREFIX + "no_server";
  public static final String GENERATING = PREFIX + "generating";
  public static final String STOPPED = PREFIX + "stopped";
  public static final String NO_PAIR = PREFIX + "no_pair";

  private NetworkKeys() {}

  public static String status(ServerStatus status) {
    return STATUS_PREFIX + status.name().toLowerCase(Locale.ROOT);
  }

  public static String chunk(ChunkState state) {
    return CHUNK_PREFIX + state.name().toLowerCase(Locale.ROOT);
  }

  public static String panel(PanelState state) {
    return PANEL_PREFIX + state.name().toLowerCase(Locale.ROOT);
  }

  public static String sky(SunState state) {
    return SKY_PREFIX + state.name().toLowerCase(Locale.ROOT);
  }
}
