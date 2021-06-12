package mods.ltr.client.models;

import mods.ltr.LilTaterReloaded;
import mods.ltr.entities.LilTaterBlockEntity;
import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.util.ColorSniffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
//not actually a "model", but don't tell anyone
public class ImitaterModel {
    private static MinecraftClient client = MinecraftClient.getInstance();
    private static final BlockState defaultState = LilTaterBlocks.LIL_TATER.getDefaultState();
    private final static Sprite MISSINGNO = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId()).getSprite();
    private final static Sprite SMILE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, LilTaterReloaded.getId("block/imitater_smile")).getSprite();

    public static void draw(LilTaterBlockEntity tater, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay, float r, float g, float b, float a) {
        matrices.push();
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180));
        ItemStack stack = tater.getStack(4);
        Item item = stack.getItem();
        boolean isBlock = !stack.isEmpty() && !(item instanceof LilTaterBlockItem) && item instanceof BlockItem && client.getItemRenderer().getModels().getModel(stack).isSideLit();
        Sprite sprite;
        if (isBlock) {
            matrices.scale(0.5f, -0.875f, -0.5f);
            matrices.translate(0f, -1.5351f, 0f);
            sprite = client.getItemRenderer().getModels().getModel(stack).getSprite();
            matrices.push();
            if (item instanceof WallStandingBlockItem) {
                matrices.scale(0.98f, 0.98f, 0.98f);
            }
            client.getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED,light,overlay,matrices,vcon,42);
            matrices.pop();
        } else {
            matrices.scale(0.25f, -0.4375f, -0.25f);
            matrices.translate(-0.5f, -3.573f, -0.5f);
            sprite = MISSINGNO;
            BakedModel missingno = client.getBakedModelManager().getMissingModel();
            client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vcon.getBuffer(RenderLayers.getEntityBlockLayer(defaultState, true)), null, missingno, r, g, b, light , overlay);
        }

        int colorOffset = ColorSniffer.getAverageColor(sprite);
        int smileR = (colorOffset >> 16) & 0xFF;
        int smileG = (colorOffset >> 8) & 0xFF;
        int smileB = colorOffset & 0xFF;

        float o = 0.4f;
        int red = (int) ((r-o)*255), green = (int) ((g-o)*255), blue = (int) ((b-o)*255), alpha = (int) (a*255);

        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90));
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
        if (isBlock){
            matrices.scale(0.5f, -0.5f, -0.5f);
            matrices.translate(-0.505f, -0.5f, -0.5f);
        } else {
            matrices.scale(1f, -1f, -1f);
            matrices.translate(-0.001f, -1f, -1f);
        }
        Matrix4f model = matrices.peek().getModel();
        Matrix3f normal = matrices.peek().getNormal();
        VertexConsumer v = vcon.getBuffer(RenderLayer.getCutout());
        ImitaterModel.drawHorizontalFace(v, model, normal, Math.max(0, smileR - red), Math.max(0, smileG - green), Math.max(0, smileB - blue), alpha, light, overlay);
        matrices.pop();
    }

    private static void drawHorizontalFace(VertexConsumer v, Matrix4f model, Matrix3f normal, int red, int green, int blue, int alpha, int light, int overlay){
        v.vertex(model, 0, 0, 1.145f).color(red, green, blue, alpha).texture(SMILE.getMinU(), SMILE.getMaxV()).light(light).overlay(overlay).normal(normal,0,0,1).next();
        v.vertex(model, 0, 2, 1.145f).color(red, green, blue, alpha).texture(SMILE.getMaxU(), SMILE.getMaxV()).light(light).overlay(overlay).normal(normal,0,1,1).next();
        v.vertex(model, 0, 2, 0).color(red, green, blue, alpha).texture(SMILE.getMaxU(), SMILE.getMinV()).light(light).overlay(overlay).normal(normal,0,1,0).next();
        v.vertex(model, 0, 0, 0).color(red, green, blue, alpha).texture(SMILE.getMinU(), SMILE.getMinV()).light(light).overlay(overlay).normal(normal,0,0,0).next();
    }
}
