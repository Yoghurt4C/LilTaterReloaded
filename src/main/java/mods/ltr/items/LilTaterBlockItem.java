package mods.ltr.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LilTaterBlockItem extends BlockItem {
    public LilTaterBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static boolean writeNbtToBlockEntity(World world, @Nullable PlayerEntity player, BlockPos pos, ItemStack stack) {
        if (world.getServer() == null) {
            return false;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null && stack.hasNbt()) {
            if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                return false;
            }

            NbtCompound beWriteTag = blockEntity.createNbt();
            NbtCompound copyTag = beWriteTag.copy();
            NbtCompound stackTag = stack.getSubNbt("BlockEntityTag");
            if (stackTag != null) {
                beWriteTag.copyFrom(stackTag);
            }
            beWriteTag.putInt("x", pos.getX());
            beWriteTag.putInt("y", pos.getY());
            beWriteTag.putInt("z", pos.getZ());
            beWriteTag.put("display", stack.getSubNbt("display"));
            if (!beWriteTag.equals(copyTag)) {
                blockEntity.readNbt(beWriteTag);
                blockEntity.markDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState placementState) {
        return writeNbtToBlockEntity(world, player, pos, stack);
    }
}
