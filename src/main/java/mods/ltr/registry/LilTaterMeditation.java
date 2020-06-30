package mods.ltr.registry;

import mods.ltr.LilTaterReloaded;
import mods.ltr.meditation.LilTaterMeditationAbility;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;

public class LilTaterMeditation {
    public static final Identifier S2C_MEDITATION_PROGRESS = LilTaterReloaded.getId("receive_meditation_progress");

    private static boolean LTR_HAS_MEDITATED;

    public static void init() {
        ClientSidePacketRegistry.INSTANCE.register(S2C_MEDITATION_PROGRESS, (ctx, buf) -> {
            boolean bl = buf.readBoolean();
            if (ctx.getPlayer()!=null) {
                ((LilTaterMeditationAbility)ctx.getPlayer().abilities).ltr_setMeditationState(bl);
            }
            ltr_setMeditationState(bl);
        });
    }

    public static boolean ltr_hasMeditated() { return LTR_HAS_MEDITATED; }

    public static void ltr_setMeditationState(boolean bool) { LTR_HAS_MEDITATED = bool;}
}
