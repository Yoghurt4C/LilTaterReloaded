package mods.ltr.mixins.trading;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mods.ltr.registry.LilTaterTradeOffers;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

import static mods.ltr.registry.LilTaterTradeOffers.tradeOffers;

@Mixin(TradeOffers.class)
public abstract class TradeOffersMixin {

    @Inject(method = "method_16929", at = @At("TAIL"))
    private static void ltr_addTradeOffers(HashMap<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map, CallbackInfo info) {
        for (LilTaterTradeOffers.LTRTradeOffer tradeOffer : tradeOffers.values()) {
            map.get(tradeOffer.getProfession()).compute(tradeOffer.getProfessionLevel(), (integer, factories) ->
                    ArrayUtils.add(factories, tradeOffer.getOffer()));
        }
    }
}
