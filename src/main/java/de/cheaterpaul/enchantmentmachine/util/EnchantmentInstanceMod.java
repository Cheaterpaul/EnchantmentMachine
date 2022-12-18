package de.cheaterpaul.enchantmentmachine.util;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Objects;

public record EnchantmentInstanceMod(@Nonnull Enchantment enchantment, int level) {

    public int getLevel() {
        return level;
    }

    @Nonnull
    public Enchantment getEnchantment() {
        return enchantment;
    }

    public Component getEnchantmentName(){
        return enchantment.getFullname(level);
    }

    public boolean canEnchant() {
        return ModConfig.SERVER.maxEnchantmentLevels.get().stream().map(s -> {
            String[] maxLevels = s.split("\\|");
            return Pair.of(new ResourceLocation(maxLevels[0]), Integer.parseInt(maxLevels[1]));
        }).filter(e -> e.getKey().equals(ForgeRegistries.ENCHANTMENTS.getKey(this.enchantment))).noneMatch(a -> a.getValue() < this.level);
    }
}
