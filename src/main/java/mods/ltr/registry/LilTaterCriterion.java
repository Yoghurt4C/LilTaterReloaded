package mods.ltr.registry;

import mods.ltr.advancement.MeditationCriterion;
import mods.ltr.mixins.CriterionsInvoker;

public class LilTaterCriterion {
    //still called because of the actual advancement
    public static final MeditationCriterion MEDITATION = CriterionsInvoker.invokeRegister(new MeditationCriterion());

    public static void init() {
    }
}
