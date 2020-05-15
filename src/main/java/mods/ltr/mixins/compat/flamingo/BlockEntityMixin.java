package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "fromTag", at=@At(value = "TAIL"))
    public void ltr_fromTag(CompoundTag tag, CallbackInfo ctx){
        if ((BlockEntity)(Object)this instanceof FlamingoBlockEntity){
            ((FlamingoAccessor)this).ltr_setTater(ItemStack.fromTag(tag.getCompound("ltr_tater")));
        }
    }

    @Inject(method = "toTag", at = @At(value = "RETURN"))
    public void ltr_toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ctx){
        if ((BlockEntity)(Object)this instanceof FlamingoBlockEntity){
            CompoundTag syncTag = new CompoundTag();
            ((FlamingoAccessor)this).ltr_getTater().toTag(syncTag);
            tag.put("ltr_tater",syncTag);
        }
    }
}
