package mods.ltr.barter;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;

import java.util.Set;

import static mods.ltr.registry.LilTaterAtlas.taterAtlas;
import static mods.ltr.registry.LilTaterBarterOffers.SEND_BARTER_NAME_POOLS;
import static mods.ltr.registry.LilTaterBarterOffers.SEND_BARTER_PREFIX_POOLS;
import static mods.ltr.util.RenderStateSetup.validPrefixes;

@Environment(EnvType.CLIENT)
public class BarterOffersC2SPackets {

    public static void sendBarterNamePools() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Set<String> keySet = taterAtlas.keySet();
        buf.writeInt(keySet.size());
        keySet.forEach(buf::writeString);
        ClientPlayNetworking.send(SEND_BARTER_NAME_POOLS, buf);
    }

    public static void sendBarterPrefixPools() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(validPrefixes.size());
        validPrefixes.forEach(buf::writeString);
        ClientPlayNetworking.send(SEND_BARTER_PREFIX_POOLS, buf);
    }
}
