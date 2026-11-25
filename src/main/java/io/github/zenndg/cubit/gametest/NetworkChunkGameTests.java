package io.github.zenndg.cubit.gametest;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.block.NetworkData;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.network.ChunkRoster;
import io.github.zenndg.cubit.domain.network.ChunkState;
import io.github.zenndg.cubit.domain.network.UpsReserve;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The chunk of a Network Server (answers 2209 to 2216, 2369 and 2372). */
@GameTestHolder(CubitMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkChunkGameTests {
  private static final int FIRST_CHECK = 5;

  /** The check runs once a second. */
  private static final int NEXT_CHECK = 45;

  private static final int PAID_TICKS = 10;
  private static final int PER_PLAYER = 48;
  private static final long COST = 25;
  private static final int FAR = 1_000_000;
  private static final int CHUNK_BLOCKS = 16;
  private static final Energy PLENTY = new Energy(1_000_000);
  private static final Energy TWO_TICKS = new Energy(50);
  private static final Energy BELOW_RESUME = new Energy(29_000);
  private static final Energy ABOVE_RESUME = new Energy(31_000);
  private static final int MAX_UPS = 50;
  private static final String ENERGY_KEY = "Energy";
  private static final String DATA_FILE = "chunks";
  private static final String WRONG_STATE = "Wrong state of the chunk";
  private static final String NO_TICKET = "The chunk should have a ticket";
  private static final String TICKET = "The chunk should have no ticket";
  private static final String WRONG_LIST = "Wrong list of chunks in the data of the world";
  private static final String WRONG_COST = "Wrong energy paid for the chunk";
  private static final String NOT_LOADED = "The data of the world should come back as it was";

  private NetworkChunkGameTests() {}

  @GameTest(template = NetworkScene.CHUNK)
  public static void theServerHoldsItsChunkAndPaysForIt(GameTestHelper helper) {
    NetworkServerBlockEntity server = place(helper, owner(helper), PLENTY);

    helper.runAfterDelay(
        PAID_TICKS,
        () -> {
          helper.assertValueEqual(server.chunkState(), ChunkState.HOLDING, WRONG_STATE);
          helper.assertTrue(hasTicket(helper), NO_TICKET);
          helper.assertTrue(chunks(helper).holds(where(helper)), WRONG_LIST);
          long paid = PLENTY.amount() - server.energy().amount();
          helper.assertTrue(paid > 0 && paid % COST == 0, WRONG_COST);
          done(helper);
        });
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void theSwitchLetsTheChunkGo(GameTestHelper helper) {
    NetworkServerBlockEntity server = place(helper, owner(helper), PLENTY);

    helper.runAfterDelay(
        FIRST_CHECK,
        () -> {
          server.setChunkLoading(false);
          helper.runAfterDelay(
              FIRST_CHECK,
              () -> {
                assertReleased(helper, server, ChunkState.SWITCHED_OFF);
                Energy left = server.energy();
                helper.runAfterDelay(
                    PAID_TICKS,
                    () -> {
                      helper.assertValueEqual(server.energy(), left, WRONG_COST);
                      done(helper);
                    });
              });
        });
  }

  /** It spends the whole reserve, which is only for the chunk, and then lets go (2209, 2372). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void withoutEnergyTheChunkGoesAndOnlyComesBackWithEnough(GameTestHelper helper) {
    NetworkServerBlockEntity server = place(helper, owner(helper), TWO_TICKS);
    server.setUps(new UpsReserve(MAX_UPS));

    helper.runAfterDelay(
        PAID_TICKS,
        () -> {
          assertReleased(helper, server, ChunkState.NO_ENERGY);
          helper.assertTrue(server.energy().isZero(), WRONG_COST);
          fill(helper, server, BELOW_RESUME);
          helper.runAfterDelay(
              NEXT_CHECK,
              () -> {
                assertReleased(helper, server, ChunkState.NO_ENERGY);
                fill(helper, server, ABOVE_RESUME);
                helper.runAfterDelay(
                    NEXT_CHECK,
                    () -> {
                      helper.assertValueEqual(server.chunkState(), ChunkState.HOLDING, WRONG_STATE);
                      done(helper);
                    });
              });
        });
  }

  /** Who ran out remembers it through a save (answer 2404). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aSavedServerStaysDrained(GameTestHelper helper) {
    NetworkServerBlockEntity server = place(helper, owner(helper), Energy.ZERO);

    helper.runAfterDelay(
        FIRST_CHECK,
        () -> {
          HolderLookup.Provider registries = helper.getLevel().registryAccess();
          CompoundTag saved = server.saveWithoutMetadata(registries);
          saved.putLong(ENERGY_KEY, BELOW_RESUME.amount());
          helper.destroyBlock(NetworkScene.serverFloor(helper).above());
          NetworkServerBlockEntity loaded = place(helper, owner(helper), Energy.ZERO);
          loaded.loadWithComponents(saved, registries);
          helper.runAfterDelay(
              NEXT_CHECK,
              () -> {
                assertReleased(helper, loaded, ChunkState.NO_ENERGY);
                done(helper);
              });
        });
  }

  /** The owner of this one is not in the list of players, as one that left (answer 2210). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anOwnerThatIsNotThereHoldsNothingAndPaysNothing(GameTestHelper helper) {
    NetworkServerBlockEntity server =
        NetworkScene.placeServer(
            helper, helper.makeMockPlayer(GameType.SURVIVAL), RobotTier.BASE, PLENTY);

    helper.runAfterDelay(
        PAID_TICKS,
        () -> {
          assertReleased(helper, server, ChunkState.OWNER_OFFLINE);
          helper.assertValueEqual(server.energy(), PLENTY, WRONG_COST);
          done(helper);
        });
  }

  /** When the owner leaves, the chunk goes and waits for the return (answers 2210 and 2407). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void anOwnerThatLeavesKeepsThePlaceInLine(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkServerBlockEntity server = place(helper, owner, PLENTY);

    helper.runAfterDelay(
        FIRST_CHECK,
        () -> {
          helper.assertValueEqual(server.chunkState(), ChunkState.HOLDING, WRONG_STATE);
          helper.getLevel().getServer().getPlayerList().remove(owner);
          helper.runAfterDelay(
              NEXT_CHECK,
              () -> {
                helper.assertValueEqual(server.chunkState(), ChunkState.OWNER_OFFLINE, WRONG_STATE);
                helper.assertFalse(hasTicket(helper), TICKET);
                helper.assertValueEqual(
                    chunks(helper).waitingOf(owner.getUUID()), List.of(where(helper)), WRONG_LIST);
                done(helper);
              });
        });
  }

  /** Above the limit the server works without its chunk (answer 2215). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void aboveTheLimitOfThePlayerNothingIsHeld(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    ChunkRoster<GlobalPos> chunks = chunks(helper);
    for (int index = 0; index < PER_PLAYER; index++) {
      chunks.hold(elsewhere(helper, index), owner.getUUID());
    }
    NetworkServerBlockEntity server = place(helper, owner, PLENTY);

    helper.runAfterDelay(
        PAID_TICKS,
        () -> {
          assertReleased(helper, server, ChunkState.PLAYER_LIMIT);
          helper.assertValueEqual(server.energy(), PLENTY, WRONG_COST);
          for (int index = 0; index < PER_PLAYER; index++) {
            chunks.release(elsewhere(helper, index));
          }
          done(helper);
        });
  }

  @GameTest(template = NetworkScene.CHUNK)
  public static void breakingTheServerLetsTheChunkGo(GameTestHelper helper) {
    place(helper, owner(helper), PLENTY);

    helper.runAfterDelay(
        FIRST_CHECK,
        () -> {
          helper.destroyBlock(NetworkScene.serverFloor(helper).above());

          helper.assertFalse(hasTicket(helper), TICKET);
          helper.assertFalse(chunks(helper).holds(where(helper)), WRONG_LIST);
          helper.succeed();
        });
  }

  /** Only the names of the contract, and nothing is lost (answer 2407). */
  @GameTest(template = NetworkScene.CHUNK)
  public static void theDataOfTheWorldComesBack(GameTestHelper helper) {
    ServerPlayer owner = owner(helper);
    NetworkData data = new NetworkData();
    data.chunks().hold(elsewhere(helper, 0), owner.getUUID());
    data.chunks().hold(elsewhere(helper, 1), owner.getUUID());
    data.chunks().await(elsewhere(helper, 1));
    HolderLookup.Provider registries = helper.getLevel().registryAccess();

    NetworkData loaded = NetworkData.load(data.save(new CompoundTag(), registries), registries);

    helper.assertValueEqual(loaded.chunks().entries(), data.chunks().entries(), NOT_LOADED);
    helper.succeed();
  }

  private static ServerPlayer owner(GameTestHelper helper) {
    return GameTestRobots.onlinePlayer(helper);
  }

  private static NetworkServerBlockEntity place(
      GameTestHelper helper, ServerPlayer owner, Energy energy) {
    return NetworkScene.placeServer(helper, owner, RobotTier.BASE, energy);
  }

  /** By the cables, a tick of input at a time, until it has that much. */
  private static void fill(GameTestHelper helper, NetworkServerBlockEntity server, Energy wanted) {
    if (server.energy().amount() < wanted.amount()) {
      int missing = (int) (wanted.amount() - server.energy().amount());
      server.energyStorage().receiveEnergy(missing, false);
      helper.runAfterDelay(1, () -> fill(helper, server, wanted));
    }
  }

  private static void assertReleased(
      GameTestHelper helper, NetworkServerBlockEntity server, ChunkState reason) {
    helper.assertValueEqual(server.chunkState(), reason, WRONG_STATE);
    helper.assertFalse(hasTicket(helper), TICKET);
    helper.assertFalse(chunks(helper).holds(where(helper)), WRONG_LIST);
  }

  /** No chunk stays held after the test. */
  private static void done(GameTestHelper helper) {
    helper.destroyBlock(NetworkScene.serverFloor(helper).above());
    helper.succeed();
  }

  private static ChunkRoster<GlobalPos> chunks(GameTestHelper helper) {
    return NetworkData.of(helper.getLevel().getServer()).chunks();
  }

  private static GlobalPos where(GameTestHelper helper) {
    BlockPos pos = helper.absolutePos(NetworkScene.serverFloor(helper).above());
    return GlobalPos.of(helper.getLevel().dimension(), pos);
  }

  private static GlobalPos elsewhere(GameTestHelper helper, int index) {
    BlockPos far = new BlockPos(FAR + index * CHUNK_BLOCKS, 0, FAR);
    return GlobalPos.of(Level.OVERWORLD, helper.absolutePos(BlockPos.ZERO).offset(far));
  }

  private static boolean hasTicket(GameTestHelper helper) {
    ForcedChunksSavedData data =
        helper
            .getLevel()
            .getDataStorage()
            .computeIfAbsent(ForcedChunksSavedData.factory(), DATA_FILE);
    long chunk = new ChunkPos(where(helper).pos()).toLong();
    return data.getBlockForcedChunks().getTickingChunks().values().stream()
        .anyMatch(tickets -> tickets.contains(chunk));
  }
}
