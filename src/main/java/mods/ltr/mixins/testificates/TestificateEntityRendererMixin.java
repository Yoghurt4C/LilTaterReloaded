package mods.ltr.mixins.testificates;

import mods.ltr.testificates.TestificateTaterFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntityRenderer.class)
public abstract class TestificateEntityRendererMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    public TestificateEntityRendererMixin(EntityRendererFactory.Context ctx, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(ctx, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void ltr_appendModel(EntityRendererFactory.Context context, CallbackInfo ctx) {
        this.addFeature(new TestificateTaterFeatureRenderer<>(this));
    }
}
