package mods.ltr.registry;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mods.ltr.entities.LilTaterBlockEntityRenderer;
import mods.ltr.registry.LilTaterAtlas.LilTaterResourceListener.LTRResourceCollections;
import mods.ltr.util.DebugTimer;
import mods.ltr.util.RenderStateSetup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.tuple.Triple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;

import static mods.ltr.LilTaterReloaded.GSON;
import static mods.ltr.LilTaterReloaded.getId;
import static mods.ltr.config.LilTaterReloadedConfig.LOGGER;
import static mods.ltr.config.LilTaterReloadedConfig.logDebug;
import static mods.ltr.util.RenderStateSetup.jsonRegex;

@Environment(EnvType.CLIENT)
public class LilTaterAtlas {
    private static final Identifier listenerId = getId("atlas_resourcelistener");
    public static Object2ObjectOpenHashMap<String, Either<Triple<List<Identifier>, IntList, Integer>, ModelIdentifier>> taterAtlas = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<String, ModelIdentifier> taterAccessoryAtlas = new Object2ObjectOpenHashMap<>();

    public static void init() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new LilTaterResourceListener());
    }

    public static class LilTaterResourceListener implements SimpleResourceReloadListener<LilTaterResourceListener.LTRResourceCollections> {

        @Override
        public Identifier getFabricId() {
            return listenerId;
        }

        @Override
        public CompletableFuture<LTRResourceCollections> load(ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.supplyAsync(()-> {
                logDebug("Preparing the initialization of the Tater Atlas. Collecting resources...");
                Instant start = Instant.now();
                taterAtlas.clear();
                taterAccessoryAtlas.clear();
                Collection<Identifier> resources = manager.findResources("powertaters/liltaterreloaded", s -> s.endsWith(".json"));
                logDebug("Found "+resources.size()+" skin definition JSONs.");
                Collection<Identifier> models = manager.findResources("models/tater", s -> s.endsWith(".json"));
                logDebug("Found "+models.size()+" model JSONs. Finished collecting resources.");
                DebugTimer.INSTANCE.addAtlas(start, Instant.now());
                return new LTRResourceCollections(resources, models);
            }, executor);
        }

        @Override
        public CompletableFuture<Void> apply(LTRResourceCollections resourceCollections, ResourceManager manager, Profiler profiler, Executor executor) {
            return CompletableFuture.runAsync(() -> {
                logDebug("Applying resources...");
                Instant start = Instant.now();
                resourceCollections.resources.forEach(id -> {
                    try {
                        InputStream input = manager.getResource(id).getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(input));
                        JsonObject json = GSON.fromJson(br, JsonObject.class);
                        json.entrySet().forEach(entry -> {
                            String name = entry.getKey();
                            JsonElement element = entry.getValue();
                            List<Identifier> idList = new ArrayList<>();
                            IntList frames = new IntArrayList();
                            int[] frametime = new int[1];
                            if (element.isJsonObject()) {
                                JsonObject animatedSprite = element.getAsJsonObject();
                                animatedSprite.entrySet().forEach(nestedEntry -> {
                                    if (nestedEntry.getKey().equals("animation")) {
                                        JsonElement nestedElement = nestedEntry.getValue();
                                        JsonObject animation = nestedElement.getAsJsonObject();
                                        animation.entrySet().forEach(animationEntry -> {
                                            if (animationEntry.getKey().equals("frametime")) {
                                                frametime[0] = Integer.parseInt(animationEntry.getValue().getAsString());
                                            } else {
                                                JsonArray frameArray = animationEntry.getValue().getAsJsonArray();
                                                frameArray.forEach(frame -> {
                                                    frames.add(frame.getAsInt());
                                                });
                                            }
                                        });
                                    } else {
                                        int spriteIndex = Integer.parseInt(nestedEntry.getKey());
                                        String sprite = nestedEntry.getValue().getAsString();
                                        idList.add(spriteIndex, new Identifier(id.getNamespace(),"textures/tater/" + sprite));
                                    }
                                });
                                taterAtlas.put(name, Either.left(Triple.of(idList, frames, frametime[0])));
                            } else {
                                String sprite = element.getAsString();
                                if (!sprite.endsWith(".png") && !name.startsWith("LTRSyntaxComment")) {
                                    LOGGER.warn("Warning! The sprite identifier for \"" + name + "\" in " + br + " does not end with '.png'! Sprite will not be loaded!");
                                }// hopefully there's no race condition and the overwrites happen intentionally
                                if (!name.equals("LTRSyntaxComment")) {
                                    idList.add(new Identifier(id.getNamespace(),"textures/tater/" + sprite));
                                    taterAtlas.put(name, Either.left(Triple.of(idList, frames, 0)));
                                }
                            }
                        });
                        logDebug("Successfully processed and applied " + id.toString());
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                        LOGGER.error("Error while loading an LTR texture JSON (" + id + "): " + e);
                    }
                });

                //models
                logDebug("Applying models...");
                resourceCollections.models.forEach(id -> {
                    try {
                        Identifier input = manager.getResource(id).getId();
                        String name = input.toString();
                        ModelIdentifier modelid = new ModelIdentifier(name.replace(".json", ""), "");
                        Matcher matcher = jsonRegex.matcher(name);
                        if (matcher.find()) {
                            name = matcher.group(1);
                            if (input.getPath().contains("/accessories/")) {
                                taterAccessoryAtlas.put(name, modelid);
                                RenderStateSetup.validPrefixes.add(name);
                            } else {
                                taterAtlas.put(name, Either.right(modelid));
                            }
                            logDebug("Successfully processed and applied " + id.toString());
                        }
                    } catch (JsonSyntaxException | JsonIOException | IOException e) {
                        LOGGER.error("Error while loading an LTR model JSON (" + id + "): " + e);
                    }
                });

                LilTaterBlockEntityRenderer.defaultId = taterAtlas.get("lil_tater").left().isPresent() ? taterAtlas.get("lil_tater").left().get().getLeft().get(0) : getId("textures/block/lil_tater.png");
                logDebug("Finished initializing the Tater Atlas.");
                DebugTimer.INSTANCE.addAtlas(start, Instant.now());
                logDebug("[LTR] Tater Atlas initialization finished in: "+DebugTimer.INSTANCE.getFormattedAtlas()+" seconds. Model initialization finished in: "+DebugTimer.INSTANCE.getFormattedModels()+" seconds.");
            }, executor);
        }

        static class LTRResourceCollections {
            Collection<Identifier> resources;
            Collection<Identifier> models;

            public LTRResourceCollections(Collection<Identifier> resources, Collection<Identifier> models){
                this.resources = resources;
                this.models = models;
            }
        }
    }
}
