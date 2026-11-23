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
import org.slf4j.Logger;

/**
 * What a Cubit says the first time it is placed, after the window of its name, with the name: the
 * help above the hotbar, always, and the link in the chat, at most once per cooldown for each
 * player (answers 1672 to 1682, 1750 to 1753 and 1880).
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
   * Marked before anything is said, so a robot that stays quiet, by the switch, by the cooldown or
   * by a window never answered, never speaks later (answers 1678 and 1753). True only the first
   * time.
   */
  static boolean markFirstPlacement(RobotEntity robot) {
    RobotChatter chatter = robot.chatter();
    if (chatter.introduced()) {
      return false;
    }
    chatter.setIntroduced(true);
    return true;
  }

  /** After the window of the name, with the name chosen (answer 1880). */
  static void greet(RobotEntity robot, ServerPlayer player) {
    if (!robot.chatter().switchedOn()) {
      return;
    }
    player.displayClientMessage(Component.translatable(HELP_KEY, robot.getName()), true);
    showTheLinkIfDue(robot, player);
  }

  private static void showTheLinkIfDue(RobotEntity robot, ServerPlayer player) {
    IntroSection config = CubitMod.CONFIG.chatter().intro();
    long now = player.serverLevel().getGameTime();
    IntroLink last = player.getData(CubitAttachments.INTRO_LINK_TIME);
    if (!last.isDue(now, config.cooldownTicks())) {
      return;
    }
    player.setData(CubitAttachments.INTRO_LINK_TIME, last.shownAt(now));
    player.sendSystemMessage(Component.translatable(LINK_KEY, robot.getName(), link(config.url())));
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
