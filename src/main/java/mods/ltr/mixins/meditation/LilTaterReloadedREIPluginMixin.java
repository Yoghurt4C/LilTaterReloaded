package mods.ltr.mixins.meditation;

import dev.architectury.event.EventResult;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import mods.ltr.compat.rei.LilTaterReloadedREIPlugin;
import mods.ltr.meditation.LilTaterMeditationAbility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mods.ltr.compat.rei.LilTaterReloadedREIPlugin.LTR;
import static mods.ltr.compat.rei.LilTaterReloadedREIPlugin.SHOW_TATERS;

@Environment(EnvType.CLIENT)
@Mixin(LilTaterReloadedREIPlugin.class)
public abstract class LilTaterReloadedREIPluginMixin {
    @Inject(method = "registerDisplays", at = @At("RETURN"), remap = false)
    public void ltr_registerRecipeVisibilityHandler(DisplayRegistry registry, CallbackInfo ci) {
        registry.registerVisibilityPredicate(((category, display) -> {
            if (!SHOW_TATERS) {
                if (category.getCategoryIdentifier().equals(LTR)) {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        PlayerEntity playerEntity = player.world.getPlayerByUuid(player.getUuid());
                        if (((LilTaterMeditationAbility) playerEntity.getAbilities()).ltr_hasMeditated()) {
                            SHOW_TATERS = true;
                            return EventResult.interruptTrue();
                        } else return EventResult.interruptFalse();
                    }
                }
            }
            return EventResult.pass();
        }));
    }
}
