package io.github.zenndg.cubit.item;

import io.github.zenndg.cubit.block.NetworkPlacement;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** The item of a block of the Network, which tells why it cannot be placed (answer 2271). */
public class NetworkItem extends BlockItem implements GeoItem {
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

  public NetworkItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    Optional<Component> refusal =
        context.canPlace() ? NetworkPlacement.refusal(getBlock(), context) : Optional.empty();
    if (refusal.isEmpty()) {
      return super.place(context);
    }
    Player player = context.getPlayer();
    if (player != null && !context.getLevel().isClientSide()) {
      player.displayClientMessage(refusal.get(), true);
    }
    return InteractionResult.FAIL;
  }

  /** The server replaces it with its own lines. */
  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
    NetworkTooltip.appendRelay(stack, lines);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    // The item stays still: the motion belongs to the placed block.
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return animationCache;
  }
}
