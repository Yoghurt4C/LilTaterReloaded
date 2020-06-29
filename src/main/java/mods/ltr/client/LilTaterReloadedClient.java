package mods.ltr.client;

import mods.ltr.LilTaterReloaded;
import mods.ltr.entities.LilTaterBlockEntityRenderer;
import mods.ltr.registry.LilTaterAtlas;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;

import static mods.ltr.LilTaterReloaded.LOGGER;
import static mods.ltr.registry.LilTaterBlocks.LIL_TATER_BLOCK_ENTITY;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedClient implements ClientModInitializer {
    public static LocalDateTime date = LocalDateTime.now();
    public static boolean isHalloween = false;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(LIL_TATER_BLOCK_ENTITY, LilTaterBlockEntityRenderer::new);
        LilTaterAtlas.init();
        if (date.getMonth() == Month.OCTOBER) { isHalloween = true; }

        ModelLoadingRegistry.INSTANCE.registerAppender((manager, consumer) -> {
            Collection<Identifier> models = manager.findResources("models/tater", s -> s.endsWith(".json"));
            models.forEach(id -> {
                try {
                    Identifier input = manager.getResource(id).getId();
                    String name = input.toString().replace(".json","");
                    ModelIdentifier modelId = new ModelIdentifier(name,"");
                    consumer.accept(modelId);
                } catch (IOException e) {
                    LOGGER.error("Error while loading an LTR model JSON ("+id+"): " + e);
                }
            });
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> {
            Collection<Identifier> models = manager.findResources("models/tater", s -> s.endsWith(".json"));
            return ((modelIdentifier, modelProviderContext) -> {
                for (Identifier input : models) {
                    if (input.getNamespace().equals(modelIdentifier.getNamespace())) {
                        String name = input.toString().replace(".json", "");
                        ModelIdentifier modelId = new ModelIdentifier(name, "");
                        if (modelIdentifier.getPath().equals(modelId.getPath())) {
                            return modelProviderContext.loadModel(new Identifier(name.replace("models/", "")));
                        }
                    }
                }
                return null;
            });
        });

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> registry.register(LilTaterReloaded.getId("block/imitater_smile")));
    }
}