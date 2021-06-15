package mods.ltr.compat.rei;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mods.ltr.registry.LilTaterBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

import static mods.ltr.LilTaterReloaded.getId;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedREICategory implements DisplayCategory<LilTaterReloadedREIDisplay> {
    public static final EntryStack<ItemStack> logo = EntryStacks.of(LilTaterBlocks.LIL_TATER.asItem());
    protected static final Identifier TATERDROP = getId("textures/gui/taterdrop.png");
    protected static final Identifier TATERDROP_DARK = getId("textures/gui/taterdrop_dark.png");
    protected static final Identifier RECIPECONTAINER = new Identifier("roughlyenoughitems:textures/gui/recipecontainer.png");

    public LilTaterReloadedREICategory() {
    }

    @Override
    public CategoryIdentifier<? extends LilTaterReloadedREIDisplay> getCategoryIdentifier() {
        return LilTaterReloadedREIPlugin.LTR;
    }

    @Override
    public Renderer getIcon() {
        return logo;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText("rei.ltr.category");
    }

    @Override
    public List<Widget> setupDisplay(LilTaterReloadedREIDisplay display, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = Lists.newArrayList();

        if (REIRuntime.getInstance().isDarkThemeEnabled()) {
            widgets.add(Widgets.createTexturedWidget(TATERDROP_DARK, startPoint.x + 55, startPoint.y - 9, 0, 0, 32, 48, 32, 48));
        } else
            widgets.add(Widgets.createTexturedWidget(TATERDROP, startPoint.x + 55, startPoint.y - 9, 0, 0, 32, 48, 32, 48));

        widgets.add(Widgets.createTexturedWidget(RECIPECONTAINER, startPoint.x + 28, startPoint.y + 5, 40, 223, 22, 15));

        widgets.add(Widgets.createSlot(new Point(startPoint.x - 4, startPoint.y + 5)).entry(display.getInput()));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 16, startPoint.y + 14)).entry(display.getAnvil()).disableHighlight().disableBackground());
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 16, startPoint.y - 4)).entry(display.getNameTag()).disableHighlight().disableBackground());
        widgets.add(Widgets.createLabel(new Point(startPoint.x + 16, startPoint.y + 10), new LiteralText("+")));
        widgets.add(new LilTaterWidget(startPoint.x + 56, startPoint.y - 8).entry(display.getOutput()).noBackground());
        if (display.isPrefix()) {
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 92, startPoint.y - 8), new LiteralText("P")));
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 92, startPoint.y - 1), new LiteralText("r")));
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 91, startPoint.y + 6), new LiteralText("e")));
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 91, startPoint.y + 15), new LiteralText("f")));
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 91, startPoint.y + 23), new LiteralText("i")));
            widgets.add(Widgets.createLabel(new Point(startPoint.x + 91, startPoint.y + 30), new LiteralText("x")));
        }
        return widgets;
    }

    public int getDisplayHeight() {
        return 66;
    }
}
