package mods.ltr.mixins.meditation;

import mods.ltr.meditation.LilTaterMeditationCounter;
import mods.ltr.meditation.MeditationSyncS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method = "respawnPlayer",at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void respawnPlayerWithMeditationTicks(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> ctx, BlockPos pos, float angle, boolean bl, ServerWorld world, Optional optional, ServerPlayerInteractionManager manager, ServerWorld sworld, ServerPlayerEntity serverPlayerEntity){
        int meditationTicks = ((LilTaterMeditationCounter)player).ltr_getMeditationTicks();
        ((LilTaterMeditationCounter)serverPlayerEntity).ltr_setMeditationTicks(meditationTicks);
    }

    @Inject(method = "onPlayerConnect",at=@At(value = "TAIL"))
    public void appendMeditationState(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ctx){
        MeditationSyncS2CPacket.sendMeditationState(player);
    }
}
