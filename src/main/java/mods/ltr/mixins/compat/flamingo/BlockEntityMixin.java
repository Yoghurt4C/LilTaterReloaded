package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "readNbt", at=@At(value = "TAIL"))
    public void ltr_fromTag(NbtCompound tag, CallbackInfo ctx){
        if ((BlockEntity)(Object)this instanceof FlamingoBlockEntity){
            ((FlamingoAccessor)this).ltr_setTater(ItemStack.fromNbt(tag.getCompound("ltr_tater")));
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "RETURN"))
    public void ltr_toTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> ctx){
        if ((BlockEntity)(Object)this instanceof FlamingoBlockEntity){
            NbtCompound syncTag = new NbtCompound();
            ((FlamingoAccessor)this).ltr_getTater().writeNbt(syncTag);
            tag.put("ltr_tater",syncTag);
        }
    }
}
