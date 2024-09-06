package de.cheaterpaul.enchantmentmachine.network.message;

import com.mojang.serialization.Codec;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EnchantingPacket(List<EnchantmentInstanceMod> enchantments) implements CustomPacketPayload {

    public static final Type<EnchantingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "enchanting"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantingPacket> CODEC = StreamCodec.composite(EnchantmentInstanceMod.STREAM_CODEC.apply(ByteBufCodecs.list()), EnchantingPacket::enchantments, EnchantingPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
