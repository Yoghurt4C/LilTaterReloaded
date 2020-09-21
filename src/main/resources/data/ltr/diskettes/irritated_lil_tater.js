var DriverUtils = diskette.require("libdp.util.DriverUtils");
var TradeOfferTweaker = diskette.require("mods.ltr.compat.libcd.LilTaterTradeOfferTweaker");

var irritater = DriverUtils.newStack("ltr:lil_tater", 1);
    irritater.setName("Irritated Lil Tater");
var potion = DriverUtils.newStack("minecraft:potion",1);
    potion.setTagValue("Potion","minecraft:harming");

TradeOfferTweaker.addTradeOffer("minecraft:farmer", 3,
    "ltr:lil_tater", potion, irritater, 4, 10, 0.05);