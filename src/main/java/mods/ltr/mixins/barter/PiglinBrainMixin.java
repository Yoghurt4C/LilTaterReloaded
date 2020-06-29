package mods.ltr.mixins.barter;

import mods.ltr.barter.StopPiggerGrayonInterface;
import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.registry.LilTaterBarterOffers;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Shadow private static boolean hasItemInOffHand(PiglinEntity piglin) { return false; }
    @Shadow private static boolean hasBeenHitByPlayer(PiglinEntity piglin) { return false; }
    @Shadow private static boolean isAdmiringItem(PiglinEntity piglin) { return false; }
    @Shadow private static void setAdmiringItem(LivingEntity entity) { }
    @Shadow private static void doBarter(PiglinEntity piglin, List<ItemStack> list) { }

    @Inject(method = "loot",at = @At("HEAD"), cancellable = true)
    private static void ltr_lootEligibilityCheck(PiglinEntity piglin, ItemEntity drop, CallbackInfo ctx){
        if (!((StopPiggerGrayonInterface)drop).ltr_canBePickedUpByPiggers()) {
            ctx.cancel();
        }
    }

    @Inject(method = "loot",at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void ltr_lootTater(PiglinEntity piglin, ItemEntity drop, CallbackInfo ctx, ItemStack itemStack2, Item item) {
        if (item instanceof LilTaterBlockItem) {
            ctx.cancel();
            if (hasItemInOffHand(piglin)) {
                piglin.dropStack(piglin.getStackInHand(Hand.OFF_HAND));
            }

            ((PiglinEntityAccessor) piglin).ltr_equipToOffHand(itemStack2);
            setAdmiringItem(piglin);
        }
    }

    @Inject(method = "canGather",at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void ltr_canGatherTater(PiglinEntity piglin, ItemStack stack, CallbackInfoReturnable<Boolean> ctx, Item item) {
        if (item instanceof LilTaterBlockItem) {
            if (!hasItemInOffHand(piglin)) {
                ctx.setReturnValue(true);
            } else ctx.setReturnValue(false);
        }
    }

    @Inject(method = "isWillingToTrade",at = @At("HEAD"), cancellable = true)
    private static void ltr_method27086(PiglinEntity piglin, ItemStack stack, CallbackInfoReturnable<Boolean> ctx){
        if (!hasBeenHitByPlayer(piglin) && !isAdmiringItem(piglin) && piglin.isAdult() && stack.getItem() instanceof LilTaterBlockItem) {
            ctx.setReturnValue(true);
        }
    }

    @Inject(method = "consumeOffHandItem",at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/mob/PiglinEntity;isAdult()Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void ltr_acceptsForBarter(PiglinEntity piglin, boolean bl, CallbackInfo ctx, ItemStack stack){
        if (stack.getItem() instanceof LilTaterBlockItem){
            ctx.cancel();
            doBarter(piglin, LilTaterBarterOffers.getBarterTater(stack.copy()));
        }
    }

    @Inject(method = "isGoldHoldingPlayer", at = @At("HEAD"), cancellable = true)
    private static void ltr_isTaterHoldingPlayer(LivingEntity target, CallbackInfoReturnable<Boolean> ctx){
        if (target instanceof PlayerEntity && target.isHolding(item -> item instanceof LilTaterBlockItem)){
            ctx.setReturnValue(true);
        }
    }
}
