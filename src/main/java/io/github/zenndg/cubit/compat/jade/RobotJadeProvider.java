package io.github.zenndg.cubit.compat.jade;

import io.github.zenndg.cubit.domain.energy.EnergyReading;
import io.github.zenndg.cubit.domain.energy.PowerState;
import io.github.zenndg.cubit.domain.tier.RobotTier;
import io.github.zenndg.cubit.domain.xp.XpTank;
import io.github.zenndg.cubit.entity.RobotEntity;
import io.github.zenndg.cubit.entity.RobotReadout;
import io.github.zenndg.cubit.item.XpTooltip;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Tier, battery, XP and state of a Cubit in Jade (answers 660 and 1837). The battery and the XP are
 * only on the server, so Jade asks for them while someone looks at the robot (answer 661). Bad data
 * shows nothing.
 */
enum RobotJadeProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
  INSTANCE;

  private static final String CHARGE = "Charge";
  private static final String CAPACITY = "Capacity";
  private static final String TIER = "Tier";
  private static final String STATE = "State";
  private static final String XP = "Xp";
  private static final String XP_CAPACITY = "XpCapacity";

  @Override
  public void appendServerData(CompoundTag data, EntityAccessor accessor) {
    if (accessor.getEntity() instanceof RobotEntity robot) {
      EnergyReading battery = RobotReadout.battery(robot);
      data.putLong(CHARGE, battery.charge().amount());
      data.putLong(CAPACITY, battery.capacity().amount());
      data.putString(TIER, robot.state().tier().name());
      data.putString(STATE, robot.powerState().name());
      XpTank xp = robot.pickup().xp().tank();
      data.putInt(XP, xp.fluid());
      data.putInt(XP_CAPACITY, xp.capacity());
    }
  }

  @Override
  public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
    CompoundTag data = accessor.getServerData();
    enumOf(RobotTier.class, data.getString(TIER))
        .ifPresent(tier -> tooltip.add(JadeTierLine.of(tier)));
    if (data.contains(CHARGE) && data.contains(CAPACITY)) {
      tooltip.add(
          JadeEnergyText.battery(
              EnergyReading.capped(data.getLong(CHARGE), data.getLong(CAPACITY))));
    }
    int xp = data.getInt(XP);
    int xpCapacity = data.getInt(XP_CAPACITY);
    if (xp >= 0 && xpCapacity > 0) {
      tooltip.add(XpTooltip.line(new XpTank(xp, xpCapacity)));
    }
    enumOf(PowerState.class, data.getString(STATE))
        .ifPresent(state -> tooltip.add(Component.translatable(JadeKeys.state(state))));
  }

  @Override
  public ResourceLocation getUid() {
    return JadeKeys.ROBOT_UID;
  }

  private static <E extends Enum<E>> Optional<E> enumOf(Class<E> type, String name) {
    return Arrays.stream(type.getEnumConstants())
        .filter(constant -> constant.name().equals(name))
        .findFirst();
  }
}
