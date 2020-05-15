package mods.ltr.compat.rei;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import mods.ltr.registry.LilTaterBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedREIDisplay implements RecipeDisplay {
    private EntryStack input = EntryStack.create(LilTaterBlocks.LIL_TATER.asItem());
    private EntryStack nameTag;
    private EntryStack anvil;
    private EntryStack result;
    private boolean isPrefix;

    public LilTaterReloadedREIDisplay(String name, boolean isPrefix){
        Text nameTag = new LiteralText(name);
        this.nameTag = EntryStack.create(new ItemStack(Items.NAME_TAG).setCustomName(nameTag));
        this.anvil = EntryStack.create(new ItemStack(Items.ANVIL).setCustomName(nameTag));
        ItemStack taterStack = new ItemStack(LilTaterBlocks.LIL_TATER.asItem());
        CompoundTag display = new CompoundTag();
        display.put("Name", StringTag.of(Text.Serializer.toJson(nameTag)));
        taterStack.putSubTag("display", display);
        this.result = EntryStack.create(taterStack);
        this.isPrefix = isPrefix;
    }

    public final EntryStack getInput() {
        return this.input;
    }

    public final EntryStack getNameTag() { return this.nameTag; }

    public final EntryStack getAnvil() { return this.anvil; }

    public final EntryStack getOutput() {
        return this.result;
    }

    public final boolean isPrefix() {return this.isPrefix;}

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return Collections.singletonList(Collections.singletonList(this.input));
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return Collections.singletonList(this.result);
    }

    @Override
    public Identifier getRecipeCategory() {
        return LilTaterReloadedREIPlugin.LTR;
    }
}
