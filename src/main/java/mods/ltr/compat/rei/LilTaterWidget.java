package mods.ltr.compat.rei;

import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.widget.EntryWidget;

public class LilTaterWidget extends EntryWidget {

    protected LilTaterWidget(int x, int y) {
        super(x,y);
        this.getBounds().setSize(32,48);
    }

    protected Rectangle getTaterBounds() {
        return new Rectangle(this.getBounds().x-48, this.getBounds().y-60, 128, 128);
    }

    public static LilTaterWidget create(int x, int y) {
        return new LilTaterWidget(x, y);
    }

    @Override
    protected void drawCurrentEntry(int mouseX, int mouseY, float delta) {
        EntryStack entry = this.getCurrentEntry();
        entry.setZ(100);
        entry.render(this.getTaterBounds(), mouseX, mouseY, delta);
    }
}
