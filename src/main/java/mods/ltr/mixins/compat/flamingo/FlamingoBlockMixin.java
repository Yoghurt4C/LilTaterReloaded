package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlock;
import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import mods.ltr.items.LilTaterBlockItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlamingoBlock.class)
public abstract class FlamingoBlockMixin extends Block {

    public FlamingoBlockMixin(Settings settings) { super(settings); }

    @Inject(method = "onUse", at=@At(value = "INVOKE", target = "Lcom/reddit/user/koppeh/flamingo/FlamingoBlockEntity;wiggle()V"),remap = false)
    public void ltr_onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ctx){
        FlamingoBlockEntity flamingo = (FlamingoBlockEntity)world.getBlockEntity(pos);
        FlamingoAccessor flamingoAccessor = (FlamingoAccessor)flamingo;
        ItemStack stack = player.getStackInHand(hand);
        ItemStack taterStack = flamingoAccessor.ltr_getTater();
        if (!taterStack.isEmpty() && player.isSneaking()){
            ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, taterStack));
            flamingoAccessor.ltr_setTater(ItemStack.EMPTY);
        } else if (!stack.isEmpty() && stack.getItem() instanceof LilTaterBlockItem){
            flamingoAccessor.ltr_setTater(stack.split(1));
        }
        if (!world.isClient()) {
            ((BlockEntityClientSerializable) flamingo).sync();
        }
    }

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
