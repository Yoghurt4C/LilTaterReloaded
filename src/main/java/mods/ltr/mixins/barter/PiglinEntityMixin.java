package mods.ltr.mixins.barter;

import mods.ltr.items.LilTaterBlockItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinEntity.class)
public abstract class PiglinEntityMixin extends HostileEntity {

    protected PiglinEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getActivity",at = @At("HEAD"), cancellable = true)
    public void ltr_getTaterAdmirationActivity(CallbackInfoReturnable<PiglinActivity> ctx){
        if (this.getOffHandStack().getItem() instanceof LilTaterBlockItem) {
            ctx.setReturnValue(PiglinActivity.ADMIRING_ITEM);
        }
    }
}
