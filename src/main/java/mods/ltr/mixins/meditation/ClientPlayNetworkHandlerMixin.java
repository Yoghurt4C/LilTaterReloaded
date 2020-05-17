package mods.ltr.mixins.meditation;

import mods.ltr.compat.LilTaterMeditationAbility;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerAbilities",at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void ltr_appendMeditation(PlayerAbilitiesS2CPacket packet, CallbackInfo ctx, PlayerEntity player){
        ((LilTaterMeditationAbility)player.abilities).ltr_setMeditationState(((LilTaterMeditationAbility)packet).ltr_hasMeditated());
    }
}
