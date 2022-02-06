package mods.ltr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mods.ltr.config.Config;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.registry.LilTaterCriterion;
import mods.ltr.registry.LilTaterSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static mods.ltr.registry.LilTaterBlocks.LIL_TATER;

public class LilTaterReloaded implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static String modid = "ltr";

    @Override
    public void onInitialize() {
        Config.tryInit();
        LilTaterBlocks.init();
        LilTaterSounds.init();
        LilTaterCriterion.init();
    }

    public static Identifier getId(String name) {
        return new Identifier(modid, name);
    }

    public static ItemGroup LilTaterReloadedGroup = FabricItemGroupBuilder.build(
            getId("core_group"),
            () -> new ItemStack(LIL_TATER));
}
