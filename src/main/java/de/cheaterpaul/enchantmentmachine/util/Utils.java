package de.cheaterpaul.enchantmentmachine.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class Utils {

    public static String genLangKey(String type, String key) {
        return type + "." + REFERENCE.MODID + "." + key;
    }

    public static ITextComponent genTranslation(String type, String key) {
        return new TranslationTextComponent(genLangKey(type, key));
    }
}
