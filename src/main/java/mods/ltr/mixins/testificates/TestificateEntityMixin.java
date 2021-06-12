package mods.ltr.mixins.testificates;

import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.testificates.TestificateTaterAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerEntity.class)
public abstract class TestificateEntityMixin extends MerchantEntity implements TestificateTaterAccessor {
    @Unique private static final TrackedData<ItemStack> ltr_taterStack = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public TestificateEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at=@At("TAIL"))
    public void ltr_initData(CallbackInfo ctx){
        this.dataTracker.startTracking(ltr_taterStack, ItemStack.EMPTY);
    }

    @Inject(method = "readCustomDataFromNbt", at=@At("TAIL"))
    public void ltr_taterStackFromTag(NbtCompound tag, CallbackInfo ctx){
        this.ltr_setTaterStack(ItemStack.fromNbt(tag.getCompound("ltr_TaterStack")));
    }

    @Inject(method = "writeCustomDataToNbt", at=@At("TAIL"))
    public void ltr_taterStackToTag(NbtCompound tag, CallbackInfo ctx){
        NbtCompound taterTag = new NbtCompound();
        this.ltr_getTaterStack().writeNbt(taterTag);
        tag.put("ltr_TaterStack", taterTag);
    }

    @Inject(method = "interactMob", at=@At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void ltr_interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> ctx, ItemStack stack){
        if (stack.getItem() instanceof ShearsItem) {
            boolean holdingTater = player.getOffHandStack().getItem() instanceof LilTaterBlockItem;
            if (!this.ltr_getTaterStack().isEmpty() && !holdingTater) {
                ItemStack copy = ltr_getTaterStack().copy();
                ltr_setTaterStack(ItemStack.EMPTY);
                dropStack(copy);
                player.getMainHandStack().damage(1, player, playerEntity -> {});
                player.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1f, 1f);
                ctx.setReturnValue(ActionResult.SUCCESS);
            } else if (holdingTater) {
                if (!this.ltr_getTaterStack().isEmpty()) {
                    ItemStack copy = ltr_getTaterStack().copy();
                    ltr_setTaterStack(ItemStack.EMPTY);
                    dropStack(copy);
                }
                player.getMainHandStack().damage(1, player, playerEntity -> {});
                this.ltr_setTaterStack(player.getOffHandStack().split(1));
                player.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1f, 1f);
                ctx.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    public ItemStack ltr_getTaterStack() { return this.dataTracker.get(ltr_taterStack); }

    public void ltr_setTaterStack(ItemStack stack) { this.dataTracker.set(ltr_taterStack, stack); }
}
