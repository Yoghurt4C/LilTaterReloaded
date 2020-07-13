package mods.ltr.mixins.compat.plugins;

import mods.ltr.config.LilTaterReloadedConfig;
import mods.ltr.mixins.plugins.LilTaterMixinPlugin;
import net.fabricmc.loader.api.FabricLoader;

public class LambdaControlsCompatMixinPlugin extends LilTaterMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FabricLoader.getInstance().isModLoaded("lambdacontrols") && LilTaterReloadedConfig.isLambdaControlsCompatEnabled();
    }
}
