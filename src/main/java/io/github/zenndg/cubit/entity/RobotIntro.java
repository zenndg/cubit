package io.github.zenndg.cubit.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.config.IntroSection;
import io.github.zenndg.cubit.domain.robot.IntroLink;
import io.github.zenndg.cubit.registry.CubitAttachments;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

/**
 * What a Cubit says the first time it is placed: the help above the hotbar, always, and the link in
 * the chat, at most once per cooldown for each player (answers 1672 to 1682 and 1750 to 1753).
 */
public final class RobotIntro {
  public static final String HELP_KEY = "notice.cubit.intro.help";
  public static final String LINK_KEY = "notice.cubit.intro.link";
  public static final Codec<IntroLink> LINK_CODEC =
      Codec.LONG.xmap(RobotIntro::savedLink, IntroLink::lastShown);
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String INVALID_LINK_MESSAGE = "Invalid tick of the last link: {}";

  private RobotIntro() {}

  /**
   * Marked before anything is said, so a robot that stays quiet, by the switch or by the cooldown,
   * never speaks later (answers 1678 and 1753).
   */
  public static void greet(RobotEntity robot, Player player) {
    RobotChatter chatter = robot.chatter();
    if (chatter.introduced()) {
      return;
    }
    chatter.setIntroduced(true);
    if (!chatter.switchedOn() || !(player instanceof ServerPlayer server)) {
      return;
    }
    server.displayClientMessage(Component.translatable(HELP_KEY), true);
    showTheLinkIfDue(server);
  }

  private static void showTheLinkIfDue(ServerPlayer player) {
    IntroSection config = CubitMod.CONFIG.chatter().intro();
    long now = player.serverLevel().getGameTime();
    IntroLink last = player.getData(CubitAttachments.INTRO_LINK_TIME);
    if (!last.isDue(now, config.cooldownTicks())) {
      return;
    }
    player.setData(CubitAttachments.INTRO_LINK_TIME, last.shownAt(now));
    player.sendSystemMessage(Component.translatable(LINK_KEY, link(config.url())));
  }

  /** Underlined, and a click opens the browser, after the confirmation screen of the game. */
  private static Component link(String url) {
    return Component.literal(url)
        .withStyle(style ->
            style
                .withUnderlined(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
  }

  /** A broken save shows the link again, which is safer than never showing it (rule 17). */
  private static IntroLink savedLink(long tick) {
    if (tick < IntroLink.NEVER_SHOWN) {
      LOGGER.warn(INVALID_LINK_MESSAGE, tick);
      return IntroLink.NEVER;
    }
    return new IntroLink(tick);
  }
}
