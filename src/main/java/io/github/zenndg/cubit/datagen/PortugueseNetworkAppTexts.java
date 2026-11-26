package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.robot.TabletApp;
import io.github.zenndg.cubit.item.TabletItem;
import io.github.zenndg.cubit.player.NetworkAppKeys;
import java.util.function.BiConsumer;

/** The Network app of the Tablet, in Portuguese, with the same keys as the English one. */
final class PortugueseNetworkAppTexts {
  private static final String APP = "Rede";
  private static final String PICK = "Clique no seu Cubit, ou numa antena e depois num Dock";
  private static final String ONLY_YOURS = "Só os seus Cubits, antenas e Docks";
  private static final String ONLY_BLOCKS = "Clique numa antena, num Dock ou num baú";
  private static final String CHOSEN = "%s escolhido: clique nas antenas, Docks e baús dele";
  private static final String NOT_LINKED = "Esta antena não está ligada a um Servidor";
  private static final String ANTENNA_LINKED = "%s ligada a %s";
  private static final String ANTENNA_CONFIRM = "Clique em %s de novo para desligá-la de %s";
  private static final String ANTENNA_CONFIRM_AREA = "Clique em %s de novo: %s também perde a área";
  private static final String ANTENNA_UNLINKED = "%s desligada de %s";
  private static final String ANTENNA_UNLINKED_AREA = "%s desligada, e %s perdeu a área";
  private static final String ANTENNA_FULL = "%s liga até %s antenas";
  private static final String OTHER_SERVER = "Todas as antenas de %s são de um Servidor só";
  private static final String NOT_BESIDE = "Ela precisa dividir um lado com uma antena de %s";
  private static final String SPLITS = "Desligue antes as antenas das pontas";
  private static final String OUTSIDE = "Fora dos chunks das antenas de %s";
  private static final String DOCK_MARKED = "%s marcado para %s";
  private static final String DOCK_UNMARKED = "%s desmarcado de %s, ainda com energia";
  private static final String DOCK_FULL = "%s usa até %s Docks";
  private static final String CHEST_MARKED = "Baú marcado para %s";
  private static final String CHEST_UNMARKED = "Baú desmarcado de %s";
  private static final String CHEST_FULL = "%s usa até %s baús";
  private static final String FEED_NEXT = "Agora clique num Dock do chunk de %s";
  private static final String FED = "%s agora alimenta %s";
  private static final String FEED_CONFIRM = "Clique em %s de novo e corte a energia: %s Cubits";
  private static final String UNFED = "%s não alimenta mais %s";
  private static final String ARROW_CHANNEL = "canal %s";
  private static final String ARROW_OF = "do %s";
  private static final String ARROW_FREE_SLOTS = "%s slots livres";
  private static final String ARROW_ENERGY_ONLY = "só energia";
  private static final String NOT_IN_CHUNK = "%s não está no chunk de %s";

  private PortugueseNetworkAppTexts() {}

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
    addFeedAndArrows(add);
  }

  /** Feeding a Dock without a Cubit, and the two lines over the arrows (answers 2589 and 2595). */
  private static void addFeedAndArrows(BiConsumer<String, String> add) {
    add.accept(NetworkAppKeys.FEED_NEXT, FEED_NEXT);
    add.accept(NetworkAppKeys.FED, FED);
    add.accept(NetworkAppKeys.FEED_CONFIRM, FEED_CONFIRM);
    add.accept(NetworkAppKeys.UNFED, UNFED);
    add.accept(NetworkAppKeys.NOT_IN_CHUNK, NOT_IN_CHUNK);
    add.accept(NetworkAppKeys.ARROW_CHANNEL, ARROW_CHANNEL);
    add.accept(NetworkAppKeys.ARROW_OF, ARROW_OF);
    add.accept(NetworkAppKeys.ARROW_FREE_SLOTS, ARROW_FREE_SLOTS);
    add.accept(NetworkAppKeys.ARROW_ENERGY_ONLY, ARROW_ENERGY_ONLY);
  }
}
