package mods.ltr.mixins.meditation;

import mods.ltr.compat.LilTaterMeditationAbility;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


//todo unfuck this. this doesn't need to exist
@Mixin(PlayerAbilitiesS2CPacket.class)
public abstract class PlayerAbilitiesS2CPacketMixin implements LilTaterMeditationAbility {
    @Unique
    private boolean LTR_HAS_MEDITATED;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerAbilities;)V", at = @At("RETURN"))
    public void ltr_constructor(PlayerAbilities abilities, CallbackInfo ctx){
        this.ltr_setMeditationState(((LilTaterMeditationAbility)abilities).ltr_hasMeditated());
    }

    @Inject(method = "read", at = @At("RETURN"))
    public void ltr_read(PacketByteBuf buf, CallbackInfo ctx){
        this.ltr_setMeditationState(buf.readBoolean());
    }

    @Inject(method = "write", at = @At("RETURN"))
    public void ltr_write(PacketByteBuf buf, CallbackInfo ctx){
        buf.writeBoolean(ltr_hasMeditated());
    }

    public boolean ltr_hasMeditated() { return this.LTR_HAS_MEDITATED; }

    public void ltr_setMeditationState(boolean bool) { this.LTR_HAS_MEDITATED = bool;}
}
