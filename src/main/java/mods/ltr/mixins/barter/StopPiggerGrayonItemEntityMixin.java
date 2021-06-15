package mods.ltr.mixins.barter;

import mods.ltr.barter.StopPiggerGrayonInterface;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public abstract class StopPiggerGrayonItemEntityMixin implements StopPiggerGrayonInterface {
    @Unique
    private boolean ltr_PIGGER_PICKUP_ELIGIBILITY = true;

    public boolean ltr_canBePickedUpByPiggers() {
        return ltr_PIGGER_PICKUP_ELIGIBILITY;
    }

    public void ltr_setPiggerPickupEligibility(boolean bool) {
        this.ltr_PIGGER_PICKUP_ELIGIBILITY = bool;
    }
}
