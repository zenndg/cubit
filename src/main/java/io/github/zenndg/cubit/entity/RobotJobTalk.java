package io.github.zenndg.cubit.entity;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.domain.behavior.ChatRules;
import io.github.zenndg.cubit.domain.behavior.JobTalkRules;
import io.github.zenndg.cubit.domain.behavior.JobTalkTopic;
import io.github.zenndg.cubit.domain.behavior.NoticeRepeat;
import io.github.zenndg.cubit.domain.behavior.NoticeRules;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.robot.RobotJob;
import io.github.zenndg.cubit.menu.WorkKeys;
import io.github.zenndg.cubit.network.CubitNetwork;
import io.github.zenndg.cubit.registry.CubitSounds;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * What a Cubit with a card says: a phrase of its job when a card goes in and the screen closes, and
 * phrases of its work, to the owner nearby (answers 1583 to 1590). The rules live in the domain,
 * and nothing here is saved, so a reloaded Cubit may speak again.
 */
public final class RobotJobTalk {
  public static final String FORMAT_KEY = "notice.cubit.job_format";
  private static final String KEY = "notice.cubit.job.%s.%d";

  private final RobotEntity robot;
  private NoticeRepeat repeat = NoticeRepeat.READY;
  private Optional<RobotJob> jobAtOpen = Optional.empty();

  RobotJobTalk(RobotEntity robot) {
    this.robot = robot;
  }

  public static String keyOf(JobTalkTopic topic, int phrase) {
    return KEY.formatted(topic.name().toLowerCase(Locale.ROOT), phrase);
  }

  void tick() {
    repeat = repeat.tick();
  }

  /** Whether the Cubit spoke of its work less than an interval ago. */
  public boolean isHolding() {
    return !repeat.canShow();
  }

  void screenOpened() {
    jobAtOpen = robot.workArea().work().job();
  }

  /** The phrase of a new card goes to whoever closed the screen (answers 1585 and 1589). */
  void screenClosed(Player player) {
    Optional<RobotJob> now = robot.workArea().work().job();
    boolean newJob = JobTalkRules.isNewJob(jobAtOpen.orElse(null), now.orElse(null));
    jobAtOpen = now;
    if (newJob && player instanceof ServerPlayer viewer && mayTalk()) {
      now.flatMap(JobTalkTopic::entryOf).ifPresent(topic -> say(viewer, topic));
    }
  }

  /** A moment of the work: with a chance and an interval, to the owner nearby (answer 1586). */
  public void onWork(JobTalkTopic topic) {
    onWork(topic, robot.getRandom().nextInt(Percentage.MAX));
  }

  /** The draw comes from the caller, so a test can fix it, as in the chatter. */
  public void onWork(JobTalkTopic topic, int roll) {
    if (!repeat.canShow() || !mayTalk()) {
      return;
    }
    if (!JobTalkRules.drawSpeaks(roll, CubitMod.CONFIG.chatter().jobs().chancePercent())) {
      return;
    }
    ownerNearby()
        .ifPresent(owner -> {
          say(owner, topic);
          repeat = repeat.shown(CubitMod.CONFIG.chatter().jobs().intervalTicks());
        });
  }

  private boolean mayTalk() {
    return NoticeRules.speaks(robot.powerState())
        && JobTalkRules.mayTalk(
            robot.chatter().switchedOn(), ChatRules.isCalm(RobotFace.expression(robot)));
  }

  private Optional<ServerPlayer> ownerNearby() {
    MinecraftServer server = robot.level().getServer();
    if (server == null) {
      return Optional.empty();
    }
    return robot
        .ownerId()
        .map(server.getPlayerList()::getPlayer)
        .filter(owner ->
            JobTalkRules.hears(
                owner.level() == robot.level(),
                owner.distanceTo(robot),
                CubitMod.CONFIG.chatter().jobs().rangeBlocks()));
  }

  /** The name of the Cubit and its job come first, so the owner knows who spoke (1588). */
  private void say(ServerPlayer player, JobTalkTopic topic) {
    int phrase = robot.getRandom().nextInt(topic.phrases());
    Component job = Component.translatable(WorkKeys.job(topic.job()));
    Component text = Component.translatable(keyOf(topic, phrase));
    CubitNetwork.sendNotice(player, Component.translatable(FORMAT_KEY, robot.getName(), job, text));
    robot.playSound(CubitSounds.EXPRESSION_BEEP.get());
  }
}
