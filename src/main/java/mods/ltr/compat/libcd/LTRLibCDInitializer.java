package mods.ltr.compat.libcd;

import io.github.cottonmc.libdp.api.DriverInitializer;
import io.github.cottonmc.libdp.api.driver.DriverManager;

public class LTRLibCDInitializer implements DriverInitializer {
    @Override
    public void init(DriverManager manager) {
        manager.addAssistant("mods.ltr.compat.libcd.LilTaterTradeOfferTweaker", new LilTaterTradeOfferTweaker());
    }
}