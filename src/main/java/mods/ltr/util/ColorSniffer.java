package mods.ltr.util;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import mods.ltr.entities.LilTaterBlockEntity;
import mods.ltr.mixins.SpriteAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ColorSniffer {

    public static int getAverageColor(Sprite sprite) {
        if (sprite == null) {
            return 0xFFFF00FF;
        }

        NativeImage image = ((SpriteAccessor) sprite).getImages()[0];
        int pixelCount = 0;
        long avgR = 0, avgG = 0, avgB = 0;
        for (int y = 0; y < sprite.getHeight(); y++) {
            for (int x = 0; x < sprite.getWidth(); x++) {
                int c = image.getPixelColor(x, y);
                if (((c >> 24) & 0xFF) != 0x00) {
                    avgB += ((c >> 16) & 0xFF) * ((c >> 16) & 0xFF);
                    avgG += ((c >> 8) & 0xFF) * ((c >> 8) & 0xFF);
                    avgR += (c & 0xFF) * (c & 0xFF);
                    pixelCount++;
                }
            }
        }
        if (pixelCount > 0) {
            return 0xFF000000
                    | ((Math.min(255, (int) (Math.sqrt(avgR / pixelCount))) & 0xFF) << 16)
                    | ((Math.min(255, (int) (Math.sqrt(avgG / pixelCount))) & 0xFF) << 8)
                    | ((Math.min(255, (int) (Math.sqrt(avgB / pixelCount))) & 0xFF));
        } else {
            return 0xFFFF00FF;
        }
    }

    public static float[] smushRgbTogether(LilTaterBlockEntity tater) {
        long tempR = 0, tempG = 0, tempB = 0;
        int colors = 0;
        for (int i = 0; i < tater.size(); i++) {
            if (!tater.getStack(i).isEmpty()) {
                ItemStack stack = tater.getStack(i);
                Sprite sprite = MinecraftClient.getInstance().getItemRenderer().getModels().getModelParticleSprite(stack);
                int spriteColor = getAverageColor(sprite);
                int spriteR = ((spriteColor >> 16) & 0xFF);
                int spriteG = ((spriteColor >> 8) & 0xFF);
                int spriteB = (spriteColor & 0xFF);
                tempR += (spriteR * spriteR);
                tempG += (spriteG * spriteG);
                tempB += (spriteB * spriteB);
                colors++;
            }
        }
        float[] avgRGB = new float[]{1f, 1f, 1f};
        if (colors > 0) {
            tempR /= colors;
            tempG /= colors;
            tempB /= colors;
            avgRGB[0] = (float) Math.sqrt(tempR) / 255f;
            avgRGB[1] = (float) Math.sqrt(tempG) / 255f;
            avgRGB[2] = (float) Math.sqrt(tempB) / 255f;
        }
        return avgRGB;
    }

    //offsets usable due to prefixes
    public static float[] spinTheWheelAndLaughAtGod(LilTaterBlockEntity tater, float rOffset, float gOffset, float bOffset) {
        FloatList hues = new FloatArrayList();
        float saturation = 0f, value = 0f;
        for (int i = 0; i < tater.size(); i++) {
            if (!tater.getStack(i).isEmpty()) {
                ItemStack stack = tater.getStack(i);
                Sprite sprite = MinecraftClient.getInstance().getItemRenderer().getModels().getModelParticleSprite(stack);
                int spriteColor = getAverageColor(sprite);
                int spriteR = ((spriteColor >> 16) & 0xFF);
                int spriteG = ((spriteColor >> 8) & 0xFF);
                int spriteB = (spriteColor & 0xFF);
                float[] hsb = rgbToHsv(spriteR, spriteG, spriteB);
                hues.add(hsb[0]);
                saturation += hsb[1];
                value += hsb[2];
            }
        }
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < hues.size(); i++) {
            x += Math.cos(hues.getFloat(i) / 180 * Math.PI);
            y += Math.sin(hues.getFloat(i) / 180 * Math.PI);
        }
        x /= hues.size();
        y /= hues.size();
        saturation /= hues.size();
        value /= hues.size();
        float avgHue = (float) (Math.atan2(y, x) * 180 / Math.PI);
        int avgColor = MathHelper.hsvToRgb(avgHue, saturation, value);
        float[] avgRGB = new float[]{1f, 1f, 1f};
        if (hues.size() > 0) {
            avgRGB[0] = MathHelper.clamp((((avgColor >> 16) & 0xFF) / 255f) + (rOffset - 1f), 0f, 1f);
            avgRGB[1] = MathHelper.clamp((((avgColor >> 8) & 0xFF) / 255f) + (gOffset - 1f), 0f, 1f);
            avgRGB[2] = MathHelper.clamp(((avgColor & 0xFF) / 255f) + (bOffset - 1f), 0f, 1f);
        }
        return avgRGB;
    }

    public static float[] rgbToHsv(int r, int g, int b) {
        float hue, saturation, value;
        float[] hsv = new float[3];
        int cmax = Math.max(r, g);
        if (b > cmax) cmax = b;
        int cmin = Math.min(r, g);
        if (b < cmin) cmin = b;

        value = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsv[0] = hue;
        hsv[1] = saturation;
        hsv[2] = value;
        return hsv;
    }
}
