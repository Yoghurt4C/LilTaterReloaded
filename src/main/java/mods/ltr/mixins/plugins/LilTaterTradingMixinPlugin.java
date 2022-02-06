package mods.ltr.mixins.plugins;

import mods.ltr.config.Config;

public class LilTaterTradingMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return Config.enableTaterTrading;
    }
}
