package mods.ltr.mixins.meditation;

import mods.ltr.meditation.LilTaterMeditationAbility;
import mods.ltr.registry.LilTaterMeditation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onGameJoin",at = @At("TAIL"))
    public void ltr_setMeditationState(GameJoinS2CPacket packet, CallbackInfo ctx) {
        ((LilTaterMeditationAbility)this.client.player.getAbilities()).ltr_setMeditationState(LilTaterMeditation.ltr_hasMeditated());
    }
}
