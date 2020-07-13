package mods.ltr.mixins.plugins;

import mods.ltr.config.LilTaterReloadedConfig;

public class LilTaterTradingMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return LilTaterReloadedConfig.isTaterTradingEnabled();
    }
}
