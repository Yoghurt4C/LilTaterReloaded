package mods.ltr.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.ltr.LilTaterReloaded;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MeditationCriterion extends AbstractCriterion<MeditationCriterion.Conditions> {
    private static final Identifier ID = LilTaterReloaded.getId("meditated");

    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject obj, JsonDeserializationContext context) {
        return new Conditions();
    }

    public void trigger(ServerPlayerEntity player, boolean bool) {
        this.test(player.getAdvancementTracker(), (conditions) -> conditions.matches(bool));
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions() {
            super(MeditationCriterion.ID);
        }

        public boolean matches(boolean bool) {
            return bool;
        }
    }
}
