package mods.ltr.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LilTaterBlockItem extends BlockItem {
    public LilTaterBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static boolean writeTagToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        if (world.getServer() == null) {
            return false;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && stack.hasTag()) {
            if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                return false;
            }

            CompoundTag beWriteTag = blockEntity.toTag(new CompoundTag());
            CompoundTag copyTag = beWriteTag.copy();
            CompoundTag stackTag = stack.getSubTag("BlockEntityTag");
            if (stackTag != null) {
                beWriteTag.copyFrom(stackTag);
            }
            beWriteTag.putInt("x", pos.getX());
            beWriteTag.putInt("y", pos.getY());
            beWriteTag.putInt("z", pos.getZ());
            beWriteTag.put("display", stack.getSubTag("display"));
            if (!beWriteTag.equals(copyTag)) {
                blockEntity.fromTag(Block.getBlockFromItem(stack.getItem()).getDefaultState(), beWriteTag);
                blockEntity.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState placementState) {
        return writeTagToBlockEntity(world, player, pos, stack);
    }
}
