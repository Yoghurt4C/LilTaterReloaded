package mods.ltr.client.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Properties;

import static mods.ltr.LilTaterReloaded.LOGGER;

public class LilTaterReloadedConfig {

    private static int totalMeditationTicks = 72000;
    private static boolean areNamesAlwaysVisible = false;
    private static int taterItemRendererCacheSize = 96;

    public static void init() {
        Properties configuration = new Properties();
        configuration.setProperty("totalMeditationTicks", String.valueOf(totalMeditationTicks));
        configuration.setProperty("areNamesAlwaysVisible", String.valueOf(areNamesAlwaysVisible));
        configuration.setProperty("taterItemRendererCacheSize", String.valueOf(taterItemRendererCacheSize));

        File subFolder = new File(FabricLoader.getInstance().getConfigDirectory(), "powertaters");
        if (!subFolder.exists() && !subFolder.mkdir()) {
            LOGGER.error("[LTR] Could not create configuration directory: " + subFolder.getAbsolutePath());
        }
        File subFolder2 = new File(subFolder, "liltaterreloaded");
        if (!subFolder2.exists() && !subFolder2.mkdir()) {
            LOGGER.error("[LTR] Could not create configuration directory: " + subFolder2.getAbsolutePath());
        }
        File configurationFile = new File(subFolder2, "ltr.properties");

        if (configurationFile.exists()) {
            try (InputStream in = new FileInputStream(configurationFile)) {
                configuration.load(in);
            } catch (IOException e) {
                LOGGER.error("[LTR] Could not read configuration file \"" + configurationFile + "\"", e);
            }
        } else {
            try (OutputStream out = new FileOutputStream(configurationFile)) {
                configuration.store(out,"Lil Tater Reloaded configuration.\n#Config explanations:\n#totalMeditationTicks: Amount of meditation ticks a player has to experience. | Side: SERVER | Default: 72000\n#areNamesAlwaysVisible: Renders tater names regardless of whether the player is looking at one. | Side: CLIENT | Default: false\n#taterItemRendererCacheSize: Amount of taters that can be cached for ITEM rendering. Increase if your item taters start ''blinking'' and destroying FPS. | Side: CLIENT | Default: 96");
                LOGGER.info("[LTR] Generated configuration file \"" + configurationFile + "\"");
            } catch (IOException e) {
                LOGGER.error("[LTR] Could not write configuration file \"" + configurationFile + "\"", e);
            }
        }

        String totalMeditationTicksProperty = configuration.getProperty("totalMeditationTicks");
        try {
            totalMeditationTicks = Integer.parseInt(totalMeditationTicksProperty);
        } catch (NumberFormatException e) {
            LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
            LOGGER.error("[LTR] Expected configuration value for totalMeditationTicks to be a number, found \"" + totalMeditationTicksProperty + "\".");
            LOGGER.error("[LTR] Using default value \"" + totalMeditationTicks + "\" instead.");
        }

        String areNamesAlwaysVisibleProperty = configuration.getProperty("areNamesAlwaysVisible");
        try {
            areNamesAlwaysVisible = Boolean.parseBoolean(areNamesAlwaysVisibleProperty);
        } catch (NumberFormatException e) {
            LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
            LOGGER.error("[LTR] Expected configuration value for areNamesAlwaysVisible to be a boolean, found \"" + areNamesAlwaysVisibleProperty + "\".");
            LOGGER.error("[LTR] Using default value \"" + areNamesAlwaysVisible + "\" instead.");
        }

        String taterItemRendererCacheSizeProperty = configuration.getProperty("taterItemRendererCacheSize");
        try {
            taterItemRendererCacheSize = Integer.parseInt(taterItemRendererCacheSizeProperty);
        } catch (NumberFormatException e) {
            LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
            LOGGER.error("[LTR] Expected configuration value for taterItemRendererCacheSize to be a number, found \"" + taterItemRendererCacheSizeProperty + "\".");
            LOGGER.error("[LTR] Using default value \"" + taterItemRendererCacheSize + "\" instead.");
        }
    }

    public static int getTotalMeditationTicks() {
        return totalMeditationTicks;
    }

    public static boolean areNamesAlwaysVisible() {
        return areNamesAlwaysVisible;
    }

    public static int getTaterItemRendererCacheSize() {
        return taterItemRendererCacheSize;
    }
}
