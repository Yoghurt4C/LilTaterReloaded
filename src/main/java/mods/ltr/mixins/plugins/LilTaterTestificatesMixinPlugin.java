package mods.ltr.mixins.plugins;

import mods.ltr.config.LilTaterReloadedConfig;

public class LilTaterTestificatesMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return LilTaterReloadedConfig.isSecretTestificateFeatureEnabled();
    }
}
