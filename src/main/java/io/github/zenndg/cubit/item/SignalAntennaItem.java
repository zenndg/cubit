package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.block.AntennaLinking;
import io.github.zenndg.cubit.block.NetworkServerBlockEntity;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

/**
 * The item of the Signal Antenna. A click on a Network Server links it, also with the Shift held or
 * on a Server of another player, who sees why nothing changed, and never places the antenna there
 * (Delivery 4, detail 13). Without a link it is not placed (answer 2546).
 */
public final class SignalAntennaItem extends NetworkItem {
  public SignalAntennaItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    NetworkServerBlockEntity server =
        AntennaLinking.serverAt(context.getLevel(), context.getClickedPos());
    if (server == null) {
      return super.useOn(context);
    }
    if (!context.getLevel().isClientSide() && context.getPlayer() != null) {
      AntennaLinking.link(server, context.getPlayer(), context.getItemInHand());
    }
    return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
    NetworkTooltip.appendAntenna(stack, lines);
  }
}
