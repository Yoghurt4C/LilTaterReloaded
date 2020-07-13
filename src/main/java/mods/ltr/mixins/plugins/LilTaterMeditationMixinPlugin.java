package mods.ltr.mixins.plugins;

import mods.ltr.config.LilTaterReloadedConfig;

public class LilTaterMeditationMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return LilTaterReloadedConfig.isMeditationEnabled();
    }
}
