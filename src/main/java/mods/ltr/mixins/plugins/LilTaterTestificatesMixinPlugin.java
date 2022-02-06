package mods.ltr.mixins.plugins;

import mods.ltr.config.Config;

public class LilTaterTestificatesMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return Config.enableTestificateSecret;
    }
}
