package de.cheaterpaul.enchantmentmachine.util;

import net.minecraft.enchantment.Enchantment;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EnchantmentInstance {
    @Nonnull
    private final Enchantment enchantment;
    private final int level;


    public EnchantmentInstance(@Nonnull Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnchantmentInstance that = (EnchantmentInstance) o;
        return level == that.level &&
                enchantment.equals(that.enchantment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enchantment, level);
    }

    @Override
    public String toString() {
        return "EnchantmentInstance{" +
                "enchantment=" + enchantment +
                ", level=" + level +
                '}';
    }
}
