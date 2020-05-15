package mods.ltr;

import mods.ltr.client.config.LilTaterReloadedConfig;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.registry.LilTaterCriterion;
import mods.ltr.registry.LilTaterSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mods.ltr.registry.LilTaterBlocks.LIL_TATER;

public class LilTaterReloaded implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Lil Tater Reloaded");
    public static String modid = "ltr";

    @Override
    public void onInitialize(){
        LilTaterBlocks.init();
        LilTaterSounds.init();
        LilTaterReloadedConfig.init();
        LilTaterCriterion.init();
    }

    public static Identifier getId(String name) {
        return new Identifier(modid, name);
    }

    public static ItemGroup LilTaterReloadedGroup = FabricItemGroupBuilder.build(
            getId("core_group"),
            () -> new ItemStack(LIL_TATER));
}
