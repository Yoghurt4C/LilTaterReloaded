package mods.ltr.testificates;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class TestificateTaterFeatureRenderer<T extends VillagerEntity, M extends VillagerResemblingModel<T>> extends FeatureRenderer<T, M> {
    public TestificateTaterFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack stack = ((TestificateTaterAccessor) entity).ltr_getTaterStack();
        if (!stack.isEmpty()) {
            matrices.push();

            ((ModelWithHead)this.getContextModel()).getHead().rotate(matrices);
            matrices.scale(0.501f,-0.572f,-0.501f);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(0,0.3906f,-0.624f);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(entity, stack, ModelTransformation.Mode.HEAD, false, matrices, vertexConsumers, light);
            matrices.pop();
        }
    }
}
