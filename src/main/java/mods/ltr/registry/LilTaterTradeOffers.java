package mods.ltr.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mods.ltr.LilTaterReloaded;
import mods.ltr.config.LilTaterReloadedConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;

import static mods.ltr.LilTaterReloaded.GSON;
import static mods.ltr.config.LilTaterReloadedConfig.LOGGER;
import static mods.ltr.config.LilTaterReloadedConfig.logDebug;
import static mods.ltr.util.RenderStateSetup.jsonRegex;

public class LilTaterTradeOffers {
    private static final Identifier listenerId = LilTaterReloaded.getId("tradeoffer_resourcelistener");
    public static Object2ObjectOpenHashMap<String, LTRTradeOffer> tradeOffers = new Object2ObjectOpenHashMap<>();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TradeOfferResourceListener());
    }

    public static class TradeOfferResourceListener implements SimpleResourceReloadListener<Collection<Identifier>> {

        @Override
        public Identifier getFabricId() {
            return listenerId;
        }

        @Override
        public CompletableFuture<Collection<Identifier>> load(ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.supplyAsync(()-> {
                logDebug("Preparing the initialization of the Trade Offers. Collecting resources...");
                tradeOffers.clear();

                Collection<Identifier> offers = manager.findResources("trades/tater", s -> {
                    if (LilTaterReloadedConfig.areDefaultTradingOffersLoaded()) {
                        return s.endsWith(".json");
                    } else return !s.contains("ltr_default_trade_offers") && s.endsWith(".json");
                });
                logDebug("Found "+offers.size()+" Trade Offer JSONs. Finished collecting resources.");
                return offers;
            }, executor);
        }

        @Override
        public CompletableFuture<Void> apply(Collection<Identifier> collection, ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.runAsync(() -> {
                logDebug("Applying Trade Offers...");
                collection.forEach(id -> {
                    try {
                        InputStream input = manager.getResource(id).getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(input));
                        JsonObject json = GSON.fromJson(br, JsonObject.class);
                        JsonElement profession = json.get("profession");
                        if (profession != null && !profession.isJsonObject()) {
                            String string = id.toString();
                            Matcher matcher = jsonRegex.matcher(string);
                            if (matcher.find()) {
                                handleTrade(id, id.getNamespace()+":"+matcher.group(1), json);
                            }
                        } else {
                            json.entrySet().forEach(entry -> {
                                String tradeId = id.getNamespace()+":"+entry.getKey();
                                JsonElement element = entry.getValue();
                                if (element.isJsonObject()) {
                                    JsonObject trade = GSON.fromJson(element, JsonObject.class);
                                    handleTrade(id, tradeId, trade);
                                }
                            });
                        }
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                        LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
                    }
                });
                logDebug("Finished initializing the Trade Offers.");
            }, executor);
        }
    }

    private static void handleTrade(Identifier id, String tradeId, JsonObject trade) {
        VillagerProfession profession = VillagerProfession.FARMER;
        int profession_level = 0;
        Item buy = Items.EMERALD;
        Item buy2 = null;
        Item sell = Items.EMERALD;
        int price = 1;
        int price2 = 1;
        int count = 1;
        CompoundTag buyTag = null;
        CompoundTag buy2Tag = null;
        CompoundTag sellTag = null;
        int maxUses = 16;
        int experience = 2;
        float multiplier = 0.05f;
        try {
            JsonElement profE = trade.get("profession");
            if (profE!=null) {
                profession = Registry.VILLAGER_PROFESSION.get(new Identifier(trade.get("profession").getAsString()));
            }
            JsonElement profLevelE = trade.get("profession_level");
            if (profLevelE!=null) {
                profession_level = profLevelE.getAsInt();
            }
            JsonElement buyE = trade.get("buy");
            if (buyE != null) {
                JsonObject obj = buyE.getAsJsonObject();
                if (obj.get("item")!=null) {
                    buy = Registry.ITEM.get(new Identifier(obj.get("item").getAsString()));
                }
                if (obj.get("count")!=null) {
                    price = obj.get("count").getAsInt();
                }
                if (obj.get("nbt")!=null){
                    buyTag = StringNbtReader.parse(obj.get("nbt").getAsString());
                }
            }
            JsonElement buy2E = trade.get("second_buy");
            if (buy2E != null) {
                JsonObject obj = buy2E.getAsJsonObject();
                if (obj.get("item")!=null) {
                    buy2 = Registry.ITEM.get(new Identifier(obj.get("item").getAsString()));
                }
                if (obj.get("count")!=null) {
                    price2 = obj.get("count").getAsInt();
                }
                if (obj.get("nbt")!=null){
                    buy2Tag = StringNbtReader.parse(obj.get("nbt").getAsString());
                }
            }
            JsonElement sellE = trade.get("sell");
            if (sellE != null) {
                JsonObject obj = sellE.getAsJsonObject();
                if (obj.get("item")!=null) {
                    sell = Registry.ITEM.get(new Identifier(obj.get("item").getAsString()));
                }
                if (obj.get("count")!=null) {
                    count = obj.get("count").getAsInt();
                }
                if (obj.get("nbt")!=null){
                    sellTag = StringNbtReader.parse(obj.get("nbt").getAsString());
                }
            }
            JsonElement maxUsesE = trade.get("maxUses");
            if (maxUsesE!=null) {
                maxUses = maxUsesE.getAsInt();
            }
            JsonElement xpE = trade.get("experiience");
            if (xpE != null) {
                experience = xpE.getAsInt();
            }
            JsonElement multiplierE = trade.get("multiplier");
            if (multiplierE!=null) {
                multiplier = multiplierE.getAsFloat();
            }
        } catch (JsonSyntaxException | CommandSyntaxException e) {
            LOGGER.error("[LTR] Error while parsing Trade Offer '"+id+"'. Stacktrace: " + e);
        } finally {
            ItemStack buyStack = new ItemStack(buy, price);
            buyStack.setTag(buyTag);
            ItemStack buy2Stack;
            if (buy2!=null) {
                buy2Stack = new ItemStack(buy2, price2);
                buy2Stack.setTag(buy2Tag);
            } else buy2Stack = ItemStack.EMPTY;
            ItemStack sellStack = new ItemStack(sell, count);
            sellStack.setTag(sellTag);
            if (profession!=VillagerProfession.NONE && (buy !=Items.EMERALD && sell!=Items.EMERALD)) {
                if (buy2!=null) {
                    tradeOffers.put(tradeId, new LTRTradeOffer(profession, profession_level, new LTRTradeOfferFactory(buyStack, buy2Stack, sellStack, maxUses, experience, multiplier)));
                } else tradeOffers.put(tradeId, new LTRTradeOffer(profession, profession_level, new LTRTradeOfferFactory(buyStack, sellStack, maxUses, experience, multiplier)));
            } else {
                LOGGER.error("[LTR] Couldn't add invalid Trade Offer '"+id+"'.");
            }
        }
    }

    public static class LTRTradeOffer {
        private final VillagerProfession profession;
        private final int profession_level;
        private final LTRTradeOfferFactory offer;

        public LTRTradeOffer(VillagerProfession profession, int profession_level, LTRTradeOfferFactory offer) {
            this.profession = profession;
            this.profession_level = profession_level;
            this.offer = offer;
        }

        public VillagerProfession getProfession() { return this.profession; }

        public int getProfessionLevel() { return this.profession_level; }

        public LTRTradeOfferFactory getOffer() { return this.offer; }
    }

    public static class LTRTradeOfferFactory implements TradeOffers.Factory {
        private final ItemStack buy;
        private final ItemStack buy2;
        private final ItemStack sell;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public LTRTradeOfferFactory(ItemStack buy, ItemStack sell, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.buy2 = null;
            this.sell = sell;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public LTRTradeOfferFactory(ItemStack buy, ItemStack buy2, ItemStack sell, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.buy2 = buy2;
            this.sell = sell;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, Random random) {
            if (buy2==null) {
                return new TradeOffer(buy.copy(), sell.copy(), this.maxUses, this.experience, this.multiplier);
            } else {
                return new TradeOffer(buy.copy(), buy2.copy(), sell.copy(), this.maxUses, this.experience, this.multiplier);
            }
        }
    }
}
