package de.cheaterpaul.enchantmentmachine.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record EnchantmentStore(Map<EnchantmentInstanceMod, Integer> enchantments) {

    public static final Codec<EnchantmentStore> CODEC = Codec.pair(EnchantmentInstanceMod.CODEC.fieldOf("enchantment").codec(), Codec.INT.fieldOf("count").codec()).listOf().xmap(EnchantmentStore::new, EnchantmentStore::asList);
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentStore> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, EnchantmentInstanceMod.STREAM_CODEC, ByteBufCodecs.INT), EnchantmentStore::enchantments,
            EnchantmentStore::new);

    public EnchantmentStore(List<Pair<EnchantmentInstanceMod, Integer>> enchantments) {
        this(enchantments.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }

    private List<Pair<EnchantmentInstanceMod, Integer>> asList() {
        return enchantments.entrySet().stream().map(yy -> Pair.of(yy.getKey(), yy.getValue())).toList();
    }
}
