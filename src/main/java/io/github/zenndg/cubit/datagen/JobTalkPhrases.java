package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.entity.RobotJobTalk;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/** The phrases of the jobs, in English, the base language, all original (answers 1583 to 1590). */
final class JobTalkPhrases {
  private static final String FORMAT = "%s (%s): %s";
  private static final String PAT_IN_0 = "Galactic Patroller, reporting for duty!";
  private static final String PAT_IN_1 = "Patrol online. Stars aligned!";
  private static final String PAT_IN_2 = "Helmet on, visor ready!";
  private static final String PAT_FIGHT_0 = "Stay calm. Stay focused.";
  private static final String PAT_FIGHT_1 = "Patience is my sharpest blade.";
  private static final String PAT_FIGHT_2 = "I sense trouble nearby.";
  private static final String PAT_FIGHT_3 = "Visor down. Here we go!";
  private static final String PAT_FIGHT_4 = "Hostile detected. Engaging!";
  private static final String PAT_FIGHT_5 = "Not on my patrol!";
  private static final String PAT_WIN_0 = "Sector secure.";
  private static final String PAT_WIN_1 = "Calm returns to the base.";
  private static final String PAT_WIN_2 = "Another one for the logbook.";
  private static final String PAT_WIN_3 = "The patrol continues.";
  private static final String FISH_IN_0 = "Gone fishing!";
  private static final String FISH_IN_1 = "Hook, line and Cubit!";
  private static final String FISH_IN_2 = "The water is calling.";
  private static final String FISH_DO_0 = "Got one!";
  private static final String FISH_DO_1 = "This one's a keeper!";
  private static final String FISH_DO_2 = "The fish are biting today.";
  private static final String FISH_DO_3 = "Patience pays off.";
  private static final String FISH_DO_4 = "Another one for the chest!";
  private static final String FARM_IN_0 = "Seeds ready, hoe ready!";
  private static final String FARM_IN_1 = "Time to get my tracks dirty!";
  private static final String FARM_IN_2 = "Let's grow something great.";
  private static final String FARM_DO_0 = "Fresh from the field!";
  private static final String FARM_DO_1 = "Look at this harvest!";
  private static final String FARM_DO_2 = "Grow, little plants, grow!";
  private static final String FARM_DO_3 = "Farming is my therapy.";
  private static final String FARM_DO_4 = "Another row done.";
  private static final String ORG_IN_0 = "A place for everything!";
  private static final String ORG_IN_1 = "Chaos? Not on my watch.";
  private static final String ORG_IN_2 = "Labels ready, chests ready!";
  private static final String ORG_DO_0 = "Sorted!";
  private static final String ORG_DO_1 = "Chests don't organize themselves.";
  private static final String ORG_DO_2 = "Same items, same chest.";
  private static final String ORG_DO_3 = "Ahh, so satisfying.";
  private static final String ORG_DO_4 = "Inventory: under control.";
  private static final String COOK_IN_0 = "Chef Cubit, at your service!";
  private static final String COOK_IN_1 = "The kitchen is open!";
  private static final String COOK_IN_2 = "Ovens on, apron on!";
  private static final String COOK_DO_0 = "Order up!";
  private static final String COOK_DO_1 = "Smells delicious!";
  private static final String COOK_DO_2 = "Hot and ready!";
  private static final String COOK_DO_3 = "Careful, it's hot!";
  private static final String COOK_DO_4 = "Seasoned with redstone. Just kidding!";

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

  private JobTalkPhrases() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RobotJobTalk.FORMAT_KEY, FORMAT);
    PHRASES.forEach((topic, phrases) -> {
      for (int index = 0; index < phrases.size(); index++) {
        add.accept(RobotJobTalk.keyOf(topic, index), phrases.get(index));
      }
    });
  }
}
