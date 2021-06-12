package mods.ltr.compat.libcd;
/*
import com.google.gson.JsonObject;
import io.github.cottonmc.libdp.api.driver.Driver;
import io.github.cottonmc.libdp.api.driver.recipe.RecipeDriver;
import io.github.cottonmc.libdp.api.driver.recipe.RecipeParser;
import mods.ltr.registry.LilTaterTradeOffers;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

import java.util.concurrent.Executor;

import static mods.ltr.registry.LilTaterTradeOffers.tradeOffers;


public class LilTaterTradeOfferTweaker implements Driver {
    @Override
    public void prepareReload(ResourceManager resourceManager) {

    }

    @Override
    public void applyReload(ResourceManager resourceManager, Executor executor) {

    }

    @Override
    public String getApplyMessage() {
        return "LTR libcd script applied xd leleleleleleelel";
    }

    @Override
    public JsonObject getDebugInfo() {
        return null;
    }

    public void addTradeOffer(String profession, int profession_level, Object buyObj, Object secondBuyObj, Object sellObj, int maxUses, int experience, float multiplier){
        try {
            ItemStack buy = RecipeParser.processItemStack(buyObj);
            ItemStack second_buy = RecipeParser.processItemStack(secondBuyObj);
            ItemStack sell = RecipeParser.processItemStack(sellObj);
            String id = RecipeDriver.INSTANCE.getRecipeId(sell).toString();
            VillagerProfession prof = Registry.VILLAGER_PROFESSION.get(new Identifier(profession));
            if (prof == VillagerProfession.NONE) { throw new Exception(); }
            LilTaterTradeOffers.LTRTradeOfferFactory tradeOfferFactory = new LilTaterTradeOffers.LTRTradeOfferFactory(buy, second_buy, sell, maxUses, experience, multiplier);
            tradeOffers.put(id, new LilTaterTradeOffers.LTRTradeOffer(prof, profession_level, tradeOfferFactory));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTradeOffer(String id) {
        try {
            tradeOffers.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

 */
