package de.cheaterpaul.enchantmentmachine.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

public class Utils {

    public static String genLangKey(String type, String key) {
        return type + "." + REFERENCE.MODID + "." + key;
    }

    public static ITextComponent genTranslation(String type, String key) {
        return new TranslationTextComponent(genLangKey(type, key));
    }

    /**
     * Tries to combine the given enchantment instance with a exisiting list of enchantments.
     * Returns the resulting enchantment instance (can be the same as passed or a combined (higher level) one) as well as the required XP levels
     *
     * @param enchInst             toApply
     * @param existingEnchantments exiting
     * @param isBook               if targeting a book (for XP cost)
     * @return Null if incompatible
     */
    @Nullable
    public static Pair<EnchantmentInstance, Integer> tryApplyEnchantment(EnchantmentInstance enchInst, Map<Enchantment, Integer> existingEnchantments, boolean isBook) {
        for (Map.Entry<Enchantment, Integer> entry : existingEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == enchInst.getEnchantment()) { //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                int newLevel = Math.min(enchantment.getMaxLevel(), enchInst.getLevel() == entry.getValue() ? enchInst.getLevel() + 1 : Math.max(enchInst.getLevel(), entry.getValue()));
                enchInst = new EnchantmentInstance(enchantment, newLevel); //Override enchInst in loop.
                continue;
            } else if (!enchInst.getEnchantment().isCompatibleWith(enchantment)) {
                return null;
            }
        }
        int baseCost = 0;
        switch (enchInst.getEnchantment().getRarity()) {
            case COMMON:
                baseCost = 1;
                break;
            case UNCOMMON:
                baseCost = 2;
                break;
            case RARE:
                baseCost = 4;
                break;
            case VERY_RARE:
                baseCost = 8;
        }

        if (isBook) {
            baseCost = Math.max(1, baseCost / 2);
        }
        return Pair.of(enchInst, baseCost * enchInst.getLevel());
    }
}
