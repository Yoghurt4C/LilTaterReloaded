package mods.ltr.mixins.barter;

import mods.ltr.LilTaterReloaded;
import mods.ltr.registry.LilTaterBarterOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilTaterReloaded.class)
public abstract class BarterOfferPacketRegistrar {
    @Inject(method = "onInitialize()V", at = @At("TAIL"), remap = false)
    public void registerBarterOfferPackets(CallbackInfo ctx){
        LilTaterBarterOffers.init();
    }
}
