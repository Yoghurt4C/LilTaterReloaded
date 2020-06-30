package mods.ltr.mixins.meditation;

import mods.ltr.client.LilTaterReloadedClient;
import mods.ltr.registry.LilTaterMeditation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LilTaterReloadedClient.class)
public class LilTaterMeditationPacketRegistrar {
    @Inject(method = "onInitializeClient", at = @At("TAIL"), remap = false)
    public void appendMeditation(CallbackInfo ctx) {
        LilTaterMeditation.init();
    }
}
