package mods.ltr.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import mods.ltr.config.LilTaterReloadedConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import static mods.ltr.LilTaterReloaded.getId;
import static mods.ltr.registry.LilTaterAtlas.taterAtlas;
import static mods.ltr.util.RenderStateSetup.toTitleCase;
import static mods.ltr.util.RenderStateSetup.validPrefixes;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedREIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<LilTaterReloadedREIDisplay> LTR = CategoryIdentifier.of(getId("ltr_catalogue"));
    public static boolean SHOW_TATERS = !LilTaterReloadedConfig.isMeditationEnabled();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new LilTaterReloadedREICategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        taterAtlas.keySet().stream().sorted().forEach(name -> {
            if (!"lil_tater".equals(name)) {
                registry.add(new LilTaterReloadedREIDisplay(toTitleCase(name), false));
            }
        });
        registry.add(new LilTaterReloadedREIDisplay("Dinnerbone", false));
        registry.add(new LilTaterReloadedREIDisplay("Pahimar", false));
        registry.add(new LilTaterReloadedREIDisplay("Imitater", false));
        registry.add(new LilTaterReloadedREIDisplay("Rotater", false));
        validPrefixes.forEach(prefix -> {
            switch (prefix) {
                case "RGB":
                case "HSV":
                    prefix = prefix + "_palettater";
                    break;
                case "rotated":
                    prefix = "45_" + prefix + "_lil_tater";
                    break;
                case "counter-clockwise":
                    prefix = "0.5_" + prefix + "_rotater";
                    break;
                case "pehkui":
                    prefix = "0.4_" + prefix + "_lil tater";
                    break;
                case "tinted":
                    prefix = "0x946DFF_"+ prefix +"_irritated_lil_tater";
                    break;
                default:
                    prefix = prefix + "_lil_tater";
            }
            registry.add(new LilTaterReloadedREIDisplay(toTitleCase(prefix), true));
        });
    }
}