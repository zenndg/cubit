package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.desk.CascadeStart;
import io.github.zenndg.cubit.domain.desk.CascadeTaskStatus;
import io.github.zenndg.cubit.menu.DeskScreenKeys;
import java.util.function.BiConsumer;

/** The Cascade tab of the Engineering Desk, in Portuguese (answer 2018). */
final class PortugueseCascadeTexts {
  private static final String NAME = "Cascata";
  private static final String LOCKED = "Liberada no Ultimate";
  private static final String LOCKS = "Há um plano em andamento";
  private static final String PART_LOCKED = "Bloqueada: %s";
  private static final String PICK = "Escolha um item no catálogo";
  private static final String MATERIALS = "Materiais de %s× %s";
  private static final String HAS = "Na entrada: %s / %s";
  private static final String USING = "Aproveitando: %s";
  private static final String VIEW = "Ver plano";
  private static final String START = "Iniciar";
  private static final String NEW = "Novo plano";
  private static final String YES = "Sim";
  private static final String NO = "Não";
  private static final String ASK = "Iniciar este plano?";
  private static final String ASKED = "%s× %s · %s s";
  private static final String AMOUNT = "%s× %s";
  private static final String COST = "%s FE · %s s";
  private static final String NO_CANCEL = "Não dá para cancelar.";
  private static final String ORDER_COLUMN = "#";
  private static final String ITEM_COLUMN = "Item";
  private static final String RESERVED_COLUMN = "Reserva";
  private static final String NEEDS_COLUMN = "Depende";
  private static final String STATUS_COLUMN = "Status";
  private static final String WAITING = "Aguardando";
  private static final String CRAFTING = "Processando %s/%s";
  private static final String DONE = "Concluído";
  private static final String REASON_READY = "Pronto";
  private static final String REASON_TIER = "Liberada no Ultimate";
  private static final String REASON_NO_PLAN = "Sem plano para este item";
  private static final String REASON_LOCKED = "Uma peça dele está bloqueada";
  private static final String REASON_MISSING = "Faltam materiais na entrada";
  private static final String REASON_ORDER_ACTIVE = "Há um pedido ativo";
  private static final String REASON_STOCK_LEFT = "O último plano ainda tem itens";

  private PortugueseCascadeTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.CASCADE, NAME);
    add.accept(DeskScreenKeys.CASCADE_LOCKED, LOCKED);
    add.accept(DeskScreenKeys.CASCADE_LOCKS, LOCKS);
    add.accept(DeskScreenKeys.CASCADE_PART_LOCKED, PART_LOCKED);
    add.accept(DeskScreenKeys.CASCADE_PICK, PICK);
    add.accept(DeskScreenKeys.CASCADE_MATERIALS, MATERIALS);
    add.accept(DeskScreenKeys.CASCADE_HAS, HAS);
    add.accept(DeskScreenKeys.CASCADE_USING, USING);
    add.accept(DeskScreenKeys.CASCADE_VIEW, VIEW);
    add.accept(DeskScreenKeys.CASCADE_START, START);
    add.accept(DeskScreenKeys.CASCADE_NEW, NEW);
    add.accept(DeskScreenKeys.CASCADE_YES, YES);
    add.accept(DeskScreenKeys.CASCADE_NO, NO);
    add.accept(DeskScreenKeys.CASCADE_ASK, ASK);
    add.accept(DeskScreenKeys.CASCADE_ASKED, ASKED);
    add.accept(DeskScreenKeys.CASCADE_AMOUNT, AMOUNT);
    add.accept(DeskScreenKeys.CASCADE_COST, COST);
    add.accept(DeskScreenKeys.CASCADE_NO_CANCEL, NO_CANCEL);
    addTable(add);
    addReasons(add);
  }

  private static void addTable(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.CASCADE_ORDER_COLUMN, ORDER_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_ITEM_COLUMN, ITEM_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_RESERVED_COLUMN, RESERVED_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_NEEDS_COLUMN, NEEDS_COLUMN);
    add.accept(DeskScreenKeys.CASCADE_STATUS_COLUMN, STATUS_COLUMN);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.WAITING), WAITING);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.CRAFTING), CRAFTING);
    add.accept(DeskScreenKeys.cascadeStatus(CascadeTaskStatus.DONE), DONE);
  }

  private static void addReasons(BiConsumer<String, String> add) {
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.READY), REASON_READY);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.TIER), REASON_TIER);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.NO_PLAN), REASON_NO_PLAN);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.LOCKED), REASON_LOCKED);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.MISSING), REASON_MISSING);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.ORDER_ACTIVE), REASON_ORDER_ACTIVE);
    add.accept(DeskScreenKeys.cascadeStart(CascadeStart.STOCK_LEFT), REASON_STOCK_LEFT);
  }
}
