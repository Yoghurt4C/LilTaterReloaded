package mods.ltr.advancement;

import com.google.gson.JsonObject;
import mods.ltr.LilTaterReloaded;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MeditationCriterion extends AbstractCriterion<MeditationCriterion.Conditions> {
    private static final Identifier ID = LilTaterReloaded.getId("meditated");

    public Identifier getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, boolean bool) {
        this.test(player, (conditions) -> conditions.matches(bool));
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new Conditions(playerPredicate);
    }

    public static class Conditions extends AbstractCriterionConditions {
        public Conditions(EntityPredicate.Extended player) {
            super(MeditationCriterion.ID, player);
        }

        public boolean matches(boolean bool) {
            return bool;
        }
    }
}
