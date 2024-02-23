package de.cheaterpaul.enchantmentmachine.network.message;

import com.mojang.serialization.Codec;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record EnchantingPacket(List<EnchantmentInstanceMod> enchantments) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "enchanting");
    public static final Codec<EnchantingPacket> CODEC = EnchantmentInstanceMod.CODEC.listOf().xmap(EnchantingPacket::new, EnchantingPacket::enchantments);

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

}
