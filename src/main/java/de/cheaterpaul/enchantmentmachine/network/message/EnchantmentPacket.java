package de.cheaterpaul.enchantmentmachine.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record EnchantmentPacket(
        Object2IntMap<EnchantmentInstanceMod> enchantments,
        boolean shouldOpenEnchantmentListScreen)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "enchantment");
    public static final Codec<EnchantmentPacket> CODEC = RecordCodecBuilder.create(inst ->
    inst.group(
            Codec.unboundedMap(EnchantmentInstanceMod.CODEC, Codec.INT).xmap(m -> (Object2IntMap<EnchantmentInstanceMod>) new Object2IntArrayMap<>(m), s -> s).fieldOf("enchantments").forGetter(EnchantmentPacket::enchantments),
            Codec.BOOL.fieldOf("shouldOpenEnchantmentListScreen").forGetter(EnchantmentPacket::shouldOpenEnchantmentListScreen)
    ).apply(inst, EnchantmentPacket::new));

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
