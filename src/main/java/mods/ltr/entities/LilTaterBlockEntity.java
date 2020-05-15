package mods.ltr.entities;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.registry.LilTaterSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import static mods.ltr.util.RenderStateSetup.getRenderName;
import static mods.ltr.util.RenderStateSetup.getRenderState;

public class LilTaterBlockEntity extends BlockEntity implements Inventory, BlockEntityClientSerializable, Tickable {
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);
    public Text name = null;
    private final static int JUMP_ACTION = 0;
    public int jumpTicks = 0;
    private int nextSound = 0;
    private Object2IntOpenHashMap<Direction> slotMap = new Object2IntOpenHashMap<>();

    @Environment(EnvType.CLIENT)
    public boolean isItem = false;
    public float[] renderColor;
    public LilTaterRenderState renderState;
    public LilTaterTxAnimState txAnimState;

    public LilTaterBlockEntity() { super(LilTaterBlocks.LIL_TATER_BLOCK_ENTITY); }

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction facing = this.getCachedState().get(LilTaterBlock.FACING);
        Direction side = hit.getSide();
        if (this.slotMap.isEmpty() || this.slotMap.getInt(facing)>0) {
            rebuildSlotMap(facing);
        }
        int slot = this.slotMap.getInt(side);

        ItemStack stack = player.getStackInHand(hand);
        ItemStack taterStack = getInvStack(slot);
        boolean shouldFX = true;
        if (!taterStack.isEmpty()) {
            if (player.isSneaking()) {
                ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, taterStack));
                setInvStack(slot, ItemStack.EMPTY);
                shouldFX = false;
            }
        } else if (!stack.isEmpty()) {
            if (!player.isSneaking()) {
                if (stack.getItem() instanceof NameTagItem && stack.hasCustomName() && !stack.getName().equals(this.name)) {
                    if (!world.isClient()) {
                        this.name = Text.Serializer.fromJson(stack.getSubTag("display").getString("Name"));
                    }
                    stack.decrement(1);
                } else {
                    setInvStack(slot, stack.copy().split(1));
                    stack.decrement(1);
                    shouldFX = false;
                }
            }
        }

        int sons = 0;
        for (int i=0; i<getInvSize(); i++) {
            ItemStack sonCheckStack = getInvStack(i);
            if (!sonCheckStack.isEmpty() && sonCheckStack.getItem() instanceof LilTaterBlockItem) {
                sons++;
            }
        }
        if (shouldFX && sons == 0) {
            for (int i = 0; i < 3; i++){
                world.addParticle(ParticleTypes.HEART,pos.getX() + Math.random(), pos.getY() - 0.05 + Math.random(), pos.getZ() + Math.random(), 0.015,0.015,0.015);
            }
        }

        if (!world.isClient()) {
            jump();

            if (sons == 0) {
                if (this.name!=null && nextSound == 0) {
                    if (this.name.getString().replace(" ","_").endsWith("shia_labeouf")) {
                        this.nextSound = 40;
                        world.playSound(null, pos, LilTaterSounds.DO_IT, SoundCategory.BLOCKS, 0.5f, 1f);
                    } else if (this.name.getString().endsWith("honeyboy")) {
                        this.nextSound = 60;
                        world.playSound(null, pos, LilTaterSounds.HWNDU, SoundCategory.BLOCKS, 0.5f, 1f);
                    }
                }
            }

            this.markDirty();
            this.sync();
        } else {
            if (sons > 0) {
                Text text;
                if (this.name != null && this.renderState != null) {
                    text = new LiteralText("<" + this.renderState.renderName + "> ");
                    text.append(new TranslatableText("text.ltr.mySon"));
                    text.append(" ");
                } else text = new TranslatableText("text.ltr.mySon").append(" ");
                if (sons == 1) {
                    text.append(new TranslatableText("text.ltr.mySon_single"));
                } else {
                    text.append(sons + " ");
                    text.append(new TranslatableText("text.ltr.mySon_multiple"));
                }
                player.sendMessage(text);
            }
            this.renderColor=null;
        }
        return ActionResult.SUCCESS;
    }

    private void jump() {
        if (jumpTicks == 0) {
            world.addBlockAction(getPos(), getCachedState().getBlock(), JUMP_ACTION, 20);
        }
    }

    @Override
    public boolean onBlockAction(int id, int data) {
        if (id == JUMP_ACTION) {
            jumpTicks = data;

            if (world.isClient()){
                if (this.renderState!=null && "furious".equals(this.renderState.prefix)) {
                    for (int i=0; i < 3; i++) {
                        world.addParticle(ParticleTypes.ANGRY_VILLAGER,pos.getX() + Math.random(), pos.getY() - 0.25 + Math.random(), pos.getZ() + Math.random(), 0.015,0.015,0.015);
                    }
                }
            }
            return true;
        } else return super.onBlockAction(id, data);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.getCompound("display")!=null){
            this.name=Text.Serializer.fromJson(tag.getCompound("display").getString("Name"));
        }
        betterFromTag(tag,items);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.name!=null) {
            CompoundTag display = new CompoundTag();
            display.put("Name", StringTag.of(Text.Serializer.toJson(name)));
            tag.put("display", display);
        }
        Inventories.toTag(tag,this.items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag){
        this.fromTag(tag);
        if (this.name!=null) {
            String fullName = this.name.getString().toLowerCase().trim().replace(" ", "_");
            if (this.renderState == null || !this.renderState.fullName.equals(fullName)) {
                this.renderState = getRenderState(fullName);
            }
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag){return this.toTag(tag);}

    public static void betterFromTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        ListTag listTag = tag.getList("Items", 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                int j = compoundTag.getByte("Slot") & 255;
                if (j >= 0 && j < stacks.size()) {
                    stacks.set(j, ItemStack.fromTag(compoundTag));
                }
            }
        } else { stacks.clear(); }
    }

    public void readFrom(ItemStack stack) {
        CompoundTag beTag = stack.getSubTag("BlockEntityTag");
        CompoundTag displayTag = stack.getSubTag("display");
        if (beTag!=null) {betterFromTag(beTag,items);}
        if (displayTag!=null) {this.name = Text.Serializer.fromJson(displayTag.getString("Name"));}
    }

    @Override
    public int getInvSize() {
        return items.size();
    }

    @Override
    public boolean isInvEmpty() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInvFull(){
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(items, slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(items, slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getInvMaxStackAmount()) {
            stack.setCount(getInvMaxStackAmount());
        }
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) { return true; }

    @Override
    public void clear() { items.clear(); }

    @Override
    public int getInvMaxStackAmount() { return 1; }

    @Override
    public void tick() {
        if (this.jumpTicks > 0) {
            this.jumpTicks--;
        }

        if (!world.isClient) {
            if (world.random.nextInt(100) == 0) {
                jump();
            }
            if (this.nextSound > 0) {
                this.nextSound--;
            }
        }
    }

    public ItemStack getPickStack(ItemStack stack) {
        if (!this.isInvEmpty()){
            CompoundTag beTag = stack.getOrCreateSubTag("BlockEntityTag");
            Inventories.toTag(beTag, this.items);
        }
        if (this.name!=null) {
            stack.setCustomName(this.name);
        }
        return stack;
    }

    private void rebuildSlotMap(Direction facing){
        this.slotMap.clear();
        this.slotMap.put(facing, 0);
        this.slotMap.put(facing.rotateYCounterclockwise(), 1);
        this.slotMap.put(facing.getOpposite(), 2);
        this.slotMap.put(facing.rotateYClockwise(), 3);
        this.slotMap.put(Direction.UP, 4);
        this.slotMap.put(Direction.DOWN, 5);
    }

    /**
     * Caches the name of the tater on the client.
     * Does NOT exist serverside.
     */
    public static class LilTaterRenderState {
        public String fullName;
        public String prefix;
        public String name;
        public String renderName;
        public double rot;

        public LilTaterRenderState(String fullName, String prefix, String name, double rot){
            this.fullName = fullName;
            this.prefix = prefix;
            this.name = name;
            this.renderName = getRenderName(name, prefix, rot);
            this.rot = rot;
        }
    }

    /**
     * Caches the animation state for ModelPart taters.
     * Does NOT exist serverside.
     */
    public static class LilTaterTxAnimState {
        public long frametime = 0;
        public int currentframe = 0;

        public LilTaterTxAnimState(){}
    }
}
