package mods.ltr.util;

import java.time.Duration;
import java.time.Instant;

public class DebugTimer {
    public static DebugTimer INSTANCE;

    private Duration atlas = Duration.ZERO;
    private Duration models = Duration.ZERO;

    public void addAtlas(Instant start, Instant finish) {
        this.atlas = this.atlas.plus(Duration.between(start, finish));
    }

    public String getFormattedAtlas() {
        return this.atlas.getSeconds() + "." + this.atlas.getNano() / 1000_000;
    }

    public void addModels(Instant start, Instant finish) {
        this.models = this.models.plus(Duration.between(start, finish));
    }

    public String getFormattedModels() {
        return this.models.getSeconds() + "." + this.models.getNano() / 1000_000;
    }

}
