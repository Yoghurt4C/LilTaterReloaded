package mods.ltr.meditation;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import static mods.ltr.registry.LilTaterMeditation.S2C_MEDITATION_PROGRESS;

public class MeditationSyncS2CPacket {

    public static void sendMeditationState(PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(((LilTaterMeditationAbility) player.getAbilities()).ltr_hasMeditated());
        ServerPlayNetworking.send((ServerPlayerEntity) player, S2C_MEDITATION_PROGRESS, buf);
    }
}
