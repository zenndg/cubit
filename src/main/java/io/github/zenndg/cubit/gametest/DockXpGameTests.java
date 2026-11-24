package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.DockXpTelling;
import io.github.zenndg.cubit.block.InductionDockBlockEntity;
import io.github.zenndg.cubit.domain.dock.DockXpMode;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.robot.RobotState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.domain.xp.PlayerXp;
import io.github.zenndg.cubit.domain.xp.XpSettings;
import io.github.zenndg.cubit.domain.xp.XpState;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotScreenReadout;
import io.github.zenndg.cubit.entity.RobotSnapshotReader;
import io.github.zenndg.cubit.menu.DockXpMenuPart;
import io.github.zenndg.cubit.menu.InductionDockMenu;
import io.github.zenndg.cubit.menu.TabletRobotMenu;
import io.github.zenndg.cubit.player.RobotSnapshot;
import io.github.zenndg.cubit.registry.CubitDataComponents;
import io.github.zenndg.cubit.registry.CubitFluids;
import io.github.zenndg.cubit.registry.CubitItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The XP of the Dock: its modes, the trade, the pipes, the saves and the Tablet (2099 to 2117). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DockXpGameTests {
  private static final String ARENA = "arena";
  private static final int TIMEOUT = 600;
  private static final int WAIT_TICKS = 40;
  private static final Energy PLENTY = new Energy(250_000);
  private static final Energy FULL_CHARGE = new Energy(300_000);
  private static final Energy CHARGE = new Energy(100_000);
  private static final float HEALTH = 25f;
  private static final int CUBIT_CAPACITY = 8_000;
  private static final int DOCK_CAPACITY = 32_000;
  private static final int RATE = 20;
  private static final int FE = 9;
  private static final long ENERGY_OF_A_CUBIT = (long) CUBIT_CAPACITY / RATE * FE;
  private static final int LEVEL = 5;
  private static final int ODD = 7;
  private static final int AWAY = 4;
  private static final double FAR = 200;
  private static final String MODE_KEY = "XpMode";
  private static final String MADE_KEY = "XpEnergyMade";
  private static final String SNAPSHOT_KEY = "XpTank";
  private static final String UNKNOWN = "from_the_future";
  private static final String WRONG_FLUID = "Wrong fluid in the Dock";
  private static final String WRONG_ENERGY = "Wrong energy made by the XP";
  private static final String NOT_EMPTY = "The Cubit should empty its tank";
  private static final String NOT_LEFT = "The Cubit should leave the Dock";
  private static final String WRONG_MODE = "Wrong mode of the Dock";
  private static final String NOT_MOVED = "A level should move";
  private static final String MOVED = "Nothing should move";
  private static final String LOST = "XP was lost or made";
  private static final String NOT_KEPT = "The XP should be kept";
  private static final String WRONG_PIPE = "The pipe moved a wrong amount";
  private static final String WRONG_CALL = "Wrong answer of Unload XP";
  private static final String WRONG_TELLING = "Only the owner near should be told";

  private DockXpGameTests() {}

  private static InductionDockBlockEntity dock(GameTestHelper helper, Player owner) {
    return DockScene.placeDock(helper, owner, DockScene.FLOOR, PLENTY);
  }

  private static ItemStack cubit(int fluid, Energy energy) {
    ItemStack item = new ItemStack(CubitItems.GENERAL_ROBOT.get());
    item.set(
        CubitDataComponents.ROBOT_STATE.get(),
        new RobotState(RobotTier.BASE, energy, HEALTH)
            .withXp(new XpState(fluid, XpSettings.INITIAL)));
    return item;
  }

  private static RobotEntity docked(GameTestHelper helper, ServerPlayer owner) {
    DockScene.useOnDock(helper, owner, cubit(CUBIT_CAPACITY, FULL_CHARGE));
    return GameTestRobots.robots(helper).getFirst();
  }

  private static int fluid(RobotEntity robot) {
    return robot.pickup().xp().tank().fluid();
  }

  private static void fill(InductionDockBlockEntity dock, int fluid) {
    dock.xp().setTank(new XpTank(fluid, dock.xp().tank().capacity()));
  }

  private static void assertLeft(GameTestHelper helper, RobotEntity robot) {
    helper.assertValueEqual(fluid(robot), 0, NOT_EMPTY);
    helper.assertTrue(robot.powerState() != PowerState.HIBERNATING, NOT_LEFT);
  }

  /** What arrives is deleted, and what was stored stays (answers 1831 and 1935). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void deletingAllKeepsWhatWasStored(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, RATE);
    dock.xp().choose(owner, DockXpMode.DELETE_ALL);
    RobotEntity robot = docked(helper, owner);

    helper.succeedWhen(() -> {
      assertLeft(helper, robot);
      helper.assertValueEqual(dock.xp().tank().fluid(), RATE, WRONG_FLUID);
      helper.assertValueEqual(dock.xp().energyMade(), Energy.ZERO, WRONG_ENERGY);
    });
  }

  /** Every point becomes 9 FE, and the Cubit leaves with nothing (answers 1942 and 2104). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void convertingAllTurnsTheXpIntoEnergy(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    dock.xp().choose(owner, DockXpMode.CONVERT_ALL);
    RobotEntity robot = docked(helper, owner);

    helper.succeedWhen(() -> {
      assertLeft(helper, robot);
      helper.assertValueEqual(dock.xp().tank().fluid(), 0, WRONG_FLUID);
      helper.assertValueEqual(dock.xp().energyMade().amount(), ENERGY_OF_A_CUBIT, WRONG_ENERGY);
    });
  }

  /** A full Dock still takes it all, and by default converts it (answers 1831 and 1832). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFullDockConvertsOnlyTheExcess(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, DOCK_CAPACITY);
    RobotEntity robot = docked(helper, owner);

    helper.succeedWhen(() -> {
      assertLeft(helper, robot);
      helper.assertValueEqual(dock.xp().tank().fluid(), DOCK_CAPACITY, WRONG_FLUID);
      helper.assertValueEqual(dock.xp().energyMade().amount(), ENERGY_OF_A_CUBIT, WRONG_ENERGY);
    });
  }

  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void aFullDockMayDeleteTheExcess(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, DOCK_CAPACITY);
    dock.xp().choose(owner, DockXpMode.STORE_DELETE_EXCESS);
    RobotEntity robot = docked(helper, owner);

    helper.succeedWhen(() -> {
      assertLeft(helper, robot);
      helper.assertValueEqual(dock.xp().tank().fluid(), DOCK_CAPACITY, WRONG_FLUID);
      helper.assertValueEqual(dock.xp().energyMade(), Energy.ZERO, WRONG_ENERGY);
    });
  }

  /** Only Convert all spends what is stored, with no Cubit in the Dock (2101 and detail 13). */
  @GameTest(template = ARENA, timeoutTicks = TIMEOUT)
  public static void onlyConvertAllSpendsWhatIsStored(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, CUBIT_CAPACITY + ODD);

    helper.runAfterDelay(
        WAIT_TICKS,
        () -> {
          helper.assertValueEqual(dock.xp().tank().fluid(), CUBIT_CAPACITY + ODD, WRONG_FLUID);
          helper.assertTrue(dock.xp().choose(owner, DockXpMode.CONVERT_ALL), WRONG_MODE);
          helper.succeedWhen(() -> {
            helper.assertValueEqual(dock.xp().tank().fluid(), ODD, WRONG_FLUID);
            helper.assertValueEqual(
                dock.xp().energyMade().amount(), ENERGY_OF_A_CUBIT, WRONG_ENERGY);
          });
        });
  }

  /** The Dock trades as the Cubit, whatever the mode (answers 2072 and detail 14). */
  @GameTest(template = ARENA)
  public static void aLevelGoesToTheDockAndBack(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    dock.xp().choose(owner, DockXpMode.DELETE_ALL);
    owner.setExperienceLevels(LEVEL);
    owner.setExperiencePoints(0);
    int level = PlayerXp.neededAt(LEVEL - 1);

    helper.assertTrue(dock.xp().send(owner), NOT_MOVED);
    helper.assertValueEqual(dock.xp().tank().fluid(), level * RATE, WRONG_FLUID);
    helper.assertValueEqual(owner.experienceLevel, LEVEL - 1, NOT_MOVED);
    helper.assertTrue(dock.xp().receive(owner), NOT_MOVED);
    helper.assertValueEqual(dock.xp().tank().fluid(), 0, LOST);
    helper.assertValueEqual(owner.experienceLevel, LEVEL, LOST);
    helper.succeed();
  }

  /** The buttons of the screen ask the server, which checks the owner (rule 20). */
  @GameTest(template = ARENA)
  public static void onlyTheOwnerUsesTheXpOfTheDock(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    ServerPlayer stranger = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, CUBIT_CAPACITY);
    stranger.setExperienceLevels(LEVEL);
    int convertAll = DockXpMenuPart.FIRST_MODE_BUTTON + DockXpMode.CONVERT_ALL.ordinal();
    int deleteAll = DockXpMenuPart.FIRST_MODE_BUTTON + DockXpMode.DELETE_ALL.ordinal();
    InductionDockMenu menu = InductionDockMenu.onServer(0, owner.getInventory(), dock);

    for (int button = DockXpMenuPart.TAKE_BUTTON; button <= convertAll; button++) {
      helper.assertFalse(menu.clickMenuButton(stranger, button), MOVED);
    }
    helper.assertFalse(
        dock.xp().send(stranger)
            || dock.xp().receive(stranger)
            || dock.xp().choose(stranger, DockXpMode.DELETE_ALL),
        MOVED);
    helper.assertTrue(menu.clickMenuButton(owner, deleteAll), WRONG_MODE);
    helper.assertValueEqual(dock.xp().mode(), DockXpMode.DELETE_ALL, WRONG_MODE);
    helper.assertTrue(menu.clickMenuButton(owner, DockXpMenuPart.TAKE_BUTTON), NOT_MOVED);
    helper.succeed();
  }

  /** Any amount of the fluid only, into the free room and out (answers 1936 and 2102). */
  @GameTest(template = ARENA)
  public static void pipesMoveOnlyTheXpFluid(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    dock.xp().choose(owner, DockXpMode.DELETE_ALL);
    IFluidHandler pipe = pipe(helper, DockScene.DOCK);
    FluidStack odd = new FluidStack(CubitFluids.XP_FLUID.get(), ODD);

    helper.assertValueEqual(pipe.fill(odd, FluidAction.SIMULATE), ODD, WRONG_PIPE);
    helper.assertValueEqual(dock.xp().tank().fluid(), 0, WRONG_PIPE);
    helper.assertValueEqual(pipe.fill(odd, FluidAction.EXECUTE), ODD, WRONG_PIPE);
    FluidStack water = new FluidStack(Fluids.WATER, RATE);
    helper.assertValueEqual(pipe.fill(water, FluidAction.EXECUTE), 0, WRONG_PIPE);
    helper.assertTrue(pipe.drain(water, FluidAction.EXECUTE).isEmpty(), WRONG_PIPE);

    fill(dock, DOCK_CAPACITY - 1);
    helper.assertValueEqual(pipe.fill(odd, FluidAction.EXECUTE), 1, WRONG_PIPE);
    helper.assertValueEqual(pipe.fill(odd, FluidAction.EXECUTE), 0, WRONG_PIPE);
    IFluidHandler upper = pipe(helper, DockScene.DOCK.above());
    helper.assertValueEqual(upper.drain(ODD, FluidAction.EXECUTE).getAmount(), ODD, WRONG_PIPE);
    helper.assertValueEqual(dock.xp().tank().fluid(), DOCK_CAPACITY - ODD, WRONG_PIPE);
    helper.succeed();
  }

  private static IFluidHandler pipe(GameTestHelper helper, BlockPos block) {
    return helper
        .getLevel()
        .getCapability(Capabilities.FluidHandler.BLOCK, helper.absolutePos(block), null);
  }

  /** In the save, in the item and, without the keys, as a new Dock (answers 1832 and 1838). */
  @GameTest(template = ARENA)
  public static void theModeAndTheEnergyStayInTheSaveAndInTheItem(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);
    fill(dock, DOCK_CAPACITY);
    dock.receiveXp(1);
    dock.xp().choose(owner, DockXpMode.STORE_DELETE_EXCESS);
    var item = dock.collectComponents().get(CubitDataComponents.DOCK_XP.get());
    helper.assertTrue(
        item.fluid() == DOCK_CAPACITY
            && item.mode() == DockXpMode.STORE_DELETE_EXCESS
            && item.energyMade().made().amount() == FE,
        NOT_KEPT);

    CompoundTag saved = dock.saveWithoutMetadata(helper.getLevel().registryAccess());
    dock.xp().choose(owner, DockXpMode.CONVERT_ALL);
    load(helper, dock, saved);
    helper.assertValueEqual(dock.xp().mode(), DockXpMode.STORE_DELETE_EXCESS, NOT_KEPT);
    helper.assertValueEqual(dock.xp().energyMade().amount(), (long) FE, NOT_KEPT);

    saved.putString(MODE_KEY, UNKNOWN);
    saved.putLong(MADE_KEY, -1);
    assertNew(helper, dock, saved);
    saved.remove(MODE_KEY);
    saved.remove(MADE_KEY);
    assertNew(helper, dock, saved);
    helper.succeed();
  }

  private static void load(
      GameTestHelper helper, InductionDockBlockEntity dock, CompoundTag saved) {
    dock.loadWithComponents(saved, helper.getLevel().registryAccess());
  }

  private static void assertNew(
      GameTestHelper helper, InductionDockBlockEntity dock, CompoundTag saved) {
    load(helper, dock, saved);
    helper.assertValueEqual(dock.xp().mode(), DockXpMode.INITIAL, WRONG_MODE);
    helper.assertValueEqual(dock.xp().energyMade(), Energy.ZERO, WRONG_ENERGY);
    helper.assertValueEqual(dock.xp().tank().fluid(), DOCK_CAPACITY, NOT_KEPT);
  }

  /** The Tablet sends the Cubit with XP, only for the owner, and trades nothing (2075, 2107). */
  @GameTest(template = ARENA)
  public static void unloadXpSendsOnlyACubitWithXp(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    ServerPlayer stranger = DockScene.ownerBeside(helper);
    dock(helper, owner);
    BlockPos floor = DockScene.FLOOR.west(AWAY);
    RobotEntity robot = GameTestRobots.placeRobot(helper, owner, cubit(0, CHARGE), floor);
    TabletRobotMenu menu = RobotScreenReadout.tabletMenu(0, owner.getInventory(), robot);

    helper.assertFalse(unload(menu, owner), WRONG_CALL);
    robot.pickup().xp().setState(new XpState(RATE, XpSettings.INITIAL));
    helper.assertFalse(unload(menu, stranger), WRONG_CALL);
    helper.assertTrue(unload(menu, owner), WRONG_CALL);
    helper.succeed();
  }

  private static boolean unload(TabletRobotMenu menu, ServerPlayer player) {
    return menu.clickMenuButton(player, TabletRobotMenu.UNLOAD_XP_BUTTON);
  }

  /** The snapshot keeps the XP, and one from before it shows none (answer 2106, detail 20). */
  @GameTest(template = ARENA)
  public static void theSnapshotKeepsTheXp(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    RobotEntity robot =
        GameTestRobots.placeRobot(helper, owner, cubit(RATE, CHARGE), DockScene.FLOOR.west(AWAY));
    CompoundTag saved = RobotSnapshotReader.of(robot).orElseThrow().save();

    Optional<XpTank> kept = RobotSnapshot.load(saved).orElseThrow().xp();
    helper.assertValueEqual(kept, Optional.of(new XpTank(RATE, CUBIT_CAPACITY)), NOT_KEPT);
    saved.remove(SNAPSHOT_KEY);
    helper.assertTrue(RobotSnapshot.load(saved).orElseThrow().xp().isEmpty(), NOT_KEPT);
    helper.succeed();
  }

  /** Near the Dock only, so the notice waits for an owner who is away (answer 2103). */
  @GameTest(template = ARENA)
  public static void onlyTheOwnerNearIsToldOfTheFullTank(GameTestHelper helper) {
    ServerPlayer owner = DockScene.ownerBeside(helper);
    InductionDockBlockEntity dock = dock(helper, owner);

    helper.assertTrue(told(helper, dock), WRONG_TELLING);
    owner.moveTo(owner.getX() + FAR, owner.getY(), owner.getZ());
    helper.assertFalse(told(helper, dock), WRONG_TELLING);
    helper.succeed();
  }

  private static boolean told(GameTestHelper helper, InductionDockBlockEntity dock) {
    return DockXpTelling.tell(
        helper.getLevel(), dock.getBlockPos(), dock.owner(), dock.identity().name());
  }
}
