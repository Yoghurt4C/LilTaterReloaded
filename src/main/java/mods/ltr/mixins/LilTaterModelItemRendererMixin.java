package mods.ltr.mixins;

import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.client.config.LilTaterReloadedConfig;
import mods.ltr.entities.LilTaterBlockEntity;
import mods.ltr.util.LRUCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static mods.ltr.util.RenderStateSetup.getRenderState;

@Environment(EnvType.CLIENT)
@Mixin(BuiltinModelItemRenderer.class)
public abstract class LilTaterModelItemRendererMixin {
    @Unique
    private LilTaterBlockEntity ltr_DUMMYTATER;

    @Unique
    public LRUCache<CompoundTag, LilTaterBlockEntity> ltr_taterItemRendererCache = new LRUCache<>(LilTaterReloadedConfig.getTaterItemRendererCacheSize());

    @Inject(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/BlockItem;getBlock()Lnet/minecraft/block/Block;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void renderLilTaterBlockItem(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay, CallbackInfo ctx, Item item, Block block) {
        if (block instanceof LilTaterBlock) {
            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (ltr_taterItemRendererCache.get(tag) != null) {
                    LilTaterBlockEntity taterToRender = ltr_taterItemRendererCache.get(tag);
                    BlockEntityRenderDispatcher.INSTANCE.renderEntity(taterToRender, matrices, vcon, light, overlay);
                    ctx.cancel();
                } else {
                    LilTaterBlockEntity taterToRender = new LilTaterBlockEntity();
                    taterToRender.readFrom(stack);
                    taterToRender.isItem = true;
                    if (taterToRender.name != null) {
                        String fullName = taterToRender.name.getString().toLowerCase().trim().replace(" ", "_");
                        if (taterToRender.renderState == null || !taterToRender.renderState.fullName.equals(fullName)) {
                            taterToRender.renderState = getRenderState(fullName);
                        }
                    }
                    ltr_taterItemRendererCache.put(tag, taterToRender);
                }
            } else {
                if (ltr_DUMMYTATER!=null)
                BlockEntityRenderDispatcher.INSTANCE.renderEntity(ltr_DUMMYTATER, matrices, vcon, light, overlay);
                else ltr_DUMMYTATER = new LilTaterBlockEntity();
            }
        }
    }
}