package de.cheaterpaul.enchantmentmachine.util;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

public class Utils {

    public static String genLangKey(String type, String key) {
        return type + "." + REFERENCE.MODID + "." + key;
    }

    public static Component genTranslation(String type, String key) {
        return Component.translatable(genLangKey(type, key));
    }

    /**
     * Tries to combine the given enchantment instance into the mutable enchantment instance.
     * Returns the enchantment cost.
     *
     * @param enchInst             toApply
     * @param existingEnchantments mutable copy of the exiting enchantments
     * @param reducedPrice         use reduced price (vanilla does this when enchantment comes from a book and not when combining items)
     * @return -1 if incompatible
     */
    public static int tryApplyEnchantment(EnchantmentInstanceMod enchInst, ItemEnchantments.Mutable existingEnchantments, boolean reducedPrice) {
        if(!ModConfig.SERVER.allowMixtureEnchantments.get() && !existingEnchantments.keySet().isEmpty() && !EnchantmentHelper.isEnchantmentCompatible(existingEnchantments.keySet(), enchInst.enchantment())) {
            return -1;
        }
        int level = existingEnchantments.getLevel(enchInst.enchantment());
        if (level > 0) {
            if (level == enchInst.level()) {
                enchInst = new EnchantmentInstanceMod(enchInst.enchantment(), level + 1);
            } else if (level > enchInst.level()) {
                return -1;
            } else {
                enchInst = new EnchantmentInstanceMod(enchInst.enchantment(), enchInst.level());
            }
        }

        if (!enchInst.canEnchant()) return -1;

        var cost = enchInst.getEnchantment().definition().maxCost().calculate(enchInst.level());


        if (reducedPrice) {
            cost = Math.max(1, cost / 2);
        }
        existingEnchantments.upgrade(enchInst.enchantment(), enchInst.level());
        return (int) Math.round(Math.abs(((double) cost) * ModConfig.SERVER.priceModifier.get()));
    }
}
