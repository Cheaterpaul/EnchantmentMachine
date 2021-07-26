package de.cheaterpaul.enchantmentmachine.util;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

public class Utils {

    public static String genLangKey(String type, String key) {
        return type + "." + REFERENCE.MODID + "." + key;
    }

    public static Component genTranslation(String type, String key) {
        return new TranslatableComponent(genLangKey(type, key));
    }

    /**
     * Tries to combine the given enchantment instance with an existing list of enchantments.
     * Returns the resulting enchantment instance (can be the same as passed or a combined (higher level) one) as well as the required XP levels
     *
     * @param enchInst             toApply
     * @param existingEnchantments exiting
     * @param reducedPrice         use reduced price (vanilla does this when enchantment comes from a book and not when combining items)
     * @return Null if incompatible
     */
    @Nullable
    public static Pair<EnchantmentInstanceMod, Integer> tryApplyEnchantment(EnchantmentInstanceMod enchInst, Map<Enchantment, Integer> existingEnchantments, boolean reducedPrice) {
        for (Map.Entry<Enchantment, Integer> entry : existingEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment == enchInst.getEnchantment()) { //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                int newLevel = Math.min(enchantment.getMaxLevel(), enchInst.getLevel() == entry.getValue() ? enchInst.getLevel() + 1 : Math.max(enchInst.getLevel(), entry.getValue()));
                enchInst = new EnchantmentInstanceMod(enchantment, newLevel); //Override enchInst in loop.
            } else if (!(enchInst.getEnchantment().isCompatibleWith(enchantment) || ModConfig.SERVER.allowMixtureEnchantments.get())) {
                return null;
            }
        }
        if (!enchInst.canEnchant()) return null;

        int baseCost = switch (enchInst.getEnchantment().getRarity()) {
            case COMMON -> 1;
            case UNCOMMON -> 2;
            case RARE -> 4;
            case VERY_RARE -> 8;
        };

        if (reducedPrice) {
            baseCost = Math.max(1, baseCost / 2);
        }
        return Pair.of(enchInst, baseCost * enchInst.getLevel());
    }
}
