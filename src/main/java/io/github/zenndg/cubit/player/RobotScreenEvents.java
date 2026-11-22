package io.github.zenndg.cubit.player;

import io.github.zenndg.cubit.CubitMod;
import io.github.zenndg.cubit.menu.RobotMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

/**
 * Opening and closing the screen of a Cubit, on its own or by the Tablet, on the server: a card put
 * in it says its phrase when the screen closes (answers 1583 and 1589).
 */
@EventBusSubscriber(modid = CubitMod.MOD_ID)
public final class RobotScreenEvents {
  private RobotScreenEvents() {}

  @SubscribeEvent
  public static void onOpen(PlayerContainerEvent.Open event) {
    if (event.getContainer() instanceof RobotMenu menu
        && !event.getEntity().level().isClientSide()) {
      menu.work().screenOpened();
    }
  }

  @SubscribeEvent
  public static void onClose(PlayerContainerEvent.Close event) {
    if (event.getContainer() instanceof RobotMenu menu
        && !event.getEntity().level().isClientSide()) {
      menu.work().screenClosed(event.getEntity());
    }
  }
}
