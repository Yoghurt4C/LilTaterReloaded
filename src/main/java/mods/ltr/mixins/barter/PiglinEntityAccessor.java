package mods.ltr.mixins.barter;

import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PiglinEntity.class)
public interface PiglinEntityAccessor {
    @Invoker("equipToOffHand")
    void ltr_equipToOffHand(ItemStack stack);
}
