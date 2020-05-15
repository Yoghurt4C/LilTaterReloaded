package mods.ltr.mixins.meditation;

import mods.ltr.compat.LilTaterMeditationCounter;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method = "respawnPlayer",at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void respawnPlayerWithMeditationTicks(ServerPlayerEntity player, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> ctx, BlockPos pos, boolean bl, ServerPlayerInteractionManager manager, ServerPlayerEntity serverPlayerEntity){
        int meditationTicks = ((LilTaterMeditationCounter)player).ltr_getMeditationTicks();
        ((LilTaterMeditationCounter)serverPlayerEntity).ltr_setMeditationTicks(meditationTicks);
    }
}
