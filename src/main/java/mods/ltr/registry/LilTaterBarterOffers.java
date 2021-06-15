package mods.ltr.registry;

import com.google.common.collect.ImmutableList;
import mods.ltr.LilTaterReloaded;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LilTaterBarterOffers {
    public static List<String> validBarterNames = new ArrayList<>();
    public static List<String> validBarterPrefixes = new ArrayList<>();

    public static final Identifier SEND_BARTER_NAME_POOLS = LilTaterReloaded.getId("send_barter_name_pools");
    public static final Identifier SEND_BARTER_PREFIX_POOLS = LilTaterReloaded.getId("send_barter_prefix_pools");

    public static void init() {
        register(SEND_BARTER_NAME_POOLS, validBarterNames);
        register(SEND_BARTER_PREFIX_POOLS, validBarterPrefixes);
    }

    private static void register(Identifier id, List<String> list) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, sender) -> {
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                String s = buf.readString(32767);
                if (!list.contains(s)) {
                    list.add(s);
                }
            }
        });
    }

    public static List<ItemStack> getBarterTater(ItemStack stack) {
        MutableText text = new LiteralText("");
        Random random = new Random();
        if (!validBarterPrefixes.isEmpty()) {
            if (random.nextInt(4) % 4 == 0)
                text.append(validBarterPrefixes.get(random.nextInt(validBarterPrefixes.size()))).append("_");
        }
        if (!validBarterNames.isEmpty()) {
            text.append(validBarterNames.get(random.nextInt(validBarterNames.size())));
        }
        if (!text.getString().isEmpty()) {
            stack.setCustomName(text);
        }
        return ImmutableList.of(stack);
    }
}
