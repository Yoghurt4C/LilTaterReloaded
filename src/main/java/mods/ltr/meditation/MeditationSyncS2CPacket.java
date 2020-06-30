package mods.ltr.meditation;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import static mods.ltr.registry.LilTaterMeditation.S2C_MEDITATION_PROGRESS;

public class MeditationSyncS2CPacket {

    public static void sendMeditationState(PlayerEntity player){
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(((LilTaterMeditationAbility)player.abilities).ltr_hasMeditated());
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_MEDITATION_PROGRESS, buf);
    }
}
