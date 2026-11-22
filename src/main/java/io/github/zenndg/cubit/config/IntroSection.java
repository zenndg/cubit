package io.github.zenndg.cubit.config;

import io.github.zenndg.cubit.domain.robot.IntroLink;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The config keys of the introduction of a new Cubit, beside the chatter. The names are a contract
 * with the pack (answers 1674, 1677 and 1750).
 */
public final class IntroSection {
  private static final String COOLDOWN = "intro_link_cooldown_ticks";
  private static final int COOLDOWN_DEFAULT = 12000;
  private static final int COOLDOWN_MIN = 0;
  private static final int COOLDOWN_MAX = 72000;
  private static final String URL = "intro_link_url";
  private static final String URL_DEFAULT = "https://github.com/zenndg/cubit";

  private final ModConfigSpec.IntValue cooldownTicks;
  private final ModConfigSpec.ConfigValue<String> url;

  /** An address that is not https goes back to the default, and the config loader logs it. */
  IntroSection(ModConfigSpec.Builder builder) {
    cooldownTicks = builder.defineInRange(COOLDOWN, COOLDOWN_DEFAULT, COOLDOWN_MIN, COOLDOWN_MAX);
    url =
        builder.define(
            URL,
            URL_DEFAULT,
            value -> value instanceof String text && IntroLink.isSafeAddress(text));
  }

  /** How long a player sees no other link after one, in ticks (answer 1677). */
  public int cooldownTicks() {
    return cooldownTicks.get();
  }

  /** Read again on use, and checked again, so an address changed at runtime is also safe. */
  public String url() {
    String address = url.get();
    return IntroLink.isSafeAddress(address) ? address : URL_DEFAULT;
  }
}
