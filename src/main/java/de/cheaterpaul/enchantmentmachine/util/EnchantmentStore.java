package de.cheaterpaul.enchantmentmachine.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record EnchantmentStore(Map<EnchantmentInstanceMod, Integer> enchantments) {

    public static final Codec<EnchantmentStore> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Codec.unboundedMap(EnchantmentInstanceMod.CODEC, Codec.INT).fieldOf("enchantments").forGetter(EnchantmentStore::enchantments)
        ).apply(inst, EnchantmentStore::new);
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentStore> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, EnchantmentInstanceMod.STREAM_CODEC, ByteBufCodecs.INT), EnchantmentStore::enchantments,
            EnchantmentStore::new);
}
