package mods.ltr.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

import static mods.ltr.LilTaterReloaded.getId;

public class LilTaterSounds {
    public static SoundEvent HWNDU;
    public static SoundEvent DO_IT;

    public static void init() {
        DO_IT = register("do_it");
        HWNDU = register("hwndu");
    }

    public static SoundEvent register(String name) {
        return Registry.register(Registry.SOUND_EVENT, getId(name), new SoundEvent(getId(name)));
    }
}
