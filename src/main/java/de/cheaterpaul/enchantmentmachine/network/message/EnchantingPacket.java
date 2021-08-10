package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.network.IMessage;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record EnchantingPacket(List<EnchantmentInstanceMod> enchantments) implements IMessage {

    public static void encode(EnchantingPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.enchantments.size());
        for (EnchantmentInstanceMod enchantment : msg.enchantments) {
            //noinspection ConstantConditions
            buf.writeResourceLocation(enchantment.getEnchantment().getRegistryName());
            buf.writeVarInt(enchantment.getLevel());
        }
    }

    public static EnchantingPacket decode(FriendlyByteBuf buf) {
        List<EnchantmentInstanceMod> enchantments = new ArrayList<>();

        int enchantmentCount = buf.readVarInt();
        for (int i = 0; i < enchantmentCount; i++) {
            ResourceLocation enchantment = buf.readResourceLocation();
            int level = buf.readVarInt();
            if (ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
                //noinspection ConstantConditions
                enchantments.add(new EnchantmentInstanceMod(ForgeRegistries.ENCHANTMENTS.getValue(enchantment), level));
            }
        }

        return new EnchantingPacket(enchantments);
    }

    public static void handle(final EnchantingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> EnchantmentMachineMod.PROXY.handleEnchantingPacket(msg, contextSupplier.get().getSender()));
        ctx.setPacketHandled(true);
    }

}
