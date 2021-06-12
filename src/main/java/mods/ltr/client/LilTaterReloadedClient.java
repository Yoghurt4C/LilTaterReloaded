package mods.ltr.client;

import mods.ltr.LilTaterReloaded;
import mods.ltr.client.models.TaterModel;
import mods.ltr.entities.LilTaterBlockEntityRenderer;
import mods.ltr.registry.LilTaterAtlas;
import mods.ltr.util.DebugTimer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;

import static mods.ltr.config.LilTaterReloadedConfig.LOGGER;
import static mods.ltr.config.LilTaterReloadedConfig.logDebug;
import static mods.ltr.registry.LilTaterBlocks.LIL_TATER_BLOCK_ENTITY;

@Environment(EnvType.CLIENT)
public class LilTaterReloadedClient implements ClientModInitializer {
    public static LocalDateTime date = LocalDateTime.now();
    public static boolean isHalloween = false;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(LIL_TATER_BLOCK_ENTITY, LilTaterBlockEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(LilTaterBlockEntityRenderer.taterLayer, TaterModel::getModel);
        if (date.getMonth() == Month.OCTOBER) { isHalloween = true; }
        LilTaterAtlas.init();

        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, consumer) -> {
            logDebug("Initializing the ModelAppender...");
            Instant start = Instant.now();
            Collection<Identifier> models = manager.findResources("models/tater", s -> s.endsWith(".json"));
            models.forEach(id -> {
                try {
                    Identifier input = manager.getResource(id).getId();
                    String name = input.toString().replace(".json","");
                    ModelIdentifier modelId = new ModelIdentifier(name,"");
                    consumer.accept(modelId);
                    logDebug(modelId.toString()+ " got accepted without issues.");
                } catch (IOException e) {
                    LOGGER.error("Error while loading an LTR model JSON ("+id+"): " + e);
                }
            });
            DebugTimer.INSTANCE.addModels(start, Instant.now());
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> {
            logDebug("Initializing the ModelVariantProvider...");
            Instant start = Instant.now();
            Collection<Identifier> models = manager.findResources("models/tater", s -> s.endsWith(".json"));
            return ((modelIdentifier, modelProviderContext) -> {
                if (!models.isEmpty()) {
                    for (Identifier input : models) {
                        if (input.getNamespace().equals(modelIdentifier.getNamespace())) {
                            String name = input.toString().replace(".json", "");
                            ModelIdentifier modelId = new ModelIdentifier(name, "");
                            if (modelIdentifier.getPath().equals(modelId.getPath())) {
                                models.remove(input);
                                logDebug(name + " was loaded without issues.");
                                if (models.isEmpty()) {
                                    logDebug("ModelVariantProvider has finished looping on the LTR side.");
                                    DebugTimer.INSTANCE.addModels(start, Instant.now());
                                }
                                return modelProviderContext.loadModel(new Identifier(name.replace("models/", "")));
                            }
                        }
                    }
                }
                return null;
            });
        });

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> registry.register(LilTaterReloaded.getId("block/imitater_smile")));
    }
}