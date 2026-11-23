package io.github.zenndg.cubit.datagen;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.registry.CubitBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * The block models of the blocks drawn by GeckoLib, the dock and the desk, which only give the
 * particles of breaking, and their items, drawn by the same models.
 */
final class CubitBlockStateProvider extends BlockStateProvider {
  private static final String PARTICLE = "particle";
  private static final String DOCK_BODY = "_body";
  private static final String BLOCK_FOLDER = "block/";
  private static final String BUILTIN_ENTITY = "builtin/entity";
  // The display of a block item of the game, so the desk sits in the slot like any block.
  private static final float GUI_TILT = 30;
  private static final float GUI_TURN = 225;
  private static final float GUI_SCALE = 0.625f;
  private static final float GROUND_LIFT = 3;
  private static final float GROUND_SCALE = 0.25f;
  private static final float FIXED_SCALE = 0.5f;
  private static final float HAND_TILT = 75;
  private static final float HAND_TURN = 45;
  private static final float HAND_LIFT = 2.5f;
  private static final float HAND_SCALE = 0.375f;
  private static final float FIRST_PERSON_SCALE = 0.4f;
  // The display of the dock item, the same as the JSON model it had before GeckoLib.
  private static final float DOCK_GUI_TILT = 20;
  private static final float DOCK_GUI_LIFT = -2.5f;
  private static final float DOCK_GUI_SCALE = 0.42f;
  private static final float DOCK_GROUND_LIFT = 2;
  private static final float DOCK_FIXED_SCALE = 0.45f;
  private static final float DOCK_HAND_LIFT = 2;
  private static final float DOCK_HAND_SCALE = 0.28f;
  private static final float DOCK_FIRST_PERSON_SCALE = 0.3f;

  CubitBlockStateProvider(PackOutput output, ExistingFileHelper existingFiles) {
    super(output, CubitMod.MOD_ID, existingFiles);
  }

  @Override
  protected void registerStatesAndModels() {
    inductionDock();
    engineeringDesk();
  }

  /**
   * The dock is drawn by GeckoLib, from its lower part, so its block model only gives the particles
   * of breaking, and its item is drawn by the same model (answer 1860).
   */
  private void inductionDock() {
    particlesOnly(
        CubitBlocks.INDUCTION_DOCK.get(),
        CubitBlocks.INDUCTION_DOCK_ID,
        CubitBlocks.INDUCTION_DOCK_ID + DOCK_BODY);
    dockItem();
  }

  private void particlesOnly(Block block, String name, String texture) {
    ModelFile particles =
        models().getBuilder(name).texture(PARTICLE, modLoc(BLOCK_FOLDER + texture));
    getVariantBuilder(block)
        .forAllStates(state -> ConfiguredModel.builder().modelFile(particles).build());
  }

  /** The item of the dock, placed in each view as the JSON model it had before GeckoLib. */
  private void dockItem() {
    itemModels()
        .getBuilder(CubitBlocks.INDUCTION_DOCK_ID)
        .parent(new ModelFile.UncheckedModelFile(BUILTIN_ENTITY))
        .transforms()
        .transform(ItemDisplayContext.GUI)
        .rotation(DOCK_GUI_TILT, GUI_TURN, 0)
        .translation(0, DOCK_GUI_LIFT, 0)
        .scale(DOCK_GUI_SCALE)
        .end()
        .transform(ItemDisplayContext.GROUND)
        .translation(0, DOCK_GROUND_LIFT, 0)
        .scale(GROUND_SCALE)
        .end()
        .transform(ItemDisplayContext.FIXED)
        .scale(DOCK_FIXED_SCALE)
        .end()
        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
        .rotation(HAND_TILT, HAND_TURN, 0)
        .translation(0, DOCK_HAND_LIFT, 0)
        .scale(DOCK_HAND_SCALE)
        .end()
        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
        .rotation(0, HAND_TURN, 0)
        .scale(DOCK_FIRST_PERSON_SCALE)
        .end();
  }

  /**
   * The desk is drawn by GeckoLib, from its left half, so its block model only gives the particles
   * of breaking, and its item is drawn by the same model (answer 1494).
   */
  private void engineeringDesk() {
    particlesOnly(
        CubitBlocks.ENGINEERING_DESK.get(),
        CubitBlocks.ENGINEERING_DESK_ID,
        CubitBlocks.ENGINEERING_DESK_ID);
    deskItem();
  }

  /** The item of the desk, drawn by GeckoLib, placed in each view like a block of the game. */
  private void deskItem() {
    itemModels()
        .getBuilder(CubitBlocks.ENGINEERING_DESK_ID)
        .parent(new ModelFile.UncheckedModelFile(BUILTIN_ENTITY))
        .transforms()
        .transform(ItemDisplayContext.GUI)
        .rotation(GUI_TILT, GUI_TURN, 0)
        .scale(GUI_SCALE)
        .end()
        .transform(ItemDisplayContext.GROUND)
        .translation(0, GROUND_LIFT, 0)
        .scale(GROUND_SCALE)
        .end()
        .transform(ItemDisplayContext.FIXED)
        .scale(FIXED_SCALE)
        .end()
        .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
        .rotation(HAND_TILT, HAND_TURN, 0)
        .translation(0, HAND_LIFT, 0)
        .scale(HAND_SCALE)
        .end()
        .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
        .rotation(0, HAND_TURN, 0)
        .scale(FIRST_PERSON_SCALE)
        .end()
        .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
        .rotation(0, GUI_TURN, 0)
        .scale(FIRST_PERSON_SCALE)
        .end();
  }
}
