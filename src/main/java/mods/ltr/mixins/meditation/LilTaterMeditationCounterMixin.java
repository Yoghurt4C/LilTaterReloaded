package mods.ltr.mixins.meditation;

import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.client.config.LilTaterReloadedConfig;
import mods.ltr.compat.LilTaterMeditationAbility;
import mods.ltr.compat.LilTaterMeditationCounter;
import mods.ltr.registry.LilTaterCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class LilTaterMeditationCounterMixin extends LivingEntity implements LilTaterMeditationCounter {
    @Shadow @Final public PlayerAbilities abilities;

    @Unique
    private int LTR_MEDITATION;

    protected LilTaterMeditationCounterMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    public int ltr_getMeditationTicks(){ return this.LTR_MEDITATION; }

    public void ltr_setMeditationTicks(int ticks) { this.LTR_MEDITATION = ticks; }

    public void ltr_tickMeditation() {
        int tick = this.ltr_getMeditationTicks() + 1;
        this.ltr_setMeditationTicks(tick);
        if (tick%(LilTaterReloadedConfig.getTotalMeditationTicks() /20) == 0) {
            this.sendSystemMessage(new TranslatableText("text.ltr.meditation"+random.nextInt(10)).formatted(Formatting.GRAY, Formatting.ITALIC), this.uuid);
        }
        if (tick >= LilTaterReloadedConfig.getTotalMeditationTicks()) {
            ((LilTaterMeditationAbility)this.abilities).ltr_setMeditationState(true);
            MinecraftServer server = this.getServer();
            PlayerManager manager = server.getPlayerManager();
            LilTaterCriterion.MEDITATION.trigger(manager.getPlayer(this.uuid), ((LilTaterMeditationAbility)this.abilities).ltr_hasMeditated());
            manager.getPlayer(this.uuid).networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(this.abilities));
        }
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    public void ltr_readFromTag(CompoundTag tag, CallbackInfo ctx) {
        if (tag.contains("ltr_meditation")) {
            CompoundTag ltrTag = tag.getCompound("ltr_meditation");
            if (ltrTag.contains("ticks")) {
                this.ltr_setMeditationTicks(ltrTag.getInt("ticks"));
            }
        }
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    public void ltr_appendToTag(CompoundTag tag, CallbackInfo ctx){
        if (this.ltr_getMeditationTicks() > 0 && !((LilTaterMeditationAbility)this.abilities).ltr_hasMeditated()) {
            CompoundTag ltrTag = new CompoundTag();
            if (!((LilTaterMeditationAbility)this.abilities).ltr_hasMeditated()) { ltrTag.putInt("ticks", this.ltr_getMeditationTicks()); }
            tag.put("ltr_meditation", ltrTag);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void ltr_tick(CallbackInfo ctx) {
        if (!world.isClient()) {
            if (!((LilTaterMeditationAbility) this.abilities).ltr_hasMeditated()) {
                HitResult rtr = this.rayTrace(6, 1f, false);
                if (rtr != null) {
                    if (rtr.getType().equals(HitResult.Type.BLOCK)) {
                        BlockHitResult brtr = (BlockHitResult) rtr;
                        if (world.getBlockState(brtr.getBlockPos()).getBlock() instanceof LilTaterBlock) {
                            ltr_tickMeditation();
                        }
                    }
                }
            }
        }
    }
}
