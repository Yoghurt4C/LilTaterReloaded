package mods.ltr.registry;

import mods.ltr.blocks.LilTaterBlock;
import mods.ltr.entities.LilTaterBlockEntity;
import mods.ltr.items.LilTaterBlockItem;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static mods.ltr.LilTaterReloaded.LilTaterReloadedGroup;
import static mods.ltr.LilTaterReloaded.getId;

public class LilTaterBlocks {
    public static Block LIL_TATER = new LilTaterBlock(AbstractBlock.Settings.of(Material.TNT).nonOpaque());

    public static final BlockEntityType<LilTaterBlockEntity> LIL_TATER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder
            .create(LilTaterBlockEntity::new, LIL_TATER)
            .build(null);

    public static void init() {
        register("lil_tater", LIL_TATER);
        register("lil_tater", LIL_TATER_BLOCK_ENTITY);
    }

    public static BlockItem register(String name, Block block) {
        return register(name, block, new Item.Settings().group(LilTaterReloadedGroup));
    }

    public static BlockItem register(String name, Block block, Item.Settings settings) {
        Identifier id = getId(name);
        Registry.register(Registry.BLOCK, id, block);
        BlockItem item = new LilTaterBlockItem(block, settings);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registry.ITEM, id, item);
        return item;
    }

    public static BlockEntityType<?> register(String name, BlockEntityType<?> type){
        return Registry.register(Registry.BLOCK_ENTITY_TYPE,getId(name), type);
    }
}
