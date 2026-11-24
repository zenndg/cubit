package io.github.zenndg.cubit.domain.robot;

import io.github.zenndg.cubit.domain.energy.Battery;
import io.github.zenndg.cubit.domain.energy.Energy;
import io.github.zenndg.cubit.domain.energy.Percentage;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.domain.tier.TierSpec;
import io.github.zenndg.cubit.domain.xp.XpState;

/**
 * What a robot keeps when it becomes an item. The config may change while a robot is stored, so the
 * numbers are only valid after being fitted to a {@link TierSpec}.
 */
public record RobotState(
    RobotTier tier,
    Energy energy,
    float health,
    boolean lampOn,
    RobotColor color,
    boolean scannersOn,
    OreFilter oreFilter,
    boolean chatterOn,
    DefenseMode defenseMode,
    boolean solarOn,
    PickupSettings pickup,
    boolean torchesOn,
    boolean autoReturnOn,
    boolean unloadOn,
    boolean deathPointOn,
    WorkSettings workSettings,
    SpeedChoice speed,
    boolean introduced,
    XpState xp) {
  private static final String INVALID_HEALTH_MESSAGE = "Invalid robot health: ";
  private static final float MIN_PLACED_HEALTH = 1f;

  public RobotState {
    if (Float.isNaN(health) || health < 0) {
      throw new IllegalArgumentException(INVALID_HEALTH_MESSAGE + health);
    }
  }

  /**
   * The switches that came later start on, the ones of the jobs as at first (1191), and the speed
   * whole (1266), also for what was saved before they existed. Such a robot has been placed
   * already, so it counts as introduced (1679).
   */
  public RobotState(
      RobotTier tier,
      Energy energy,
      float health,
      boolean lampOn,
      RobotColor color,
      boolean scannersOn,
      OreFilter oreFilter,
      boolean chatterOn,
      DefenseMode defenseMode,
      boolean solarOn,
      PickupSettings pickup,
      boolean torchesOn) {
    this(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        true,
        true,
        true,
        WorkSettings.INITIAL,
        SpeedChoice.FULL,
        true,
        XpState.EMPTY);
  }

  /** Peaceful, with the solar panel and the torches on and the first pickup, as before them. */
  public RobotState(
      RobotTier tier,
      Energy energy,
      float health,
      boolean lampOn,
      RobotColor color,
      boolean scannersOn,
      OreFilter oreFilter,
      boolean chatterOn) {
    this(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        DefenseMode.INITIAL,
        true,
        PickupSettings.INITIAL,
        true);
  }

  /** The lamp starts switched on, also for what was saved before the lamp existed. */
  public RobotState(RobotTier tier, Energy energy, float health) {
    this(tier, energy, health, true);
  }

  /** The shell starts white, also for what was saved before the colors existed. */
  public RobotState(RobotTier tier, Energy energy, float health, boolean lampOn) {
    this(tier, energy, health, lampOn, RobotColor.WHITE);
  }

  /** The scanners start on, looking for nothing in particular, and the chatter on, as before. */
  public RobotState(RobotTier tier, Energy energy, float health, boolean lampOn, RobotColor color) {
    this(tier, energy, health, lampOn, color, true, OreFilter.EMPTY, true);
  }

  /** A new robot has not been placed yet, so its first placement introduces it (1750). */
  public static RobotState initial(RobotTier tier, TierSpec spec, Percentage initialCharge) {
    return new RobotState(tier, spec.newBattery(initialCharge).charge(), spec.maxHealth())
        .withIntroduced(false);
  }

  /** What a robot keeps when it dies: the tier, the looks and the XP, without energy or health. */
  public RobotState damaged() {
    return with(Energy.ZERO, 0f);
  }

  public RobotState repaired(TierSpec spec, Percentage health) {
    return with(Energy.ZERO, (float) spec.maxHealth() * health.value() / Percentage.MAX);
  }

  /**
   * The same robot with another charge, as the recharge in the crafting grid gives (answer 181).
   */
  public RobotState withEnergy(Energy next) {
    return with(next, health);
  }

  private RobotState with(Energy nextEnergy, float nextHealth) {
    return new RobotState(
        tier,
        nextEnergy,
        nextHealth,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withPickup(PickupSettings next) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        next,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withTorches(boolean on) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        on,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withAutoReturn(boolean on) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        on,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withUnload(boolean on) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        on,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withDeathPoint(boolean on) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        on,
        workSettings,
        speed,
        introduced,
        xp);
  }

  public RobotState withWorkSettings(WorkSettings on) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        on,
        speed,
        introduced,
        xp);
  }

  /** The same robot walking at another percentage of its tier (answer 1266). */
  public RobotState withSpeed(SpeedChoice next) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        next,
        introduced,
        xp);
  }

  public RobotState withIntroduced(boolean next) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        next,
        xp);
  }

  /** The same robot with another tank, mode or switch of the excess (answer 1838). */
  public RobotState withXp(XpState next) {
    return new RobotState(
        tier,
        energy,
        health,
        lampOn,
        color,
        scannersOn,
        oreFilter,
        chatterOn,
        defenseMode,
        solarOn,
        pickup,
        torchesOn,
        autoReturnOn,
        unloadOn,
        deathPointOn,
        workSettings,
        speed,
        introduced,
        next);
  }

  public Battery batteryFor(TierSpec spec) {
    return new Battery(spec.batteryCapacity(), energy.min(spec.batteryCapacity()));
  }

  /** Never below the minimum: a robot placed without health would die immediately. */
  public float healthFor(TierSpec spec) {
    return Math.clamp(health, MIN_PLACED_HEALTH, (float) spec.maxHealth());
  }
}
