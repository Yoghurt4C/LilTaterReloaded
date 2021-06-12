package mods.ltr.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;
import net.minecraft.client.util.math.MatrixStack;

public class LilTaterWidget extends EntryWidget {

    protected LilTaterWidget(int x, int y) {
        super(new Point(x, y));
        this.getBounds().setSize(32, 48);
    }

    protected Rectangle getTaterBounds() {
        return new Rectangle(this.getBounds().x - 48, this.getBounds().y - 60, 128, 128);
    }

    @Override
    protected void drawCurrentEntry(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        EntryStack<?> entry = this.getCurrentEntry();
        entry.setZ(100);
        entry.render(matrices, this.getTaterBounds(), mouseX, mouseY, delta);
    }
}
