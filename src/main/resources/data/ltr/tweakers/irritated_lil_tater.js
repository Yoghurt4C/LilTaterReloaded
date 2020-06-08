var TweakerUtils = libcd.require("libcd.util.TweakerUtils");
var TradeOfferTweaker = libcd.require("mods.ltr.compat.libcd.LilTaterTradeOfferTweaker");

var irritater = TweakerUtils.createItemStack("ltr:lil_tater", 1);
    irritater = TweakerUtils.setName(irritater,"Irritated Lil Tater");
var potion = TweakerUtils.createItemStack("minecraft:potion",1);
    potion = TweakerUtils.addNbtToStack(potion, "{Potion: \"minecraft:harming\"}");

TradeOfferTweaker.addTradeOffer("minecraft:farmer", 3,
    "ltr:lil_tater", potion, irritater, 4, 10, 0.05);