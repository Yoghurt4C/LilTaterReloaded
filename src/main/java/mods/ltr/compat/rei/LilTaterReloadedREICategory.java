package mods.ltr.compat.rei;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.api.Point;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.widget.EntryWidget;
import me.shedaniel.rei.gui.widget.LabelWidget;
import me.shedaniel.rei.gui.widget.RecipeBaseWidget;
import me.shedaniel.rei.gui.widget.Widget;
import me.shedaniel.rei.impl.ScreenHelper;
import mods.ltr.registry.LilTaterBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static mods.ltr.LilTaterReloaded.getId;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedREICategory implements RecipeCategory<LilTaterReloadedREIDisplay> {
    public static final EntryStack logo = EntryStack.create(LilTaterBlocks.LIL_TATER.asItem());
    protected static final Identifier TATERDROP = getId("textures/gui/taterdrop.png");
    protected static final Identifier TATERDROP_DARK = getId("textures/gui/taterdrop_dark.png");
    protected static final Identifier RECIPECONTAINER = new Identifier("roughlyenoughitems:textures/gui/recipecontainer.png");

    public LilTaterReloadedREICategory() {
    }

    public Identifier getIdentifier() {
        return LilTaterReloadedREIPlugin.LTR;
    }

    public EntryStack getLogo() { return logo; }

    public String getCategoryName() {
        return I18n.translate("rei.ltr.category");
    }

    public List<Widget> setupDisplay(Supplier<LilTaterReloadedREIDisplay> recipeDisplaySupplier, Rectangle bounds) {
        final Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = new LinkedList<>(Collections.singletonList(new RecipeBaseWidget(bounds) {
            public void render(int mouseX, int mouseY, float delta) {
                RenderSystem.pushMatrix();
                this.minecraft.getTextureManager().bindTexture(ScreenHelper.isDarkModeEnabled() ? TATERDROP_DARK : TATERDROP);
                blit(startPoint.x + 55, startPoint.y - 9, 0, 0, 32, 48, 32, 48);
                this.minecraft.getTextureManager().bindTexture(RECIPECONTAINER);
                blit(startPoint.x + 28, startPoint.y + 5, 40, 223, 22, 15);
                RenderSystem.popMatrix();
            }
        }));
        widgets.add(EntryWidget.create(startPoint.x - 4, startPoint.y + 5).entry(recipeDisplaySupplier.get().getInput()));
        widgets.add(EntryWidget.create(startPoint.x + 16, startPoint.y + 14).entry(recipeDisplaySupplier.get().getAnvil()).noHighlight().noBackground());
        widgets.add(EntryWidget.create(startPoint.x + 16, startPoint.y - 4).entry(recipeDisplaySupplier.get().getNameTag()).noHighlight().noBackground());
        widgets.add(LabelWidget.create(new Point(startPoint.x+16, startPoint.y +10),"+"));
        widgets.add(LilTaterWidget.create(startPoint.x + 56, startPoint.y-8).entry(recipeDisplaySupplier.get().getOutput()).noBackground());
        if (recipeDisplaySupplier.get().isPrefix()) {
            widgets.add( LabelWidget.create(new Point(startPoint.x+92, startPoint.y -8),"P"));
            widgets.add( LabelWidget.create(new Point(startPoint.x+92, startPoint.y-1),"r"));
            widgets.add( LabelWidget.create(new Point(startPoint.x+91, startPoint.y +6),"e"));
            widgets.add( LabelWidget.create(new Point(startPoint.x+90, startPoint.y +15),"f"));
            widgets.add( LabelWidget.create(new Point(startPoint.x+90, startPoint.y +23),"i"));
            widgets.add( LabelWidget.create(new Point(startPoint.x+91, startPoint.y +30),"x"));
        }
        return widgets;
    }

    public int getDisplayHeight() {
        return 66;
    }
}
