package de.cheaterpaul.enchantmentmachine.util;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

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

    @Nonnull
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

    public boolean canEnchant() {
        return ModConfig.SERVER.maxEnchantmentLevels.get().stream().map(s -> {
            String[] maxlvels = s.split("\\|");
            return Pair.of(new ResourceLocation(maxlvels[0]), Integer.parseInt(maxlvels[1]));
        }).filter(e -> e.getKey().equals(this.enchantment.getRegistryName())).noneMatch(a -> a.getValue() < this.level);
    }
}
