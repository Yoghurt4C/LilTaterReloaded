package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FlamingoBlockEntity.class)
public abstract class FlamingoBlockEntityMixin extends BlockEntity implements FlamingoAccessor {
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.ltr_taterStack = ItemStack.fromNbt(nbt.getCompound("ltr_tater"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtCompound syncTag = new NbtCompound();
        ltr_taterStack.writeNbt(syncTag);
        nbt.put("ltr_tater", syncTag);
    }

    @Override
    public void markDirty() {
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        super.markDirty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
