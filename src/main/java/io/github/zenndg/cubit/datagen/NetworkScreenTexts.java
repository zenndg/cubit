package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.ServerStatus;
import io.github.zenndg.cubit.menu.NetworkKeys;
import java.util.function.BiConsumer;

/** The screens of the Network, in English, the base language (answer 2397). */
final class NetworkScreenTexts {
  private static final String TAB_ENERGY = "Energy";
  private static final String RENAME_SERVER = "Rename the Server";
  private static final String RENAME_RELAY = "Rename the Solar Relay";
  private static final String FLOW = "Flow";
  private static final String SUN = "Solar Relay";
  private static final String SUN_RATE = "+%s / %s FE/t";
  private static final String CHUNK_COST = "Server chunk";
  private static final String BANKS = "Power Banks";
  private static final String GAIN = "+%s FE/t";
  private static final String LOSS = "−%s FE/t";
  private static final String NOTHING = "0 FE/t";
  private static final String CHUNK = "Chunk";
  private static final String LOAD_CHUNK = "Load the chunk";
  private static final String HOLDING_WITHOUT_LIMIT = "Holding this chunk · %s of yours";
  private static final String UPS = "UPS";
  private static final String UPS_VALUE = "%s%%";
  private static final String HOLDS_MINUTES = "holds %s min";
  private static final String HOLDS_HOURS = "holds %s h %s min";
  private static final String MAY_DROP = "the chunk may drop";
  private static final String NONE = "—";
  private static final String USED_SLOTS = "%s of %s slots";
  private static final String SLOT_RULE = "Items of the mod only, up to %s per slot";
  private static final String BANK_CHARGING = "Charging a Power Bank · +%s FE/t";
  private static final String BANK_WAITING = "Power Bank waits: UPS reserve";
  private static final String GENERATION = "Generation";
  private static final String SKY = "Sky";
  private static final String NOW = "Now";
  private static final String TIER_MOST = "Tier maximum";
  private static final String PANEL = "Panel";
  private static final String RATE = "%s FE/t";
  private static final String PAIR = "Pair";
  private static final String PAIRED = "%s · %s";
  private static final String NO_SERVER = "No Server in this chunk";
  private static final String GENERATING = "Generating";
  private static final String STOPPED = "Stopped";
  private static final String NO_PAIR = "No pair";
  private static final String STATUS_ON = "On";
  private static final String STATUS_ON_UPS = "On UPS";
  private static final String STATUS_NO_ENERGY = "No energy";
  private static final String CHUNK_HOLDING = "Holding this chunk · %s of your %s";
  private static final String CHUNK_LOCKED = "Locked by the world config";
  private static final String CHUNK_SWITCHED_OFF = "Released: switched off";
  private static final String CHUNK_OWNER_OFFLINE = "Released: owner offline";
  private static final String CHUNK_NO_ENERGY = "Released: no energy. Back at %s FE";
  private static final String CHUNK_PLAYER_LIMIT = "Released: you already hold %s chunks";
  private static final String CHUNK_WORLD_LIMIT = "Released: the world is at its limit";
  private static final String SKY_GENERATING = "Sun";
  private static final String SKY_NIGHT = "Night";
  private static final String SKY_RAIN = "Rain";
  private static final String SKY_COVERED = "Covered";
  private static final String SKY_NO_SUN = "No sun here";
  private static final String SKY_NO_ROOM = "No room";
  private static final String SKY_MOONLIGHT = "Moonlight";
  private static final String PANEL_STORED = "Stored";
  private static final String PANEL_DEPLOYING = "Opening";
  private static final String PANEL_DEPLOYED = "Open";
  private static final String PANEL_RETRACTING = "Closing";

  private NetworkScreenTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(NetworkKeys.TAB_ENERGY, TAB_ENERGY);
    add.accept(NetworkKeys.RENAME_SERVER, RENAME_SERVER);
    add.accept(NetworkKeys.RENAME_RELAY, RENAME_RELAY);
    add.accept(NetworkKeys.FLOW, FLOW);
    add.accept(NetworkKeys.SUN, SUN);
    add.accept(NetworkKeys.SUN_RATE, SUN_RATE);
    add.accept(NetworkKeys.CHUNK_COST, CHUNK_COST);
    add.accept(NetworkKeys.BANKS, BANKS);
    add.accept(NetworkKeys.GAIN, GAIN);
    add.accept(NetworkKeys.LOSS, LOSS);
    add.accept(NetworkKeys.NOTHING, NOTHING);
    add.accept(NetworkKeys.CHUNK, CHUNK);
    add.accept(NetworkKeys.LOAD_CHUNK, LOAD_CHUNK);
    add.accept(NetworkKeys.HOLDING_WITHOUT_LIMIT, HOLDING_WITHOUT_LIMIT);
    add.accept(NetworkKeys.UPS, UPS);
    add.accept(NetworkKeys.UPS_VALUE, UPS_VALUE);
    add.accept(NetworkKeys.HOLDS_MINUTES, HOLDS_MINUTES);
    add.accept(NetworkKeys.HOLDS_HOURS, HOLDS_HOURS);
    addMore(add);
    addStates(add);
  }

  private static void addMore(BiConsumer<String, String> add) {
    add.accept(NetworkKeys.MAY_DROP, MAY_DROP);
    add.accept(NetworkKeys.NONE, NONE);
    add.accept(NetworkKeys.USED_SLOTS, USED_SLOTS);
    add.accept(NetworkKeys.SLOT_RULE, SLOT_RULE);
    add.accept(NetworkKeys.BANK_CHARGING, BANK_CHARGING);
    add.accept(NetworkKeys.BANK_WAITING, BANK_WAITING);
    add.accept(NetworkKeys.GENERATION, GENERATION);
    add.accept(NetworkKeys.SKY, SKY);
    add.accept(NetworkKeys.NOW, NOW);
    add.accept(NetworkKeys.TIER_MOST, TIER_MOST);
    add.accept(NetworkKeys.PANEL, PANEL);
    add.accept(NetworkKeys.RATE, RATE);
    add.accept(NetworkKeys.PAIR, PAIR);
    add.accept(NetworkKeys.PAIRED, PAIRED);
    add.accept(NetworkKeys.NO_SERVER, NO_SERVER);
    add.accept(NetworkKeys.GENERATING, GENERATING);
    add.accept(NetworkKeys.STOPPED, STOPPED);
    add.accept(NetworkKeys.NO_PAIR, NO_PAIR);
  }

  private static void addStates(BiConsumer<String, String> add) {
    add.accept(NetworkKeys.status(ServerStatus.ON), STATUS_ON);
    add.accept(NetworkKeys.status(ServerStatus.ON_UPS), STATUS_ON_UPS);
    add.accept(NetworkKeys.status(ServerStatus.NO_ENERGY), STATUS_NO_ENERGY);
    add.accept(NetworkKeys.chunk(ChunkState.HOLDING), CHUNK_HOLDING);
    add.accept(NetworkKeys.chunk(ChunkState.LOCKED), CHUNK_LOCKED);
    add.accept(NetworkKeys.chunk(ChunkState.SWITCHED_OFF), CHUNK_SWITCHED_OFF);
    add.accept(NetworkKeys.chunk(ChunkState.OWNER_OFFLINE), CHUNK_OWNER_OFFLINE);
    add.accept(NetworkKeys.chunk(ChunkState.NO_ENERGY), CHUNK_NO_ENERGY);
    add.accept(NetworkKeys.chunk(ChunkState.PLAYER_LIMIT), CHUNK_PLAYER_LIMIT);
    add.accept(NetworkKeys.chunk(ChunkState.WORLD_LIMIT), CHUNK_WORLD_LIMIT);
    add.accept(NetworkKeys.sky(SunState.GENERATING), SKY_GENERATING);
    add.accept(NetworkKeys.sky(SunState.NIGHT), SKY_NIGHT);
    add.accept(NetworkKeys.sky(SunState.RAIN), SKY_RAIN);
    add.accept(NetworkKeys.sky(SunState.COVERED), SKY_COVERED);
    add.accept(NetworkKeys.sky(SunState.NO_SUN), SKY_NO_SUN);
    add.accept(NetworkKeys.sky(SunState.NO_ROOM), SKY_NO_ROOM);
    add.accept(NetworkKeys.sky(SunState.MOONLIGHT), SKY_MOONLIGHT);
    add.accept(NetworkKeys.panel(PanelState.STORED), PANEL_STORED);
    add.accept(NetworkKeys.panel(PanelState.DEPLOYING), PANEL_DEPLOYING);
    add.accept(NetworkKeys.panel(PanelState.DEPLOYED), PANEL_DEPLOYED);
    add.accept(NetworkKeys.panel(PanelState.RETRACTING), PANEL_RETRACTING);
  }
}
