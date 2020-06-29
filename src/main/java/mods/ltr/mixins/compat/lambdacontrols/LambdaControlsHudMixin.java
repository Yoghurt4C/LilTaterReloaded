package mods.ltr.mixins.compat.lambdacontrols;

import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsHud;
import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.entities.LilTaterBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(LambdaControlsHud.class)
public abstract class LambdaControlsHudMixin {
    @Shadow(remap = false) private BlockHitResult placeHitResult;
    @Shadow(remap = false) private MinecraftClient client;

    @Unique LilTaterBlockEntity ltr_tater = null;

    @ModifyVariable(method = "tick()V", at=@At(value = "FIELD", target = "Lme/lambdaurora/lambdacontrols/client/gui/LambdaControlsHud;placeAction:Ljava/lang/String;", opcode = Opcodes.PUTFIELD, shift = At.Shift.BEFORE, remap = false), name = "placeAction", remap = false)
    private String ltr_determineAction(String placeAction){
        ClientPlayerEntity player = this.client.player;
        if (player == null) { return placeAction; }
        ItemStack stack = player.getMainHandStack();
        if (this.placeHitResult != null && client.world.getBlockState(this.placeHitResult.getBlockPos()).getBlock() instanceof LilTaterBlock) {
            if (this.ltr_tater == null) {
                this.ltr_tater = (LilTaterBlockEntity) client.world.getBlockEntity(this.placeHitResult.getBlockPos());
            }
            ItemStack taterStack = this.ltr_tater!=null ? ltr_tater.getStackForSide(this.placeHitResult.getSide()) : ItemStack.EMPTY;
            if (stack.isEmpty()) {
                if (player.isSneaking() && !taterStack.isEmpty()) {
                    return "lambdacontrols.ltr.disrobe";
                } else {
                    return "lambdacontrols.ltr.pet";
                }
            } else if (!player.isSneaking() && taterStack.isEmpty()) {
                return "lambdacontrols.ltr.furnish";
            }
        } else if (this.ltr_tater!=null) {
            this.ltr_tater = null;
        }
        return placeAction;
    }
}
