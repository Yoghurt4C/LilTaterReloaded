package mods.ltr.mixins.meditation;

import me.shedaniel.rei.api.RecipeHelper;
import mods.ltr.compat.LilTaterMeditationAbility;
import mods.ltr.compat.rei.LilTaterReloadedREIPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mods.ltr.compat.rei.LilTaterReloadedREIPlugin.LTR;
import static mods.ltr.compat.rei.LilTaterReloadedREIPlugin.SHOW_TATERS;

@Environment(EnvType.CLIENT)
@Mixin(LilTaterReloadedREIPlugin.class)
public abstract class LilTaterReloadedREIPluginMixin {

    @Inject(method = "registerOthers", at = @At("HEAD"), remap = false)
    public void ltr_registerRecipeVisibilityHandler(RecipeHelper helper, CallbackInfo ctx) {
        helper.registerRecipeVisibilityHandler(((category, display) -> {
            if (!SHOW_TATERS) {
                if (display.getRecipeCategory().equals(LTR)) {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        PlayerEntity playerEntity = player.world.getPlayerByUuid(player.getUuid());
                        if (((LilTaterMeditationAbility) playerEntity.abilities).ltr_hasMeditated()) {
                            SHOW_TATERS=true;
                            return ActionResult.SUCCESS;
                        } else return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.PASS;
        }));
    }
}
