package mods.ltr.mixins.barter;

import mods.ltr.barter.StopPiggerGrayonInterface;
import mods.ltr.items.LilTaterBlockItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LookTargetUtil.class)
public abstract class LookTargetUtilMixin {

    @Inject(method = "give",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void ltr_stopPiggerGrayons(LivingEntity entity, ItemStack stack, Vec3d vec3d, CallbackInfo ctx, double d, ItemEntity ientity){
        if (entity instanceof PiglinEntity && stack.getItem() instanceof LilTaterBlockItem) {
            ((StopPiggerGrayonInterface)ientity).ltr_setPiggerPickupEligibility(false);
        }
    }
}
