package mods.ltr.entities;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.registry.LilTaterSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static mods.ltr.util.RenderStateSetup.getRenderName;
import static mods.ltr.util.RenderStateSetup.getRenderState;

public class LilTaterBlockEntity extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);
    public Text name = null;
    private final static int JUMP_ACTION = 0;
    public int jumpTicks = 0;
    private int nextSound = 0;
    private final Object2IntOpenHashMap<Direction> slotMap = new Object2IntOpenHashMap<>();

    @Environment(EnvType.CLIENT)
    public boolean isItem = false;
    public float[] renderColor;
    public LilTaterRenderState renderState;
    public LilTaterTxAnimState txAnimState;

    public LilTaterBlockEntity(BlockPos pos, BlockState state) {
        super(LilTaterBlocks.LIL_TATER_BLOCK_ENTITY, pos, state);
    }

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction facing = this.getCachedState().get(LilTaterBlock.FACING);
        Direction side = hit.getSide();
        if (this.slotMap.isEmpty() || this.slotMap.getInt(facing) > 0) {
            rebuildSlotMap(facing);
        }
        int slot = this.slotMap.getInt(side);

        ItemStack stack = player.getStackInHand(hand);
        ItemStack taterStack = getStack(slot);
        boolean shouldFX = true;
        if (!taterStack.isEmpty()) {
            if (player.isSneaking()) {
                ItemScatterer.spawn(world, pos, DefaultedList.copyOf(ItemStack.EMPTY, taterStack));
                setStack(slot, ItemStack.EMPTY);
                shouldFX = false;
            }
        } else if (!stack.isEmpty()) {
            if (!player.isSneaking()) {
                if (stack.getItem() instanceof NameTagItem && stack.hasCustomName() && !stack.getName().equals(this.name)) {
                    if (!world.isClient()) {
                        this.name = Text.Serializer.fromJson(stack.getSubNbt("display").getString("Name"));
                    }
                    stack.decrement(1);
                } else {
                    setStack(slot, stack.copy().split(1));
                    stack.decrement(1);
                    shouldFX = false;
                }
            }
        }

        int sons = 0;
        for (int i = 0; i < size(); i++) {
            ItemStack sonCheckStack = getStack(i);
            if (!sonCheckStack.isEmpty() && sonCheckStack.getItem() instanceof LilTaterBlockItem) {
                sons++;
            }
        }
        if (shouldFX && sons == 0) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(ParticleTypes.HEART, pos.getX() + Math.random(), pos.getY() - 0.05 + Math.random(), pos.getZ() + Math.random(), 0.015, 0.015, 0.015);
            }
        }

        if (!world.isClient()) {
            jump(this);

            if (sons == 0) {
                if (this.name != null && nextSound == 0) {
                    if (this.name.getString().replace(" ", "_").endsWith("shia_labeouf")) {
                        this.nextSound = 40;
                        world.playSound(null, pos, LilTaterSounds.DO_IT, SoundCategory.BLOCKS, 0.5f, 1f);
                    } else if (this.name.getString().endsWith("honeyboy")) {
                        this.nextSound = 60;
                        world.playSound(null, pos, LilTaterSounds.HWNDU, SoundCategory.BLOCKS, 0.5f, 1f);
                    }
                }
            }

            this.markDirty();
        } else {
            if (sons > 0) {
                MutableText text;
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
                player.sendSystemMessage(text, player.getUuid());
            }
            this.renderColor = null;
        }
        return ActionResult.SUCCESS;
    }

    private static void jump(LilTaterBlockEntity be) {
        if (be.jumpTicks == 0) {
            be.world.addSyncedBlockEvent(be.getPos(), be.getCachedState().getBlock(), JUMP_ACTION, 20);
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int id, int data) {
        if (id == JUMP_ACTION) {
            jumpTicks = data;

            if (world.isClient()) {
                if (this.renderState != null && "furious".equals(this.renderState.prefix)) {
                    for (int i = 0; i < 3; i++) {
                        world.addParticle(ParticleTypes.ANGRY_VILLAGER, pos.getX() + Math.random(), pos.getY() - 0.25 + Math.random(), pos.getZ() + Math.random(), 0.015, 0.015, 0.015);
                    }
                }
            }
            return true;
        } else return super.onSyncedBlockEvent(id, data);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.getCompound("display") != null) {
            this.name = Text.Serializer.fromJson(tag.getCompound("display").getString("Name"));
            if (world != null && world.isClient && this.name != null) {
                String fullName = this.name.getString().toLowerCase().trim().replace(" ", "_");
                if (this.renderState == null || !this.renderState.fullName.equals(fullName)) {
                    this.renderState = getRenderState(fullName);
                }
            }
        }
        betterFromTag(tag, items);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (this.name != null) {
            NbtCompound display = new NbtCompound();
            display.put("Name", NbtString.of(Text.Serializer.toJson(name)));
            tag.put("display", display);
        }
        Inventories.writeNbt(tag, this.items);
    }

    @Override
    public void markDirty() {
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);
        super.markDirty();
    }

    /*
    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
        if (this.name != null) {
            String fullName = this.name.getString().toLowerCase().trim().replace(" ", "_");
            if (this.renderState == null || !this.renderState.fullName.equals(fullName)) {
                this.renderState = getRenderState(fullName);
            }
        }
    }
     */

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public static void betterFromTag(NbtCompound tag, DefaultedList<ItemStack> stacks) {
        NbtList listTag = tag.getList("Items", 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                NbtCompound NbtCompound = listTag.getCompound(i);
                int j = NbtCompound.getByte("Slot") & 255;
                if (j < stacks.size()) {
                    stacks.set(j, ItemStack.fromNbt(NbtCompound));
                }
            }
        } else {
            stacks.clear();
        }
    }

    public void readFrom(ItemStack stack) {
        NbtCompound beTag = stack.getSubNbt("BlockEntityTag");
        NbtCompound displayTag = stack.getSubNbt("display");
        if (beTag != null) {
            betterFromTag(beTag, items);
        }
        if (displayTag != null) {
            this.name = Text.Serializer.fromJson(displayTag.getString("Name"));
        }
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInvFull() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.items.get(slot);
    }

    public ItemStack getStackForSide(Direction side) {
        return this.items.get(this.slotMap.getInt(side));
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(items, slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity b) {
        LilTaterBlockEntity be = (LilTaterBlockEntity) b;
        if (be.jumpTicks > 0) {
            be.jumpTicks--;
        }

        if (!world.isClient) {
            if (world.random.nextInt(100) == 0) {
                jump(be);
            }
            if (be.nextSound > 0) {
                be.nextSound--;
            }
        }
    }

    public ItemStack getPickStack(ItemStack stack) {
        if (!this.isEmpty()) {
            NbtCompound beTag = stack.getOrCreateSubNbt("BlockEntityTag");
            Inventories.writeNbt(beTag, this.items);
        }
        if (this.name != null) {
            stack.setCustomName(this.name);
        }
        return stack;
    }

    private void rebuildSlotMap(Direction facing) {
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

        public LilTaterRenderState(String fullName, String prefix, String name, double rot) {
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

        public LilTaterTxAnimState() {
        }
    }
}
