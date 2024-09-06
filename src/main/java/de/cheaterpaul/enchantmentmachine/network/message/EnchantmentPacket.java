package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record EnchantmentPacket(
        Object2IntMap<EnchantmentInstanceMod> enchantments,
        boolean shouldOpenEnchantmentListScreen)
        implements CustomPacketPayload {

    public static final Type<EnchantmentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "enchantment"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2IntArrayMap::new,EnchantmentInstanceMod.STREAM_CODEC,ByteBufCodecs.INT), EnchantmentPacket::enchantments,
            ByteBufCodecs.BOOL, EnchantmentPacket::shouldOpenEnchantmentListScreen,
            EnchantmentPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
