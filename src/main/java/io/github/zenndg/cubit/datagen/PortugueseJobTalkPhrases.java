package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.entity.RobotJobTalk;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/** The phrases of the jobs, in Portuguese, with the same keys as English (answers 1583 to 1590). */
final class PortugueseJobTalkPhrases {
  private static final String FORMAT = "%s (%s): %s";
  private static final String PAT_IN_0 = "Patrulheiro Galático, apresentando-se para o serviço!";
  private static final String PAT_IN_1 = "Patrulha online. Estrelas alinhadas!";
  private static final String PAT_IN_2 = "Capacete posto, visor pronto!";
  private static final String PAT_FIGHT_0 = "Calma. Foco.";
  private static final String PAT_FIGHT_1 = "A paciência é a minha lâmina mais afiada.";
  private static final String PAT_FIGHT_2 = "Sinto encrenca por perto.";
  private static final String PAT_FIGHT_3 = "Visor baixado. Lá vamos nós!";
  private static final String PAT_FIGHT_4 = "Hostil detectado. Atacando!";
  private static final String PAT_FIGHT_5 = "Não na minha patrulha!";
  private static final String PAT_WIN_0 = "Setor seguro.";
  private static final String PAT_WIN_1 = "A calma volta à base.";
  private static final String PAT_WIN_2 = "Mais um para o diário de bordo.";
  private static final String PAT_WIN_3 = "A patrulha continua.";
  private static final String FISH_IN_0 = "Fui pescar!";
  private static final String FISH_IN_1 = "Anzol, linha e Cubit!";
  private static final String FISH_IN_2 = "A água está chamando.";
  private static final String FISH_DO_0 = "Peguei um!";
  private static final String FISH_DO_1 = "Esse fica!";
  private static final String FISH_DO_2 = "Os peixes estão mordendo hoje.";
  private static final String FISH_DO_3 = "A paciência compensa.";
  private static final String FISH_DO_4 = "Mais um para o baú!";
  private static final String FARM_IN_0 = "Sementes prontas, enxada pronta!";
  private static final String FARM_IN_1 = "Hora de sujar as esteiras!";
  private static final String FARM_IN_2 = "Vamos plantar algo incrível.";
  private static final String FARM_DO_0 = "Fresquinho da roça!";
  private static final String FARM_DO_1 = "Olha essa colheita!";
  private static final String FARM_DO_2 = "Cresçam, plantinhas, cresçam!";
  private static final String FARM_DO_3 = "Plantar é a minha terapia.";
  private static final String FARM_DO_4 = "Mais uma fileira pronta.";
  private static final String ORG_IN_0 = "Cada coisa no seu lugar!";
  private static final String ORG_IN_1 = "Bagunça? Não no meu turno.";
  private static final String ORG_IN_2 = "Etiquetas prontas, baús prontos!";
  private static final String ORG_DO_0 = "Organizado!";
  private static final String ORG_DO_1 = "Baús não se organizam sozinhos.";
  private static final String ORG_DO_2 = "Itens iguais, baú igual.";
  private static final String ORG_DO_3 = "Ahh, que satisfação.";
  private static final String ORG_DO_4 = "Inventário: sob controle.";
  private static final String COOK_IN_0 = "Chef Cubit, a seu dispor!";
  private static final String COOK_IN_1 = "A cozinha está aberta!";
  private static final String COOK_IN_2 = "Fornos ligados, avental posto!";
  private static final String COOK_DO_0 = "Saindo um prato!";
  private static final String COOK_DO_1 = "Que cheiro bom!";
  private static final String COOK_DO_2 = "Quentinho e pronto!";
  private static final String COOK_DO_3 = "Cuidado, está quente!";
  private static final String COOK_DO_4 = "Temperado com redstone. Brincadeira!";

  private static final Map<JobTalkTopic, List<String>> PHRASES =
      Map.ofEntries(
          Map.entry(JobTalkTopic.PATROLLER_ENTRY, List.of(PAT_IN_0, PAT_IN_1, PAT_IN_2)),
          Map.entry(
              JobTalkTopic.PATROLLER_COMBAT,
              List.of(
                  PAT_FIGHT_0, PAT_FIGHT_1, PAT_FIGHT_2, PAT_FIGHT_3, PAT_FIGHT_4, PAT_FIGHT_5)),
          Map.entry(
              JobTalkTopic.PATROLLER_DEFEAT, List.of(PAT_WIN_0, PAT_WIN_1, PAT_WIN_2, PAT_WIN_3)),
          Map.entry(JobTalkTopic.FISHER_ENTRY, List.of(FISH_IN_0, FISH_IN_1, FISH_IN_2)),
          Map.entry(
              JobTalkTopic.FISHER_CATCH,
              List.of(FISH_DO_0, FISH_DO_1, FISH_DO_2, FISH_DO_3, FISH_DO_4)),
          Map.entry(JobTalkTopic.FARMER_ENTRY, List.of(FARM_IN_0, FARM_IN_1, FARM_IN_2)),
          Map.entry(
              JobTalkTopic.FARMER_HARVEST,
              List.of(FARM_DO_0, FARM_DO_1, FARM_DO_2, FARM_DO_3, FARM_DO_4)),
          Map.entry(JobTalkTopic.ORGANIZER_ENTRY, List.of(ORG_IN_0, ORG_IN_1, ORG_IN_2)),
          Map.entry(
              JobTalkTopic.ORGANIZER_STORE,
              List.of(ORG_DO_0, ORG_DO_1, ORG_DO_2, ORG_DO_3, ORG_DO_4)),
          Map.entry(JobTalkTopic.COOK_ENTRY, List.of(COOK_IN_0, COOK_IN_1, COOK_IN_2)),
          Map.entry(
              JobTalkTopic.COOK_SERVE,
              List.of(COOK_DO_0, COOK_DO_1, COOK_DO_2, COOK_DO_3, COOK_DO_4)));

  private PortugueseJobTalkPhrases() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RobotJobTalk.FORMAT_KEY, FORMAT);
    PHRASES.forEach((topic, phrases) -> {
      for (int index = 0; index < phrases.size(); index++) {
        add.accept(RobotJobTalk.keyOf(topic, index), phrases.get(index));
      }
    });
  }
}
