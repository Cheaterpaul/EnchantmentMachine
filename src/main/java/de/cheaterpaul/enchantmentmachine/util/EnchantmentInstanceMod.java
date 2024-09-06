package de.cheaterpaul.enchantmentmachine.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public record EnchantmentInstanceMod(@Nonnull Holder<Enchantment> enchantment, int level) {

    public static final Codec<EnchantmentInstanceMod> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Enchantment.CODEC.fieldOf("enchantment").forGetter(EnchantmentInstanceMod::enchantment),
                Codec.INT.fieldOf("level").forGetter(EnchantmentInstanceMod::level)
        ).apply(inst, EnchantmentInstanceMod::new);
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentInstanceMod> STREAM_CODEC = StreamCodec.composite(
            Enchantment.STREAM_CODEC, EnchantmentInstanceMod::enchantment,
            ByteBufCodecs.INT, EnchantmentInstanceMod::level,
            EnchantmentInstanceMod::new);

    @Nonnull
    public Enchantment getEnchantment() {
        return enchantment.value();
    }

    public Component getEnchantmentName(){
        return Enchantment.getFullname(enchantment, level);
    }

    public boolean canEnchant() {
        int maxLevel = ModConfig.SERVER.getMaxEnchantmentLevels().getOrDefault(enchantment.getKey().location(), enchantment.value().getMaxLevel());
        return maxLevel >= level;
    }
}
