package mods.ltr.mixins.testificates;

import mods.ltr.testificates.TestificateTaterFeatureRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.resource.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntityRenderer.class)
public abstract class TestificateEntityRendererMixin extends MobEntityRenderer<VillagerEntity, VillagerResemblingModel<VillagerEntity>> {

    public TestificateEntityRendererMixin(EntityRenderDispatcher entityRenderDispatcher, VillagerResemblingModel<VillagerEntity> entityModel, float f) {
        super(entityRenderDispatcher, entityModel, f);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Lnet/minecraft/resource/ReloadableResourceManager;)V", at=@At("TAIL"))
    public void ltr_appendModel(EntityRenderDispatcher dispatcher, ReloadableResourceManager reloadableResourceManager, CallbackInfo ctx) {
        this.addFeature(new TestificateTaterFeatureRenderer<>(this));
    }
}
