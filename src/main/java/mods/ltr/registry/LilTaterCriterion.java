package mods.ltr.registry;

import mods.ltr.advancement.MeditationCriterion;
import mods.ltr.mixins.meditation.CriterionsInvoker;

public class LilTaterCriterion {
    public static final MeditationCriterion MEDITATION = CriterionsInvoker.invokeRegister(new MeditationCriterion());

    public static void init() { }
}
