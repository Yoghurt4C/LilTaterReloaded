package mods.ltr.util;

import com.google.common.collect.ImmutableSet;
import mods.ltr.entities.LilTaterBlockEntity.LilTaterRenderState;
import net.minecraft.util.Util;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenderStateSetup {
    static final Pattern pattern = Pattern.compile("[^-\\d]*(-?\\d?\\.?\\d+|0x[a-fA-F0-9]{6})[_].*");

    public static List<String> validPrefixes = Util.make(new ArrayList<>(), list -> list.addAll(ImmutableSet.of(
            "ghastly",
            "dark",
            "rotated",
            "furious",
            "upside-down",
            "counter-clockwise",
            "potted",
            "pehkui",
            "tinted",
            "calm",
            "RGB",
            "HSV"
    )));

    public static LilTaterRenderState getRenderState(String fullName) {
        String trimmedName = fullName;
        double rot = 0;
        Matcher matcher = pattern.matcher(fullName);
        if (matcher.find()) {
            String num = matcher.group(1);
            if (num.startsWith("0x")){
                rot = Integer.parseInt(num.substring(2), 16);
            } else {
                rot = Double.parseDouble(num);
            }
            trimmedName = trimmedName.replace(num+"_", "");
            if (trimmedName.startsWith("_")) trimmedName = trimmedName.substring(1);
        }
        String prefix = getPrefix(trimmedName).toLowerCase();
        prefix = !prefix.isEmpty() && trimmedName.startsWith(prefix) ? prefix.substring(0, prefix.length()-1) : "";
        String name = prefix.isEmpty() ? trimmedName : trimmedName.substring(prefix.length()+1);
        return new LilTaterRenderState(fullName, prefix, name, rot);
    }

    public static String toTitleCase(String givenString) {
        String[] splitString = givenString.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : splitString) {
            if (!string.isEmpty()) {
                stringBuilder.append(WordUtils.capitalize(string,'-','.')).append(" ");
            }
        }
        if (givenString.endsWith("_")) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
            stringBuilder.append("_");
        }
        return stringBuilder.toString().trim();
    }

    private static boolean nameStartsWith(String name, String match){
        return name.equals(match) || name.startsWith(match);
    }

    public static String getPrefix(String name) {
        for (String prefix: validPrefixes){
            if (nameStartsWith(name,prefix.toLowerCase())){
                return prefix+"_";
            }
        }
        return "";
    }

    public static String getRenderName(String name, String prefix, double rot) {
        StringBuilder stringBuilder = new StringBuilder();
        if (rot!=0) {
            if (prefix.equals("tinted")){
                int t = (int) rot;
                String sRot = Integer.toHexString(t).toUpperCase();
                stringBuilder.append("0x");
                stringBuilder.append(sRot);
            } else {
                String sRot = Double.toString(rot);
                if (sRot.endsWith(".0")) {
                    stringBuilder.append(sRot.replace(".0", ""));
                } else {
                    stringBuilder.append(sRot);
                }
            }
            if (name.equals("rotater") || prefix.equals("rotated")) {
                stringBuilder.append("°");
            } else if (prefix.equals("pehkui") && rot<1) {
                stringBuilder.append("x");
            }
            stringBuilder.append(" ");
        }
        if (!prefix.isEmpty()) {
            prefix = prefix.toLowerCase();
            String capitalizedPrefix = prefix.toUpperCase();
            boolean uppercase = false;
            for (String validPrefix : validPrefixes) {
                if (capitalizedPrefix.equals(validPrefix)) {
                    uppercase = true;
                    break;
                }
            }
            if (uppercase) {
                stringBuilder.append(capitalizedPrefix).append(" ");
            } else stringBuilder.append(toTitleCase(prefix)).append(" ");
        }
        stringBuilder.append(toTitleCase(name));
        return stringBuilder.toString().trim();
    }
}
