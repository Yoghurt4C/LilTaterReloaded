package mods.ltr.mixins.plugins;

import mods.ltr.config.Config;

public class LilTaterMeditationMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return Config.enableMeditation;
    }
}
