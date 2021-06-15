package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FlamingoBlockEntity.class)
public abstract class FlamingoBlockEntityMixin extends BlockEntity implements FlamingoAccessor, BlockEntityClientSerializable {
    @Unique
    public ItemStack ltr_taterStack = ItemStack.EMPTY;

    public FlamingoBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStack ltr_getTater() {
        return ltr_taterStack;
    }

    public void ltr_setTater(ItemStack taterStack) {
        this.ltr_taterStack = taterStack;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.ltr_taterStack = ItemStack.fromNbt(tag.getCompound("ltr_tater"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        NbtCompound syncTag = new NbtCompound();
        ltr_taterStack.writeNbt(syncTag);
        tag.put("ltr_tater", syncTag);
        return tag;
    }
}
