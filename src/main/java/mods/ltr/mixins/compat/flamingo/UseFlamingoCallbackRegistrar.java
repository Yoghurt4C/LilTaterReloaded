package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.LilTaterReloaded;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import mods.ltr.items.LilTaterBlockItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilTaterReloaded.class)
public class UseFlamingoCallbackRegistrar {

    @Inject(method = "onInitialize()V", at = @At("TAIL"), remap = false)
    public void registerUseFlamingoCallback(CallbackInfo ctx){
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FlamingoBlockEntity) {
                FlamingoAccessor flamingoAccessor = (FlamingoAccessor) be;
                ItemStack stack = player.getStackInHand(hand);
                ItemStack taterStack = flamingoAccessor.ltr_getTater();
                if (!taterStack.isEmpty() && player.isSneaking()) {
                    ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, taterStack));
                    flamingoAccessor.ltr_setTater(ItemStack.EMPTY);
                } else if (!stack.isEmpty() && stack.getItem() instanceof LilTaterBlockItem) {
                    flamingoAccessor.ltr_setTater(stack.split(1));
                } else world.addBlockAction(pos, world.getBlockState(pos).getBlock(), 0, 0);
                if (!world.isClient()) {
                    ((BlockEntityClientSerializable) be).sync();
                }
                return ActionResult.SUCCESS;
            } else return ActionResult.PASS;
        });
    }
}
