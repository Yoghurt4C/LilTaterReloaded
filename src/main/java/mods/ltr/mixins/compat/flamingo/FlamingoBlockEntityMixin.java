package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FlamingoBlockEntity.class)
public abstract class FlamingoBlockEntityMixin extends BlockEntity implements FlamingoAccessor, BlockEntityClientSerializable {
    @Unique
    public ItemStack ltr_taterStack = ItemStack.EMPTY;

    public FlamingoBlockEntityMixin(BlockEntityType<?> type) { super(type); }

    public ItemStack ltr_getTater() {
        return ltr_taterStack;
    }

    public void ltr_setTater(ItemStack taterStack){
        this.ltr_taterStack = taterStack;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.ltr_taterStack = ItemStack.fromTag(tag.getCompound("ltr_tater"));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        CompoundTag syncTag = new CompoundTag();
        ltr_taterStack.toTag(syncTag);
        tag.put("ltr_tater",syncTag);
        return tag;
    }
}
