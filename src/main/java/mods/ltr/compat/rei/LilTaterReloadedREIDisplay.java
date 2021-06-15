package mods.ltr.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mods.ltr.registry.LilTaterBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedREIDisplay implements Display {
    private final EntryStack<ItemStack> input = EntryStacks.of(LilTaterBlocks.LIL_TATER.asItem());
    private final EntryStack<ItemStack> nameTag;
    private final EntryStack<ItemStack> anvil;
    private final EntryStack<ItemStack> result;
    private final boolean isPrefix;

    public LilTaterReloadedREIDisplay(String name, boolean isPrefix) {
        Text nameTag = new LiteralText(name);
        this.nameTag = EntryStacks.of(new ItemStack(Items.NAME_TAG).setCustomName(nameTag));
        this.anvil = EntryStacks.of(new ItemStack(Items.ANVIL).setCustomName(nameTag));
        ItemStack taterStack = new ItemStack(LilTaterBlocks.LIL_TATER.asItem());
        NbtCompound display = new NbtCompound();
        display.put("Name", NbtString.of(Text.Serializer.toJson(nameTag)));
        taterStack.putSubTag("display", display);
        this.result = EntryStacks.of(taterStack);
        this.isPrefix = isPrefix;
    }

    public final EntryStack<ItemStack> getInput() {
        return this.input;
    }

    public final EntryStack<ItemStack> getNameTag() {
        return this.nameTag;
    }

    public final EntryStack<ItemStack> getAnvil() {
        return this.anvil;
    }

    public final EntryStack<ItemStack> getOutput() {
        return this.result;
    }

    public final boolean isPrefix() {
        return this.isPrefix;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Collections.singletonList(EntryIngredient.of(this.input));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(EntryIngredient.of(this.result));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return LilTaterReloadedREIPlugin.LTR;
    }
}
