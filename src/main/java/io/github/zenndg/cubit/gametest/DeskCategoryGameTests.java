package io.github.zenndg.cubit.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.desk.DeskCategory;
import io.github.zenndg.cubit.item.DeskCategoryCodecs;
import io.github.zenndg.cubit.item.EngineeringRecipe;
import java.util.Map;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The categories of the catalog of the desk in the recipes the game loads: one of each kind of
 * recipe, and a JSON without the field or with an unknown one (answers 1521, 1522 and 1525).
 */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DeskCategoryGameTests {
  private static final String ARENA = "arena";
  private static final String WRONG_CATEGORY = "Wrong desk category for ";
  private static final String UNKNOWN_ID = "furniture";
  private static final String JSON_TYPE = "{\"type\":\"cubit:engineering\",";
  private static final String JSON_INGREDIENT = "{\"item\":\"minecraft:iron_ingot\"}";
  private static final String JSON_INGREDIENTS = "\"ingredients\":[" + JSON_INGREDIENT + "],";
  private static final String JSON_RESULT = "\"result\":{\"id\":\"minecraft:iron_nugget\"}}";
  private static final String SHAPELESS_JSON = JSON_TYPE + JSON_INGREDIENTS + JSON_RESULT;

  private static final String ROBOT = "general_robot";
  private static final String REPAIR = "repair_general_robot";
  private static final String RECHARGE = "recharge_general_robot";
  private static final String POWER_BANK = "advanced_power_bank";
  private static final String SOLAR_DOCK = "solar_induction_dock";
  private static final String MODULE = "elite_module";
  private static final String CARD = "ml_cook";
  private static final String TABLET = "tablet";

  /** One recipe of each type that runs in the desk, with the category of answer 1525. */
  private static final Map<String, DeskCategory> EXPECTED =
      Map.of(
          ROBOT, DeskCategory.ROBOT,
          REPAIR, DeskCategory.ROBOT,
          RECHARGE, DeskCategory.ENERGY,
          POWER_BANK, DeskCategory.ENERGY,
          SOLAR_DOCK, DeskCategory.DOCK,
          MODULE, DeskCategory.MODULES,
          CARD, DeskCategory.CARDS,
          TABLET, DeskCategory.TOOLS);

  private DeskCategoryGameTests() {}

  @GameTest(template = ARENA)
  public static void eachRecipeOfTheModHasItsCategory(GameTestHelper helper) {
    EXPECTED.forEach((path, category) -> {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CubitMod.MOD_ID, path);
      RecipeHolder<?> recipe = helper.getLevel().getRecipeManager().byKey(id).orElseThrow();
      helper.assertTrue(DeskCategoryCodecs.of(recipe.value()) == category, WRONG_CATEGORY + path);
    });
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aRecipeWithoutTheFieldGoesToTheTools(GameTestHelper helper) {
    helper.assertTrue(read(json()) == DeskCategory.TOOLS, WRONG_CATEGORY + SHAPELESS_JSON);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void anUnknownCategoryGoesToTheTools(GameTestHelper helper) {
    JsonObject json = json();
    json.addProperty(DeskCategoryCodecs.FIELD, UNKNOWN_ID);
    helper.assertTrue(read(json) == DeskCategory.TOOLS, WRONG_CATEGORY + UNKNOWN_ID);
    helper.succeed();
  }

  @GameTest(template = ARENA)
  public static void aKnownCategoryIsRead(GameTestHelper helper) {
    JsonObject json = json();
    json.addProperty(DeskCategoryCodecs.FIELD, DeskCategory.CARDS.id());
    helper.assertTrue(read(json) == DeskCategory.CARDS, WRONG_CATEGORY + DeskCategory.CARDS.id());
    helper.succeed();
  }

  private static JsonObject json() {
    return JsonParser.parseString(SHAPELESS_JSON).getAsJsonObject();
  }

  private static DeskCategory read(JsonObject json) {
    return EngineeringRecipe.CODEC
        .codec()
        .parse(JsonOps.INSTANCE, json)
        .getOrThrow()
        .deskCategory();
  }
}
