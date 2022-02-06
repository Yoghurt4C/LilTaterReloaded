package mods.ltr.config;

import com.google.common.collect.ImmutableSet;
import mods.ltr.util.DebugTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Config {
    public static final Logger LOGGER = LogManager.getLogger("Lil Tater Reloaded");
    private static boolean isInitialized = false;
    public static int totalMeditationTicks, taterItemRendererCacheSize;
    public static boolean areNamesAlwaysVisible, enableMeditation, enableTaterBarter, enableTaterTrading, loadDefaultTradingOffers, loadLambdaControlsCompat, enableTestificateSecret, logDebugInfo, exploitationOfFreeSoftware;

    public static void logDebug(String info) {
        if (logDebugInfo) {
            LOGGER.info("[LTR Debug] " + info);
        }
    }

    public static void tryInit() {
        if (!isInitialized) {
            init();
            if (!exploitationOfFreeSoftware) {
                String os = System.getProperty("os.name");
                Pattern uhOh = Pattern.compile("windows|wsl", Pattern.CASE_INSENSITIVE);
                if (uhOh.matcher(os).find()) {
                    LOGGER.warn("[LTR] Please set the 'exploitationOfFreeSoftware' property in the configuration file to 'true' to proceed past this initialization step.");
                    LOGGER.warn("[LTR] Note that this is a *joke option*, and does not actually do anything other than bring up this message and kill the JVM.");
                    System.err.println("[main/WARN] [FML]: MOD HAS DIRECT REFERENCE System.exit() THIS IS NOT ALLOWED REROUTING TO FML!");
                    System.exit(-1);
                }
            }
        }
    }

    private static void init() {
        Map<String, String> cfg = new HashMap<>();
        ImmutableSet<? extends Entry<? extends Serializable>> entries = ImmutableSet.of(
                Entry.of("totalMeditationTicks", 72000,
                        "totalMeditationTicks: Amount of meditation ticks a player has to experience. [Side: SERVER | Default: 72000]"),
                Entry.of("areNamesAlwaysVisible", false,
                        "areNamesAlwaysVisible: Renders tater names regardless of whether the player is looking at one. [Side: CLIENT | Default: false]"),
                Entry.of("enableMeditation", true,
                        "enableMeditation: Toggles the \"Meditation\" module. [Side : BOTH | Default: true]\n#^If disabled, the meditation progress of all players will be lost permanently, along with the fun and soul of this feature."),
                Entry.of("enableTaterBarter", true,
                        "enableTaterBarter: Toggles the \"Barter\" module. [Side : SERVER | Default: true]\n#^If disabled, Piggers will no longer be attracted to taters, nor will they do anything with them."),
                Entry.of("enableTaterTrading", true,
                        "enableTaterTrading: Toggles the \"Trading\" module. [Side: SERVER | Default: true]\n#^If disabled, ALL custom trade offers won't be read, processed and given to Testificates."),
                Entry.of("loadDefaultTradingOffers", true,
                        "loadDefaultTradingOffers: Controls the loading of LTR's default trade offers. [Side: SERVER | Default: true]\n#^Useful if you don't have a tweaker to remove them with."),
                Entry.of("taterItemRendererCacheSize", 96,
                        "taterItemRendererCacheSize: Amount of taters that can be cached for ITEM rendering. [Side: CLIENT | Default: 96]\n#^Increase if your item taters start \"blinking\" and destroying FPS."),
                Entry.of("loadLambdaControlsCompat", true,
                        "loadLambdaControlsCompat: Toggles the small Mixin into LambdaControls. [Side: CLIENT | Default: true]"),
                Entry.of("enableTestificateSecret", false,
                        "enableTestificateSecret: Toggles the Secret Testificate Feature. Only for the most deviant users. [Side: BOTH | Default: false]"),
                Entry.of("exploitationOfFreeSoftware", true,
                        "exploitationOfFreeSoftware: Set to 'false' if you don't support exploitation of free software. May have dire consequences. [Side: BOTH | Default: true]"),
                Entry.of("logDebugInfo", false,
                        "logDebugInfo: Toggles logging various information to help cherry-pick possible issues during init or post-init. [Side: BOTH | Default: false]")

        );
        Path configPath = getConfigDir().resolve("powertaters").resolve("liltaterreloaded");
        try {
            boolean changed = false;
            if (Files.notExists(configPath) && !configPath.toFile().mkdirs())
                LOGGER.error("[LTR] Can't create config dirs \"" + configPath.toFile() + "\". This is probably bad.");
            configPath = configPath.resolve("ltr.properties");
            File configurationFile = configPath.toFile();
            StringBuilder content = new StringBuilder().append("#Lil Tater Reloaded Configuration.\n");
            content.append("#Last generated at: ").append(new Date()).append("\n\n");
            if (Files.notExists(configPath) && !configurationFile.createNewFile())
                LOGGER.error("[LTR] Can't create config file \"" + configurationFile + "\". This is probably bad.");
            BufferedReader r = Files.newBufferedReader(configPath, StandardCharsets.UTF_8);

            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;
                String[] kv = line.split("=");
                if (kv.length == 2) cfg.put(kv[0], kv[1]);
            }
            r.close();

            for (Entry<?> entry : entries) {
                String key = entry.key;
                Object value = entry.value;
                Class<?> cls = entry.cls;
                if (cfg.containsKey(key)) {
                    String s = cfg.get(key);
                    if (s.equals("")) {
                        logEntryError(configurationFile, key, value, "nothing", "present");
                    } else if (cls.equals(Integer.class)) {
                        try {
                            setCfgValue(key, Integer.parseInt(s));
                        } catch (NumberFormatException e) {
                            logEntryError(configurationFile, key, value, s, "an integer");
                        }
                    } else if (cls.equals(Float.class)) {
                        try {
                            setCfgValue(key, Float.parseFloat(s));
                        } catch (NumberFormatException e) {
                            logEntryError(configurationFile, key, value, s, "a float");
                        }
                    } else if (cls.equals(Boolean.class)) {
                        if (!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
                            logEntryError(configurationFile, key, value, s, "a boolean");
                        } else setCfgValue(key, Boolean.parseBoolean(s));
                    }
                } else {
                    changed = true;
                    cfg.put(key, value.toString());
                    setCfgValue(key, value);
                }
                content.append("#").append(entry.comment.get()).append("\n");
                content.append(key).append("=").append(cfg.get(key)).append("\n");
            }
            if (changed) {
                Files.write(configPath, Collections.singleton(content.toString()), StandardCharsets.UTF_8);
            }
            isInitialized = true;
            DebugTimer.INSTANCE = new DebugTimer();
        } catch (IOException e) {
            LOGGER.fatal("[LTR] Could not read/write config!");
            LOGGER.fatal(e);
        }
    }

    private static void logEntryError(File configurationFile, String key, Object value, String found, String expected) {
        LOGGER.error("[LTR] Error processing configuration file \"" + configurationFile + "\".");
        LOGGER.error("[LTR] Expected configuration value for " + key + " to be " + expected + ", found \"" + found + "\". Using default value \"" + value + "\" instead.");
        setCfgValue(key, value);
    }

    private static void setCfgValue(String k, Object v) {
        try {
            Config.class.getDeclaredField(k).set(Config.class, v);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("[LTR] Could not set the runtime config state!");
            LOGGER.error(e);
        }
    }

    private static Path getConfigDir() {
        return Path.of(".", "config");
    }

    private static class Entry<T> {
        private final String key;
        private final T value;
        private final WeakReference<String> comment;
        private final Class<T> cls;

        private Entry(String key, T value, String comment, Class<T> cls) {
            this.key = key;
            this.value = value;
            this.comment = new WeakReference<>(comment);
            this.cls = cls;
        }

        public static Entry<Integer> of(String key, int value, String comment) {
            return new Entry<>(key, value, comment, Integer.class);
        }

        public static Entry<Float> of(String key, float value, String comment) {
            return new Entry<>(key, value, comment, Float.class);
        }

        public static Entry<Boolean> of(String key, boolean value, String comment) {
            return new Entry<>(key, value, comment, Boolean.class);
        }
    }
}
