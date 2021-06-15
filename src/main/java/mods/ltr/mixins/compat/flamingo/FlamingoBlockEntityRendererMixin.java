package mods.ltr.mixins.compat.flamingo;

import com.reddit.user.koppeh.flamingo.FlamingoBlockEntity;
import com.reddit.user.koppeh.flamingo.client.FlamingoBlockEntityRenderer;
import mods.ltr.compat.flamingo.FlamingoAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(FlamingoBlockEntityRenderer.class)
public abstract class FlamingoBlockEntityRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"))
    public void ltr_render(FlamingoBlockEntity flamingo, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcon, int i, int j, CallbackInfo ctx) {
        ItemStack taterStack = ((FlamingoAccessor) flamingo).ltr_getTater();
        matrices.scale(1f, -1f, -1f);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        matrices.translate(0, -0.625, 0.15);
        MinecraftClient.getInstance().getItemRenderer().renderItem(taterStack, ModelTransformation.Mode.FIXED, i, j, matrices, vcon, 42);
    }
}
