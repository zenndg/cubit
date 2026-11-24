package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.block.DockXpTelling;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.xp.XpMode;
import io.github.zenndg.cubit.entity.RobotXpReturn;
import io.github.zenndg.cubit.menu.XpKeys;
import io.github.zenndg.cubit.registry.CubitFluids;
import java.util.function.BiConsumer;

/** The XP, in Portuguese, with the same keys as the English one (answer 1816). */
final class PortugueseXpTexts {
  private static final String FLUID = "Fluido de XP Cubit";
  private static final String FULL_NOTICE = "%s: meu tanque de XP está cheio";
  private static final String TAB = "XP";
  private static final String TANK = "Tanque";
  private static final String AMOUNT = "%s / %s mB · %s pontos";
  private static final String AMOUNT_ONE = "%s / %s mB · %s ponto";
  private static final String MARK = "%s mB";
  private static final String YOUR_LEVEL = "Seu nível: %s";
  private static final String MODES = "Modos";
  private static final String DELETE_EXCESS = "Apagar o excesso";
  private static final String EXCESS_DETAIL = "Com o tanque cheio, o que não cabe é apagado";
  private static final String ONLY_STORE = "Só no modo Armazenar";
  private static final String TAKE_DETAIL = "Tira um nível do tanque para você";
  private static final String SEND_DETAIL = "Envia um nível seu ao tanque";
  private static final String NOT_ACTIVE = "Só com o Cubit ativo";
  private static final String TANK_EMPTY = "O tanque está vazio";
  private static final String TANK_FULL = "O tanque está cheio";
  private static final String NO_XP = "Você não tem XP";
  private static final String TOOLTIP = "XP %s / %s mB";
  private static final String IGNORE = "Ignorar";
  private static final String IGNORE_DETAIL = "Apaga o XP do trabalho e do chão";
  private static final String STORE = "Armazenar";
  private static final String STORE_DETAIL = "Guarda o XP até o tanque encher";
  private static final String RETURN_TO_DOCK = "Voltar ao Dock";
  private static final String RETURN_TO_DOCK_DETAIL = "Cheio, esvazia o tanque no Dock";
  private static final String DOCK_FULL_NOTICE = "%s: o tanque de XP está cheio";
  private static final String UNLOAD = "Descarregar XP";
  private static final String UNLOAD_DETAIL = "Vai ao Dock e esvazia o tanque de XP";
  private static final String ENERGY_MADE = "Energia gerada: %s FE";
  private static final String SEND_CONVERTS = "Aqui o XP vira FE, sem volta";
  private static final String DELETE_ALL = "Apagar tudo";
  private static final String DELETE_ALL_DETAIL = "Apaga o que chega e mantém o guardado";
  private static final String STORE_DELETE = "Armazenar e apagar o excesso";
  private static final String STORE_DELETE_DETAIL = "Cheio, apaga o que não cabe";
  private static final String STORE_CONVERT = "Armazenar e converter o excesso";
  private static final String STORE_CONVERT_DETAIL = "Cheio, converte em FE o que não cabe";
  private static final String CONVERT_ALL = "Converter tudo";
  private static final String CONVERT_ALL_DETAIL = "Converte todo o XP em FE, sem volta";

  private PortugueseXpTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(CubitFluids.XP_FLUID_TYPE.get().getDescriptionId(), FLUID);
    add.accept(RobotXpReturn.FULL_KEY, FULL_NOTICE);
    add.accept(XpKeys.TAB, TAB);
    add.accept(XpKeys.TANK, TANK);
    add.accept(XpKeys.AMOUNT, AMOUNT);
    add.accept(XpKeys.AMOUNT_ONE, AMOUNT_ONE);
    add.accept(XpKeys.MARK, MARK);
    add.accept(XpKeys.YOUR_LEVEL, YOUR_LEVEL);
    add.accept(XpKeys.MODES, MODES);
    add.accept(XpKeys.DELETE_EXCESS, DELETE_EXCESS);
    add.accept(XpKeys.DELETE_EXCESS_DETAIL, EXCESS_DETAIL);
    add.accept(XpKeys.ONLY_STORE, ONLY_STORE);
    add.accept(XpKeys.TAKE_DETAIL, TAKE_DETAIL);
    add.accept(XpKeys.SEND_DETAIL, SEND_DETAIL);
    add.accept(XpKeys.NOT_ACTIVE, NOT_ACTIVE);
    add.accept(XpKeys.TANK_EMPTY, TANK_EMPTY);
    add.accept(XpKeys.TANK_FULL, TANK_FULL);
    add.accept(XpKeys.NO_XP, NO_XP);
    add.accept(XpKeys.TOOLTIP, TOOLTIP);
    add.accept(DockXpTelling.FULL_KEY, DOCK_FULL_NOTICE);
    add.accept(XpKeys.UNLOAD, UNLOAD);
    add.accept(XpKeys.UNLOAD_DETAIL, UNLOAD_DETAIL);
    addModes(add);
    addDock(add);
  }

  private static void addDock(BiConsumer<String, String> add) {
    add.accept(XpKeys.ENERGY_MADE, ENERGY_MADE);
    add.accept(XpKeys.SEND_CONVERTS, SEND_CONVERTS);
    add.accept(XpKeys.dockMode(DockXpMode.DELETE_ALL), DELETE_ALL);
    add.accept(XpKeys.dockModeDetail(DockXpMode.DELETE_ALL), DELETE_ALL_DETAIL);
    add.accept(XpKeys.dockMode(DockXpMode.STORE_DELETE_EXCESS), STORE_DELETE);
    add.accept(XpKeys.dockModeDetail(DockXpMode.STORE_DELETE_EXCESS), STORE_DELETE_DETAIL);
    add.accept(XpKeys.dockMode(DockXpMode.STORE_CONVERT_EXCESS), STORE_CONVERT);
    add.accept(XpKeys.dockModeDetail(DockXpMode.STORE_CONVERT_EXCESS), STORE_CONVERT_DETAIL);
    add.accept(XpKeys.dockMode(DockXpMode.CONVERT_ALL), CONVERT_ALL);
    add.accept(XpKeys.dockModeDetail(DockXpMode.CONVERT_ALL), CONVERT_ALL_DETAIL);
  }

  private static void addModes(BiConsumer<String, String> add) {
    add.accept(XpKeys.mode(XpMode.IGNORE), IGNORE);
    add.accept(XpKeys.modeDetail(XpMode.IGNORE), IGNORE_DETAIL);
    add.accept(XpKeys.mode(XpMode.STORE), STORE);
    add.accept(XpKeys.modeDetail(XpMode.STORE), STORE_DETAIL);
    add.accept(XpKeys.mode(XpMode.RETURN_TO_DOCK), RETURN_TO_DOCK);
    add.accept(XpKeys.modeDetail(XpMode.RETURN_TO_DOCK), RETURN_TO_DOCK_DETAIL);
  }
}
