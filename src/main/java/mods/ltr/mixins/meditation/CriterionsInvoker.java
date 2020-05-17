package mods.ltr.mixins.meditation;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criteria.class)
public interface CriterionsInvoker {
    @Invoker
    static <T extends Criterion<?>> T invokeRegister(T criterion) {
        throw new IllegalStateException("Dummy method body should not be invoked. Critical mixin failure.");
    }
}
