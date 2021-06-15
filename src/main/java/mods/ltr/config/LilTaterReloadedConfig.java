package mods.ltr.config;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mods.ltr.util.DebugTimer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class LilTaterReloadedConfig {
    public static final Logger LOGGER = LogManager.getLogger("Lil Tater Reloaded");
    static boolean isInitialized = false;
    static LTRConfig ltrConfig;

    public static void logDebug(String info) {
        if (shouldLogDebugInfo()) {
            LOGGER.info("[LTR Debug] " + info);
        }
    }

    public static void tryInit() {
        if (!isInitialized()) { init(); }
    }

    public static void init() {
        Object2ObjectOpenHashMap<String, String> cfg = new Object2ObjectOpenHashMap<>();
        ImmutableSet<LTRConfigEntry<?>> entries = ImmutableSet.of(
                LTRConfigEntry.of("totalMeditationTicks", 72000,
                        "totalMeditationTicks: Amount of meditation ticks a player has to experience. [Side: SERVER | Default: 72000]"),
                LTRConfigEntry.of("areNamesAlwaysVisible", false,
                        "areNamesAlwaysVisible: Renders tater names regardless of whether the player is looking at one. [Side: CLIENT | Default: false]"),
                LTRConfigEntry.of("enableMeditation", true,
                        "enableMeditation: Toggles the \"Meditation\" module. [Side : BOTH | Default: true]\n#^If disabled, the meditation progress of all players will be lost permanently, along with the fun and soul of this feature."),
                LTRConfigEntry.of("enableTaterBarter", true,
                        "enableTaterBarter: Toggles the \"Barter\" module. [Side : SERVER | Default: true]\n#^If disabled, Piggers will no longer be attracted to taters, nor will they do anything with them."),
                LTRConfigEntry.of("enableTaterTrading", true,
                        "enableTaterTrading: Toggles the \"Trading\" module. [Side: SERVER | Default: true]\n#^If disabled, ALL custom trade offers won't be read, processed and given to Testificates."),
                LTRConfigEntry.of("loadDefaultTradingOffers", true,
                        "loadDefaultTradingOffers: Controls the loading of LTR's default trade offers. [Side: SERVER | Default: true]\n#^Useful if you don't have a tweaker to remove them with."),
                LTRConfigEntry.of("taterItemRendererCacheSize", 96,
                        "taterItemRendererCacheSize: Amount of taters that can be cached for ITEM rendering. [Side: CLIENT | Default: 96]\n#^Increase if your item taters start \"blinking\" and destroying FPS."),
                LTRConfigEntry.of("loadLambdaControlsCompat", true,
                        "loadLambdaControlsCompat: Toggles the small Mixin into LambdaControls. [Side: CLIENT | Default: true]"),
                LTRConfigEntry.of("enableTestificateSecret", false,
                        "enableTestificateSecret: Toggles the Secret Testificate Feature. Only for the most deviant users. [Side: BOTH | Default: false]"),
                LTRConfigEntry.of("logDebugInfo", false,
                        "logDebugInfo: Toggles logging various information to help cherry-pick possible issues during init or post-init. [Side: BOTH | Default: false]")
        );

        boolean changed = false;
        Path path = FabricLoader.getInstance().getConfigDir().resolve("powertaters/liltaterreloaded");
        File dir = path.toFile();
        File configurationFile = path.resolve("ltr.properties").toFile();
        try {
            if (!configurationFile.exists() && dir.mkdirs() && configurationFile.createNewFile()) {
                LOGGER.info("[LTR] Successfully created the configuration file.");
            }
        } catch (IOException e) {
            LOGGER.error("[LTR] Could not create configuration file: " + configurationFile.getAbsolutePath() + ". Something might be up with write permissions. Using default values internally!");
            // in case of failure, use default values. don't write because there's no file
            for (LTRConfigEntry<?> entry : entries) {
                cfg.put(entry.key, entry.value.toString());
            }
            ltrConfig = new LTRConfig(cfg);
            return;
        } finally {
            isInitialized = true;

        }

        DebugTimer.INSTANCE = new DebugTimer();
        Properties config = new Properties();
        StringBuilder content = new StringBuilder().append("#Lil Tater Configuration.\n");
        content.append("#Last generated at: ").append(new Date()).append("\n\n");
        try {
            FileInputStream input = new FileInputStream(configurationFile);
            config.load(input);
            for (LTRConfigEntry<?> entry : entries) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Class<?> cls = entry.getCls();
                if (config.containsKey(key)) {
                    Object obj = config.getProperty(key);
                    String s = String.valueOf(obj);
                    if (s.equals(Strings.EMPTY)) {
                        LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
                        LOGGER.error("[LTR] Expected configuration value for " + key + " to be present, found nothing. Using default value \"" + value + "\" instead.");
                        cfg.put(key, value.toString());
                    } else if (cls.equals(Integer.class)) {
                        try {
                            Integer.parseInt(s);
                            cfg.put(key, s);
                        } catch (NumberFormatException e) {
                            LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
                            LOGGER.error("[LTR] Expected configuration value for " + key + " to be an integer number, found \"" + s + "\". Using default value \"" + value + "\" instead.");
                            cfg.put(key, value.toString());
                        }
                    } else if (entry.getCls().equals(Boolean.class)) {
                        if (!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
                            LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
                            LOGGER.error("[LTR] Expected configuration value for " + key + " to be a boolean, found \"" + s + "\". Using default value \"" + value + "\" instead.");
                            cfg.put(key, value.toString());
                        } else cfg.put(key, s);
                    }
                } else {
                    changed = true;
                    config.setProperty(key, value.toString());
                    cfg.put(key, value.toString());
                }
                content.append("#").append(entry.getComment()).append("\n");
                content.append(key).append("=").append(cfg.get(key)).append("\n");
            }
            if (changed) {
                FileWriter fw = new FileWriter(configurationFile, false);
                fw.write(content.toString());
                fw.close();
            }
            ltrConfig = new LTRConfig(cfg);
            isInitialized = true;
        } catch (IOException e) {
            LOGGER.error("[LTR] Could not read/write config! Stacktrace: "+ e);
        }
    }

    public static class LTRConfig {
        int totalMeditationTicks;
        boolean areNamesAlwaysVisible;
        boolean enableMeditation;
        boolean enableTaterBarter;
        boolean enableTaterTrading;
        boolean loadDefaultTradingOffers;
        int taterItemRendererCacheSize;
        boolean loadLambdaControlsCompat;
        boolean enableTestificateSecret;
        boolean logDebugInfo;

        public LTRConfig(Map<String, String> map) {
            totalMeditationTicks = Integer.parseInt(map.get("totalMeditationTicks"));
            areNamesAlwaysVisible = Boolean.parseBoolean(map.get("areNamesAlwaysVisible"));
            enableMeditation = Boolean.parseBoolean(map.get("enableMeditation"));
            enableTaterBarter = Boolean.parseBoolean(map.get("enableTaterBarter"));
            enableTaterTrading = Boolean.parseBoolean(map.get("enableTaterTrading"));
            loadDefaultTradingOffers = Boolean.parseBoolean(map.get("loadDefaultTradingOffers"));
            taterItemRendererCacheSize = Integer.parseInt(map.get("taterItemRendererCacheSize"));
            loadLambdaControlsCompat = Boolean.parseBoolean(map.get("loadLambdaControlsCompat"));
            enableTestificateSecret = Boolean.parseBoolean(map.get("enableTestificateSecret"));
            logDebugInfo = Boolean.parseBoolean(map.get("logDebugInfo"));
        }
    }

    public static boolean isInitialized() { return isInitialized; }

    public static int getTotalMeditationTicks() { return ltrConfig.totalMeditationTicks; }
    public static boolean areNamesAlwaysVisible() { return ltrConfig.areNamesAlwaysVisible; }
    public static boolean isMeditationEnabled() { return ltrConfig.enableMeditation; }
    public static boolean isTaterBarterEnabled() { return ltrConfig.enableTaterBarter; }
    public static boolean isTaterTradingEnabled() { return ltrConfig.enableTaterTrading; }
    public static boolean areDefaultTradingOffersLoaded() { return ltrConfig.loadDefaultTradingOffers; }
    public static int getTaterItemRendererCacheSize() { return ltrConfig.taterItemRendererCacheSize; }
    public static boolean isLambdaControlsCompatEnabled() { return ltrConfig.loadLambdaControlsCompat; }
    public static boolean isSecretTestificateFeatureEnabled() { return ltrConfig.enableTestificateSecret; }
    public static boolean shouldLogDebugInfo() { return ltrConfig.logDebugInfo; }

    private static class LTRConfigEntry<T> {
        private final String key;
        private final T value;
        private final WeakReference<String> comment;
        private final Class<T> cls;

        private LTRConfigEntry(String key, T value, String comment, Class<T> cls) {
            this.key = key;
            this.value = value;
            this.comment = new WeakReference<>(comment);
            this.cls = cls;
        }

        public static LTRConfigEntry<Integer> of(String key, int value, String comment) {
            return new LTRConfigEntry<>(key, value, comment, Integer.class);
        }

        public static LTRConfigEntry<Boolean> of(String key, boolean value, String comment) {
            return new LTRConfigEntry<>(key, value, comment, Boolean.class);
        }

        public String getKey() { return key; }
        public T getValue() { return value; }
        public String getComment() { return comment.get(); }
        public Class<T> getCls() { return cls; }
    }
}
