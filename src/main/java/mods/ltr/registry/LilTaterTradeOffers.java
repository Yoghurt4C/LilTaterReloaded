package mods.ltr.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mods.ltr.LilTaterReloaded;
import mods.ltr.client.config.LilTaterReloadedConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;

import static mods.ltr.LilTaterReloaded.GSON;
import static mods.ltr.LilTaterReloaded.LOGGER;
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
                tradeOffers.clear();

                return manager.findResources("trades/tater", s -> {
                    if (LilTaterReloadedConfig.areDefaultTradingOffersLoaded()) {
                        return s.endsWith(".json");
                    } else return !s.contains("ltr_default_trade_offers") && s.endsWith(".json");
                });
            }, executor);
        }

        @Override
        public CompletableFuture<Void> apply(Collection<Identifier> collection, ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.runAsync(() -> {
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
            }, executor);
        }
    }

    private static void handleTrade(Identifier id, String tradeId, JsonObject trade) {
        VillagerProfession profession = null;
        int profession_level = 0;
        Item buy = null;
        Item buy2 = null;
        Item sell = null;
        int price = 1;
        int price2 = 1;
        int count = 1;
        int maxUses = 16;
        int experience = 2;
        float multiplier = 0.05f;
        try {
            try {
                profession = Registry.VILLAGER_PROFESSION.get(new Identifier(trade.get("profession").getAsString()));
            } catch (JsonSyntaxException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                profession_level = trade.get("profession_level").getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                JsonElement buyE = trade.get("buy");
                if (buyE!=null) {
                    buy = Registry.ITEM.get(new Identifier(trade.get("buy").getAsString()));
                } else {
                    buy = Items.EMERALD;
                }
            } catch (JsonSyntaxException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
                e.printStackTrace();
                LOGGER.fatal(trade.get("buy").getAsString() + " is an invalid \"buy\" item, trade will not be loaded!");
            }
            try {
                JsonElement buy2E = trade.get("second_buy");
                if (buy2E!=null) {
                    buy2 = Registry.ITEM.get(new Identifier(trade.get("second_buy").getAsString()));
                }
            } catch (JsonSyntaxException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
                e.printStackTrace();
                LOGGER.fatal(trade.get("second_buy").getAsString() + " is an invalid \"second_buy\" item, trade will not be loaded!");
            }
            try {
                JsonElement sellE = trade.get("sell");
                if (sellE!=null) {
                    sell = Registry.ITEM.get(new Identifier(trade.get("sell").getAsString()));
                } else {
                    sell = Items.EMERALD;
                }
            } catch (JsonSyntaxException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
                LOGGER.fatal(trade.get("sell").getAsString() + " is an invalid \"sell\" item, trade will not be loaded!");
            }
            try {
                price = trade.get("price").getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                JsonElement price2E = trade.get("second_price");
                if (price2E!=null) {
                    price2 = trade.get("second_price").getAsInt();
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                count = trade.get("count").getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                maxUses = trade.get("maxUses").getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                experience = trade.get("experience").getAsInt();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
            try {
                multiplier = trade.get("multiplier").getAsFloat();
            } catch (NumberFormatException e) {
                LOGGER.error("Error while loading an LTR trade JSON (" + id + "): " + e);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } finally {
            if (profession!=null && profession!=VillagerProfession.NONE && buy !=null && sell!=null) {
                if (buy2!=null) {
                    tradeOffers.put(tradeId, new LTRTradeOffer(profession, profession_level, new LTRTradeOfferFactory(buy, buy2, sell, price, price2, count, maxUses, experience, multiplier)));
                } else tradeOffers.put(tradeId, new LTRTradeOffer(profession, profession_level, new LTRTradeOfferFactory(buy, sell, price, count, maxUses, experience, multiplier)));
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

        public LTRTradeOfferFactory(Item buy, Item sell, int price, int count, int maxUses, int experience, float mult) {
            this(new ItemStack(buy, price), new ItemStack(sell, count), maxUses, experience, mult);
        }

        public LTRTradeOfferFactory(ItemStack buy, ItemStack sell, int maxUses, int experience, float multiplier) {
            this.buy = buy;
            this.buy2 = null;
            this.sell = sell;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public LTRTradeOfferFactory(Item buy, Item buy2, Item sell, int price, int price2, int count, int maxUses, int experience, float mult) {
            this(new ItemStack(buy, price), new ItemStack(buy2, price2), new ItemStack(sell, count), maxUses, experience, mult);
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
