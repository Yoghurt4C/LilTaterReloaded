package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlock;
import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlamingoBlock.class)
public abstract class FlamingoBlockMixin extends Block {

    public FlamingoBlockMixin(Settings settings) { super(settings); }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (this.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FlamingoBlockEntity) {
                ItemStack taterStack = ((FlamingoAccessor) be).ltr_getTater();
                if (!taterStack.isEmpty()) {
                    ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, taterStack));
                }
            }
        }
    }
}
