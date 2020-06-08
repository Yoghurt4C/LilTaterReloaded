package mods.ltr.mixins.trading;

import mods.ltr.LilTaterReloaded;
import mods.ltr.registry.LilTaterTradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilTaterReloaded.class)
public class TradeOfferRegistrar {

    @Inject(method = "onInitialize()V", at = @At("TAIL"), remap = false)
    public void registerTradeOffers(CallbackInfo ctx) {
        LilTaterTradeOffers.init();
    }
}
