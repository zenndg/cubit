package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.energy.PanelState;
import io.github.zenndg.cubit.domain.energy.SunState;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.ServerStatus;
import io.github.zenndg.cubit.menu.NetworkKeys;
import java.util.function.BiConsumer;

/** The screens of the Network, in Portuguese, with the same keys as the English ones. */
final class PortugueseNetworkScreenTexts {
  private static final String TAB_ENERGY = "Energia";
  private static final String RENAME_SERVER = "Renomear o Servidor";
  private static final String RENAME_RELAY = "Renomear o Relé Solar";
  private static final String FLOW = "Fluxo";
  private static final String SUN = "Relé Solar";
  private static final String SUN_RATE = "+%s / %s FE/t";
  private static final String CHUNK_COST = "Servidor e chunk";
  private static final String COST_CHUNK = "Chunk: %s FE/t";
  private static final String COST_SERVER = "Servidor: %s FE/t";
  private static final String COST_RELAY = "Relé Solar: %s FE/t";
  private static final String BANKS = "Power Banks";
  private static final String GAIN = "+%s FE/t";
  private static final String LOSS = "−%s FE/t";
  private static final String NOTHING = "0 FE/t";
  private static final String CHUNK = "Chunk";
  private static final String LOAD_CHUNK = "Carregar o chunk";
  private static final String HOLDING_WITHOUT_LIMIT = "Segurando este chunk · %s seus";
  private static final String UPS = "Nobreak";
  private static final String UPS_VALUE = "%s%%";
  private static final String HOLDS_MINUTES = "segura %s min";
  private static final String HOLDS_HOURS = "segura %s h %s min";
  private static final String MAY_DROP = "o chunk pode cair";
  private static final String NONE = "—";
  private static final String USED_SLOTS = "%s de %s slots";
  private static final String SLOT_RULE = "Só itens do mod, até %s por slot";
  private static final String BANK_CHARGING = "Power Bank carregando · +%s FE/t";
  private static final String BANK_WAITING = "Power Bank espera: reserva do Nobreak";
  private static final String GENERATION = "Geração";
  private static final String SKY = "Céu";
  private static final String NOW = "Agora";
  private static final String TIER_MOST = "Máximo do tier";
  private static final String PANEL = "Painel";
  private static final String RATE = "%s FE/t";
  private static final String PAIR = "Par";
  private static final String PAIRED = "%s · %s";
  private static final String NO_SERVER = "Sem Servidor neste chunk";
  private static final String GENERATING = "Gerando";
  private static final String STOPPED = "Parado";
  private static final String NO_PAIR = "Sem par";
  private static final String STATUS_ON = "Ligado";
  private static final String STATUS_ON_UPS = "No Nobreak";
  private static final String STATUS_NO_ENERGY = "Sem energia";
  private static final String CHUNK_HOLDING = "Segurando este chunk · %s de %s seus";
  private static final String CHUNK_LOCKED = "Travado pelo config do mundo";
  private static final String CHUNK_SWITCHED_OFF = "Solto: chave desligada";
  private static final String CHUNK_OWNER_OFFLINE = "Solto: dono offline";
  private static final String CHUNK_NO_ENERGY = "Solto: sem energia. Volta com %s FE";
  private static final String CHUNK_PLAYER_LIMIT = "Solto: você já segura %s chunks";
  private static final String CHUNK_WORLD_LIMIT = "Solto: o mundo está no limite";
  private static final String SKY_GENERATING = "Sol";
  private static final String SKY_NIGHT = "Noite";
  private static final String SKY_RAIN = "Chuva";
  private static final String SKY_COVERED = "Coberto";
  private static final String SKY_NO_SUN = "Sem sol aqui";
  private static final String SKY_NO_ROOM = "Sem espaço";
  private static final String SKY_MOONLIGHT = "Luz da lua";
  private static final String PANEL_STORED = "Guardado";
  private static final String PANEL_DEPLOYING = "Abrindo";
  private static final String PANEL_DEPLOYED = "Aberto";
  private static final String PANEL_RETRACTING = "Fechando";

  private PortugueseNetworkScreenTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(NetworkKeys.TAB_ENERGY, TAB_ENERGY);
    add.accept(NetworkKeys.RENAME_SERVER, RENAME_SERVER);
    add.accept(NetworkKeys.RENAME_RELAY, RENAME_RELAY);
    add.accept(NetworkKeys.FLOW, FLOW);
    add.accept(NetworkKeys.SUN, SUN);
    add.accept(NetworkKeys.SUN_RATE, SUN_RATE);
    add.accept(NetworkKeys.CHUNK_COST, CHUNK_COST);
    add.accept(NetworkKeys.COST_CHUNK, COST_CHUNK);
    add.accept(NetworkKeys.COST_SERVER, COST_SERVER);
    add.accept(NetworkKeys.COST_RELAY, COST_RELAY);
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
