package io.github.zenndg.cubit.menu;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.CascadeLaunch;
import io.github.zenndg.cubit.block.CascadeState;
import io.github.zenndg.cubit.block.DeskRecipes;
import io.github.zenndg.cubit.block.EngineeringDeskBlockEntity;
import io.github.zenndg.cubit.domain.desk.CascadeRun;
import io.github.zenndg.cubit.domain.desk.DeskOrder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The Cascade tab in the menu: what this player chose, kept only while the screen is open, and the
 * plan of the desk (answers 2006 and 2021). The server decides, and the client reads the numbers.
 */
public final class CascadeMenuPart {
  public static final int MINUS = 100;
  public static final int PLUS = 101;
  public static final int MINUS_SHIFT = 102;
  public static final int PLUS_SHIFT = 103;
  public static final int START = 104;
  public static final int NEW_PLAN = 105;
  public static final int CHOOSE_FIRST = 10_000;
  private static final int MAX_STEPS = 32;
  private static final int RECIPE = 0;
  private static final int QUANTITY = 1;
  private static final int STATE = 2;
  private static final int STEPS = 3;
  private static final int ALLOWED = 4;
  private static final int CRAFT_TICKS = 5;
  private static final int CRAFT_ENERGY = 6;
  private static final int HOLDS = 7;
  private static final int FIRST_STEP = 8;
  private static final int STEP_SIZE = 3;
  private static final int COUNT = FIRST_STEP + MAX_STEPS * STEP_SIZE;
  private static final int NONE = -1;

  private final EngineeringDeskBlockEntity desk;
  private final ContainerData data;
  private int recipe = NONE;
  private int quantity = DeskOrder.STEP;
  private CascadeRun shownRun;

  /** What the tab shows of the plan. */
  public enum State {
    NONE,
    RUNNING,
    DONE
  }

  /** A line of the table: the recipe in the catalog, the crafts asked and the ones done. */
  public record StepView(int recipe, int total, int done) {}

  private CascadeMenuPart(EngineeringDeskBlockEntity desk) {
    this.desk = desk;
    this.data = new SimpleContainerData(COUNT);
  }

  static CascadeMenuPart onServer(EngineeringDeskBlockEntity desk) {
    CascadeMenuPart part = new CascadeMenuPart(desk);
    part.refresh();
    return part;
  }

  static CascadeMenuPart onClient() {
    return new CascadeMenuPart(null);
  }

  ContainerData data() {
    return data;
  }

  public static int choose(int recipeIndex) {
    return CHOOSE_FIRST + recipeIndex;
  }

  /** A plan on its way locks the desk for everyone (answers 2006 and 2015). */
  public boolean locks() {
    return desk == null ? state() == State.RUNNING : desk.cascade().isRunning();
  }

  /** True when the click was of this tab. */
  boolean click(ServerPlayer player, int id) {
    switch (id) {
      case MINUS -> change(-DeskOrder.STEP);
      case PLUS -> change(DeskOrder.STEP);
      case MINUS_SHIFT -> change(-DeskOrder.SHIFT_STEP);
      case PLUS_SHIFT -> change(DeskOrder.SHIFT_STEP);
      case START -> start(player);
      case NEW_PLAN -> {
        desk.cascade().forget();
        desk.setChanged();
      }
      default -> {
        return pick(player, id - CHOOSE_FIRST);
      }
    }
    return true;
  }

  private void change(int delta) {
    quantity = Math.clamp(quantity + (long) delta, DeskOrder.STEP, desk.spec().maxOrder());
  }

  private boolean pick(ServerPlayer player, int index) {
    List<RecipeHolder<CraftingRecipe>> all = all(player);
    if (index < 0 || index >= all.size() || !CascadeLaunch.mayChoose(player, all.get(index))) {
      return false;
    }
    recipe = index;
    quantity = DeskOrder.STEP;
    return true;
  }

  private void start(ServerPlayer player) {
    List<RecipeHolder<CraftingRecipe>> all = all(player);
    if (recipe < 0 || recipe >= all.size()) {
      return;
    }
    Item item = all.get(recipe).value().getResultItem(player.registryAccess()).getItem();
    CascadeLaunch.start(desk, player, item, quantity);
  }

  private static List<RecipeHolder<CraftingRecipe>> all(ServerPlayer player) {
    return DeskRecipes.all(player.serverLevel().getRecipeManager());
  }

  void refresh() {
    if (desk == null) {
      return;
    }
    CascadeState cascade = desk.cascade();
    data.set(RECIPE, recipe);
    data.set(QUANTITY, quantity);
    data.set(STATE, stateOf(cascade).ordinal());
    data.set(ALLOWED, desk.spec().cascade() ? 1 : 0);
    data.set(CRAFT_TICKS, Math.min(desk.spec().craftTicks(), Short.MAX_VALUE));
    long cost = CubitMod.CONFIG.engineeringDesk().craftEnergy().amount();
    data.set(CRAFT_ENERGY, (int) Math.min(cost, Short.MAX_VALUE));
    data.set(HOLDS, cascade.holdsItems() ? 1 : 0);
    CascadeRun run = cascade.run().orElse(null);
    if (run != shownRun) {
      shownRun = run;
      showSteps(run);
    }
  }

  private static State stateOf(CascadeState cascade) {
    if (cascade.isRunning()) {
      return State.RUNNING;
    }
    return cascade.run().isPresent() ? State.DONE : State.NONE;
  }

  private void showSteps(CascadeRun run) {
    int count = run == null ? 0 : Math.min(run.steps().size(), MAX_STEPS);
    data.set(STEPS, count);
    if (desk.getLevel() == null) {
      return;
    }
    List<ResourceLocation> ids =
        DeskRecipes.all(desk.getLevel().getRecipeManager()).stream().map(RecipeHolder::id).toList();
    for (int index = 0; index < count; index++) {
      CascadeRun.Step step = run.steps().get(index);
      int first = FIRST_STEP + index * STEP_SIZE;
      data.set(first, ids.indexOf(ResourceLocation.tryParse(step.recipe())));
      data.set(first + 1, Math.min(step.total(), Short.MAX_VALUE));
      data.set(first + 2, Math.min(step.done(), Short.MAX_VALUE));
    }
  }

  /** The chosen recipe, as its place in the catalog, or a negative number without one. */
  public int recipe() {
    return data.get(RECIPE);
  }

  public int quantity() {
    return Math.max(DeskOrder.STEP, data.get(QUANTITY));
  }

  public State state() {
    return ScreenEnums.of(State.values(), data.get(STATE));
  }

  /** Whether the tier of the desk has the cascade (answer 2020). */
  public boolean allowed() {
    return data.get(ALLOWED) != 0;
  }

  /** Whether the last plan still has items to leave the desk (answer 2028). */
  public boolean holdsItems() {
    return data.get(HOLDS) != 0;
  }

  public int craftTicks() {
    return data.get(CRAFT_TICKS);
  }

  public int craftEnergy() {
    return data.get(CRAFT_ENERGY);
  }

  public List<StepView> steps() {
    List<StepView> steps = new ArrayList<>();
    int count = Math.clamp(data.get(STEPS), 0, MAX_STEPS);
    for (int index = 0; index < count; index++) {
      int first = FIRST_STEP + index * STEP_SIZE;
      steps.add(new StepView(data.get(first), data.get(first + 1), data.get(first + 2)));
    }
    return steps;
  }
}
