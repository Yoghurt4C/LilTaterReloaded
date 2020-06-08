package mods.ltr.mixins.barter;

import mods.ltr.barter.StopPiggerGrayonInterface;
import mods.ltr.items.LilTaterBlockItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.brain.task.AdmireItemTask;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AdmireItemTask.class)
public abstract class AdmireItemTaskMixin {

    @Inject(method = "shouldRun", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void ltr_shouldRun(ServerWorld world, PiglinEntity piglin, CallbackInfoReturnable<Boolean> ctx, ItemEntity ientity){
        if (((StopPiggerGrayonInterface)ientity).ltr_canBePickedUpByPiggers() && ientity.getStack().getItem() instanceof LilTaterBlockItem){
            ctx.setReturnValue(true);
        }
    }
}
