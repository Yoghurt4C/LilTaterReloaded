package mods.ltr.mixins.meditation;

import mods.ltr.compat.LilTaterMeditationAbility;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerAbilities.class)
public abstract class LilTaterMeditationAbilityMixin implements LilTaterMeditationAbility {
    @Unique
    private boolean LTR_HAS_MEDITATED = false;

    @Inject(method = "serialize",at=@At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void ltr_serialize(CompoundTag tag, CallbackInfo ctx, CompoundTag tag2){
        if (this.ltr_hasMeditated()) {
            tag2.putBoolean("ltr_hasMeditated", this.ltr_hasMeditated());
        }
    }

    @Inject(method = "deserialize",at=@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void ltr_deserialize(CompoundTag tag, CallbackInfo ctx, CompoundTag tag2){
        if (tag2.contains("ltr_hasMeditated")) {
            this.ltr_setMeditationState(tag2.getBoolean("ltr_hasMeditated"));
        }
    }

    public boolean ltr_hasMeditated() { return this.LTR_HAS_MEDITATED; }

    public void ltr_setMeditationState(boolean bool) { this.LTR_HAS_MEDITATED = bool;}
}
