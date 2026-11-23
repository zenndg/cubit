package io.github.zenndg.cubit.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/** The item of the Induction Dock, drawn by the same GeckoLib model as the block (answer 1860). */
public final class InductionDockItem extends BlockItem implements GeoItem {
  private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

  public InductionDockItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    // The item stays still, with the panel in its cabinet: the motion belongs to the placed dock.
  }

  @Override
  public AnimatableInstanceCache getAnimatableInstanceCache() {
    return animationCache;
  }
}
