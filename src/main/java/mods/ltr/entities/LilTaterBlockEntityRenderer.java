package mods.ltr.entities;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import mods.ltr.client.models.ImitaterModel;
import mods.ltr.config.LilTaterReloadedConfig;
import mods.ltr.entities.LilTaterBlockEntity.LilTaterTxAnimState;
import mods.ltr.items.LilTaterBlockItem;
import mods.ltr.registry.LilTaterBlocks;
import mods.ltr.util.ColorSniffer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Triple;

import static mods.ltr.LilTaterReloaded.getId;
import static mods.ltr.blocks.LilTaterBlock.FACING;
import static mods.ltr.client.LilTaterReloadedClient.isHalloween;
import static mods.ltr.registry.LilTaterAtlas.taterAccessoryAtlas;
import static mods.ltr.registry.LilTaterAtlas.taterAtlas;

public class LilTaterBlockEntityRenderer extends BlockEntityRenderer<LilTaterBlockEntity> {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ModelPart taterModel;
    private static final Identifier defaultId = taterAtlas.get("lil_tater").left().isPresent() ? taterAtlas.get("lil_tater").left().get().getLeft().get(0) : getId("textures/block/lil_tater.png");
    private static final BakedModel pot = MinecraftClient.getInstance().getBlockRenderManager().getModel(Blocks.FLOWER_POT.getDefaultState());
    private static final BlockState defaultState = LilTaterBlocks.LIL_TATER.getDefaultState();
    private static final String imitater_lil = I18n.translate("text.ltr.imitater_lil")+" ";

    public LilTaterBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        taterModel = new ModelPart(16, 16, 0, 0);
        taterModel.addCuboid(0.0f, 0.0f, 0.0f, 4.0f, 7.0f, 4.0f);
        taterModel.setPivot(-2.0f, 18.0f, -2.0f);
        taterModel.setTextureSize(32, 32);
    }

    /**
     * {@see RenderStateSetup#validPrefixes} for the whole list of hardcoded prefixes
     */
    private static RenderLayer getRenderLayer(LilTaterBlockEntity tater) {
        Item upItem = tater.getStackForSide(Direction.UP).getItem();

        if (upItem instanceof BlockItem && ((BlockItem) upItem).getBlock() instanceof PistonBlock) {
            List<Item> sides = new ArrayList<>();
            Direction facing = tater.getCachedState().get(FACING);

            sides.add(tater.getStackForSide(facing.rotateYClockwise()).getItem());
            sides.add(tater.getStackForSide(facing.rotateYCounterclockwise()).getItem());

            if (sides.contains(Items.END_ROD) && sides.contains(Items.LEVER)) {
                return RenderLayer.getEntitySolid(tryToGetAnimatedTexture(tater, "concernedtater"));
            }
        }

        Identifier id = isHalloween ? tryToGetAnimatedTexture(tater, "spook_tater") : defaultId;
        RenderLayer layer;
        if (tater.renderState != null) {
            id = tryToGetAnimatedTexture(tater, tater.renderState.name);
        }
        if (tater.renderState != null && (tater.renderState.prefix.equals("ghastly") || tater.renderState.name.equals("imitater"))) {
            layer = RenderLayer.getEntityTranslucent(id, false);
        } else {
            layer = RenderLayer.getEntitySolid(id);
        }
        return layer;
    }

    private static Identifier tryToGetAnimatedTexture(LilTaterBlockEntity tater, String name) {
        if (taterAtlas.get(name) != null && taterAtlas.get(name).left().isPresent()) {
            Triple<List<Identifier>, IntList, Integer> frameCollection = taterAtlas.get(name).left().get();
            List<Identifier> idList = frameCollection.getLeft();
            IntList frames = frameCollection.getMiddle();
            int rawAnimFrametime = frameCollection.getRight();
            int animFrametime = (rawAnimFrametime / 20) * 1000;
            int size = idList.size();
            if (size > 1 && rawAnimFrametime > 0) {
                if (tater.txAnimState == null) { tater.txAnimState = new LilTaterTxAnimState(); }
                int maxFrames = frames.size();
                return idList.get(frames.getInt(getTimeFrameForAnimation(tater.txAnimState, animFrametime, maxFrames)));
            } else return idList.get(0);
        } else return defaultId;
    }

    private static int getTimeFrameForAnimation(LilTaterTxAnimState animState, int animFrametime, int maxFrames) {
        long currentTime = System.nanoTime() / 1_000_000L;
        if (currentTime - animState.frametime > animFrametime) {
            animState.frametime = currentTime;
            if (animState.currentframe < maxFrames - 1) {
                animState.currentframe++;
            } else animState.currentframe = 0;
        }
        return animState.currentframe;
    }

    @Override
    public void render(LilTaterBlockEntity tater, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay) {
        try {
            matrices.push();
            renderMain(tater, tickDelta, matrices, vcon, light, overlay);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            matrices.pop();
        }
    }

    public void renderMain(LilTaterBlockEntity tater, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay) {
        RenderLayer layer = getRenderLayer(tater);
        String name = tater.renderState != null ? tater.renderState.name : "";
        String prefix = tater.renderState != null ? tater.renderState.prefix : "";
        String[] nameToRender = new String[]{tater.renderState != null ? tater.renderState.renderName : ""};
        double rot = tater.renderState != null ? tater.renderState.rot : 0;
        float r = 1f, g = 1f, b = 1f, a = 1f;
        float[] nameOffset = new float[]{1f};

        matrices.translate(0.5f, 1.5625f, 0.5f);
        matrices.scale(1f, -1f, -1f);
        float rotation = 0f;
        if (tater.getWorld() != null) {
            BlockState state = tater.getCachedState();
            rotation = state.get(FACING).asRotation();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        }
        float rotationZ = 0f;
        if (!prefix.equals("calm")) {
            float jump = tater.jumpTicks;
            if (jump > 0) {
                jump -= tickDelta;
            }
            float jumpf = (float) -Math.abs(Math.sin(jump / 10 * Math.PI)) * 0.2f;
            rotationZ = (float) Math.sin(jump / 10 * Math.PI) * 2;
            matrices.translate(0f, jumpf, 0f);
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationZ));
        }

        matrices.push();
        //prefixes
        switch (prefix) {
            case "dark":
                r = 0.25f;
                g = 0.25f;
                b = 0.25f;
                break;
            case "ghastly":
                matrices.translate(0f, -0.001f, 0f);
                r = 0.75f;
                g = 0.75f;
                b = 0.75f;
                a = 0.5f;
                break;
            case "rotated":
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) rot));
                break;
            case "upside-down":
                matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(180));
                matrices.translate(0f, -2.6874f, 0f);
                break;
            case "potted":
                matrices.push();
                matrices.scale(1f, -1f, -1f);
                matrices.translate(0.5f, -1.5624f, 0.5f);
                matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(180));
                client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vcon.getBuffer(RenderLayers.getEntityBlockLayer(defaultState, true)), null, pot, r, g, b, light, overlay);
                matrices.pop();
                matrices.translate(0f, -0.25f, 0f);
                nameOffset[0] -= 0.25f;
                break;
            case "pehkui":
                float s;
                matrices.translate(0f, 1.5625f, 0f);
                if (rot > 0 && rot < 1) {
                    s = (float) rot;
                } else {
                    s = 0.4f;
                }
                matrices.translate(0f, -1.5625f * s, 0f);
                matrices.scale(s, s, s);
                break;
            case "tinted":
                if (rot != 0) {
                    int t = (int) rot;
                    r = ((t >> 16) & 0xFF) * 255;
                    g = ((t >> 8) & 0xFF) * 255;
                    b = (t & 0xFF) * 255;
                }
        }

        // try to use a blockmodel, fallback to modelparts
        if (taterAtlas.get(name) != null && taterAtlas.get(name).right().isPresent()) {
            matrices.push();
            matrices.scale(1f, -1f, -1f);
            matrices.translate(0.5f, -1.5624, 0.5f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
            BakedModel model = client.getBlockRenderManager().getModels().getModelManager().getModel(taterAtlas.get(name).right().get());
            client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vcon.getBuffer(RenderLayers.getEntityBlockLayer(defaultState, true)), null, model, r, g, b, light, overlay);
            matrices.pop();
            nameOffset[0] = 0.9f;
        } else {
            //names
            if ("imitater".equals(name)) {
                ImitaterModel.draw(tater, matrices, vcon, light, overlay, r, g, b, a);
            } else {
                switch (name) {
                    case "pahimar":
                        matrices.scale(1f, 0.3f, 1f);
                        matrices.translate(0f, 3.65f, 0f);
                        break;
                    case "dinnerbone":
                    case "dinnerbong":
                        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
                        matrices.translate(0, -2.6874, 0);
                        break;
                    case "jeb_":
                        int tempRGB = MathHelper.hsvToRgb(((client.world.getTime() + tickDelta) * 2 % 360) / 360f, 0.75f, 0.9f);
                        r = (tempRGB >> 16 & 0xFF) / 255F;
                        g = (tempRGB >> 8 & 0xFF) / 255F;
                        b = (tempRGB & 0xFF) / 255F;
                        break;
                    case "palettater":
                        if ("rgb".equals(prefix)) {
                            if (tater.renderColor == null) {
                                tater.renderColor = ColorSniffer.smushRgbTogether(tater);
                            } else {
                                r = tater.renderColor[0];
                                g = tater.renderColor[1];
                                b = tater.renderColor[2];
                            }
                        } else {
                            if (tater.renderColor == null) {
                                tater.renderColor = ColorSniffer.spinTheWheelAndLaughAtGod(tater, r, g, b);
                            } else {
                                r = tater.renderColor[0];
                                g = tater.renderColor[1];
                                b = tater.renderColor[2];
                            }
                        }
                        break;
                    case "rotater":
                        double i = 1;
                        if (rot != 0) {
                            i = MathHelper.clamp(rot, -1000, 1000);
                        }
                        if ("counter-clockwise".equals(prefix)) {
                            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float) ((client.world.getTime() + tickDelta) * i % 360)));
                        } else
                            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) ((client.world.getTime() + tickDelta) * i % 360)));
                }
                this.taterModel.render(matrices, vcon.getBuffer(layer), light, overlay, r, g, b, a);
            }
        }
        if (!prefix.isEmpty() && taterAccessoryAtlas.get(prefix) != null) {
            matrices.push();
            matrices.scale(1f, -1f, -1f);
            matrices.translate(0.5f, -1.5625f, 0.5f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
            BakedModel model = client.getBlockRenderManager().getModels().getModelManager().getModel(taterAccessoryAtlas.get(prefix));
            client.getBlockRenderManager().getModelRenderer().render(matrices.peek(), vcon.getBuffer(RenderLayers.getEntityBlockLayer(defaultState, true)), null, model, r, g, b, light, overlay);
            matrices.pop();
            nameOffset[0] = 0.85f;
        }
        renderItems(tater, name, nameToRender, nameOffset, matrices, vcon, light, overlay);
        matrices.pop();
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-rotationZ));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-rotation));
        matrices.scale(1f, -1f, -1f);
        renderName(tater, name, nameToRender[0], nameOffset[0], matrices, vcon, light);
    }

    public void renderItems(LilTaterBlockEntity tater, String name, String[] nameToRender, float[] nameOffset, MatrixStack matrices, VertexConsumerProvider vcon, int light, int overlay) {
        matrices.translate(0f, 1f, 0f);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180f));
        float s = 1f / 3.5f;
        matrices.scale(s, s, s);


        for (int i = 0; i < tater.size(); i++) {
            ItemStack stack = tater.getStack(i);
            if (stack.isEmpty()) {
                continue;
            }

            Item item = stack.getItem();
            boolean isBlock = item instanceof BlockItem && client.getItemRenderer().getModels().getModel(item).isSideLit();
            boolean isMySon = item instanceof LilTaterBlockItem;
            if (name.endsWith("imitater") && i == 4 && !isMySon && isBlock) {
                nameToRender[0] = nameToRender[0].replace("Imitater", imitater_lil + stack.getName().getString());
                continue;
            }

            matrices.push();
            switch (i) {
                case 0:
                    matrices.translate(0, -1.6f, -0.89f);
                    if (isMySon) {
                        matrices.translate(0, 1.4f, 0.5f);
                    } else if (isBlock) {
                        matrices.translate(0, 1.0f, 0.5f);
                    }
                    break;
                case 1:
                    if (isMySon) {
                        matrices.translate(-0.4f, 0.65f, 0f);
                    } else if (isBlock) {
                        matrices.translate(-0.4f, 0.8f, 0f);
                    } else {
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f));
                    }
                    matrices.translate(-0.3f, -1.9f, 0.04f);
                    break;
                case 2:
                    matrices.translate(0, -1.9f, 0.02f);
                    if (isMySon) {
                        matrices.translate(0, 1, 0.6f);
                    } else if (isBlock) {
                        matrices.translate(0, 1, 0.6f);
                    }
                    break;
                case 3:
                    if (isMySon) {
                        matrices.translate(1f, 0.65f, 1f);
                    } else if (isBlock) {
                        matrices.translate(1f, 0.8f, 1f);
                    } else {
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f));
                    }
                    matrices.translate(-0.3f, -1.9f, -0.92f);
                    break;
                case 4:
                    if (isMySon) {
                        matrices.translate(0f, 0.86f, 0.5f);
                    } else if (isBlock) {
                        matrices.translate(0f, 0.55f, 0.5f);
                    }
                    matrices.translate(0f, -0.75f, -0.4f);
                    nameOffset[0] -= 0.15f;
                    break;
                case 5:
                    matrices.translate(0, -2.3f, -0.88f);
                    if (isMySon) {
                        matrices.translate(0, 0.95f, 0.175f);
                    } else if (isBlock) {
                        matrices.translate(0, 0.58, 0.1925f);
                    }
                    break;
            }
            if (isMySon) {
                matrices.scale(1.1f, 1.1f, 1.1f);
            } else if (isBlock) {
                matrices.scale(0.5f, 0.5f, 0.5f);
            }
            if (isBlock && i == 2) {
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180f));
            }
            client.getItemRenderer().renderItem(stack, ModelTransformation.Mode.HEAD, light, overlay, matrices, vcon);
            matrices.pop();
        }
    }

    private void renderName(LilTaterBlockEntity tater, String name, String nameToRender, float nameOffset, MatrixStack matrices, VertexConsumerProvider vcon, int light) {
        HitResult rtr = client.crosshairTarget;
        if (!name.isEmpty() && !tater.isItem && (LilTaterReloadedConfig.areNamesAlwaysVisible() || (rtr != null && rtr.getType() == HitResult.Type.BLOCK && tater.getPos().equals(((BlockHitResult) rtr).getBlockPos())))) {
            matrices.push();
            matrices.translate(0f, -nameOffset, 0f);
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(client.cameraEntity.yaw));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(client.cameraEntity.pitch));
            float f = 0.016666668f * 0.6f;
            matrices.scale(-f, -f, f);
            matrices.translate(0.0f, 0f / f, 0.0f);
            int halfWidth = client.textRenderer.getWidth(nameToRender) / 2;
            float opacity = client.options.getTextBackgroundOpacity(0.25f);
            int opacityRGB = (int) (opacity * 255.0f) << 24;
            client.textRenderer.draw(nameToRender, -halfWidth, 0, 0x20ffffff, false, matrices.peek().getModel(), vcon, true, opacityRGB, light);
            client.textRenderer.draw(nameToRender, -halfWidth, 0, 0xffffffff, false, matrices.peek().getModel(), vcon, false, 0, light);
            if (name.equals("pahimar") || name.equals("soaryn")) {
                matrices.translate(0f, 14f, 0f);
                String str = name.equals("pahimar") ? "[WIP]" : "(soon)";
                halfWidth = client.textRenderer.getWidth(str) / 2;
                client.textRenderer.draw(str, -halfWidth, 0, 0x20ffffff, false, matrices.peek().getModel(), vcon, true, opacityRGB, light);
                client.textRenderer.draw(str, -halfWidth, 0, 0xffffffff, false, matrices.peek().getModel(), vcon, false, 0, light);
            }
            matrices.pop();
        }
    }
}
