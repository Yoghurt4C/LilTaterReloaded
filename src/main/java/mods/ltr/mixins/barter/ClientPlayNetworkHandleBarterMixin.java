package mods.ltr.mixins.barter;

import mods.ltr.barter.BarterOffersC2SPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandleBarterMixin {

    @Inject(method = "onGameJoin",at = @At("TAIL"))
    public void ltr_sendBarterOffers(GameJoinS2CPacket packet, CallbackInfo ctx){
        BarterOffersC2SPackets.sendBarterNamePools();
        BarterOffersC2SPackets.sendBarterPrefixPools();
    }
}
