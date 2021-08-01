package mods.ltr.client.models;

import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.entities.LilTaterBlockEntity;
import mods.ltr.registry.LilTaterBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import static mods.ltr.client.LilTaterReloadedClient.DUMMYTATER;
import static mods.ltr.client.LilTaterReloadedClient.taterItemRendererCache;
import static mods.ltr.util.RenderStateSetup.getRenderState;

public interface TaterModel {

    static TexturedModelData getModel() {
        ModelData data = new ModelData();
        data.getRoot().addChild("tater", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(0, 0, 0, 4, 7, 4, Dilation.NONE, 0.5F, 0.5F),
                ModelTransform.pivot(-2, 18, -2));
        return TexturedModelData.of(data, 32, 32);
    }

    static void renderItem(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay) {
        Item item = stack.getItem();
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof LilTaterBlock) {
            final MinecraftClient mc = MinecraftClient.getInstance();
            if (stack.hasNbt()) {
                NbtCompound tag = stack.getNbt();
                if (taterItemRendererCache.get(tag) != null) {
                    LilTaterBlockEntity taterToRender = taterItemRendererCache.get(tag);
                    mc.getBlockEntityRenderDispatcher().renderEntity(taterToRender, matrices, vcon, light, overlay);

                } else {
                    LilTaterBlockEntity taterToRender = new LilTaterBlockEntity(BlockPos.ORIGIN, LilTaterBlocks.LIL_TATER.getDefaultState());
                    taterToRender.readFrom(stack);
                    taterToRender.isItem = true;
                    if (taterToRender.name != null) {
                        String fullName = taterToRender.name.getString().toLowerCase().trim().replace(" ", "_");
                        if (taterToRender.renderState == null || !taterToRender.renderState.fullName.equals(fullName)) {
                            taterToRender.renderState = getRenderState(fullName);
                        }
                    }
                    taterItemRendererCache.put(tag.copy(), taterToRender);
                }
            } else {
                if (DUMMYTATER != null)
                    mc.getBlockEntityRenderDispatcher().renderEntity(DUMMYTATER, matrices, vcon, light, overlay);
                else
                    DUMMYTATER = new LilTaterBlockEntity(BlockPos.ORIGIN, LilTaterBlocks.LIL_TATER.getDefaultState());
            }
        }
    }
}
