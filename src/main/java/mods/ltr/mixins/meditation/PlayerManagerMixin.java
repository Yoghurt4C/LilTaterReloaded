package mods.ltr.mixins.meditation;

import mods.ltr.compat.LilTaterMeditationCounter;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method = "respawnPlayer",at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void respawnPlayerWithMeditationTicks(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> ctx, BlockPos pos, boolean bl, Optional optional, RegistryKey<World> registryKey, ServerPlayerInteractionManager manager, ServerWorld world, ServerPlayerEntity serverPlayerEntity){
        int meditationTicks = ((LilTaterMeditationCounter)player).ltr_getMeditationTicks();
        ((LilTaterMeditationCounter)serverPlayerEntity).ltr_setMeditationTicks(meditationTicks);
    }
}
