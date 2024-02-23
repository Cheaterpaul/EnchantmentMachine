package de.cheaterpaul.enchantmentmachine.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Objects;

public record EnchantmentInstanceMod(@Nonnull Enchantment enchantment, int level) {

    public static final Codec<EnchantmentInstanceMod> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BuiltInRegistries.ENCHANTMENT.byNameCodec().fieldOf("enchantment").forGetter(s -> s.enchantment),
                    Codec.INT.fieldOf("level").forGetter(s -> s.level)
            ).apply(inst, EnchantmentInstanceMod::new));

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
        }).filter(e -> e.getKey().equals(BuiltInRegistries.ENCHANTMENT.getKey(this.enchantment))).noneMatch(a -> a.getValue() < this.level);
    }
}
