package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.network.IMessage;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EnchantingPacket implements IMessage {

    private final List<EnchantmentInstance> enchantments;

    public EnchantingPacket(List<EnchantmentInstance> enchantments) {
        this.enchantments = enchantments;
    }

    public static void encode(EnchantingPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.enchantments.size());
        for (EnchantmentInstance enchantment : msg.enchantments) {
            buf.writeResourceLocation(enchantment.getEnchantment().getRegistryName());
            buf.writeVarInt(enchantment.getLevel());
        }
    }

    public static EnchantingPacket decode(PacketBuffer buf) {
        List<EnchantmentInstance> enchantments = new ArrayList<>();

        int enchantmentCount = buf.readVarInt();
        for (int i = 0; i < enchantmentCount; i++) {
            ResourceLocation enchantment = buf.readResourceLocation();
            int level = buf.readVarInt();
            if (ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
                enchantments.add(new EnchantmentInstance(ForgeRegistries.ENCHANTMENTS.getValue(enchantment), level));
            }
        }

        return new EnchantingPacket(enchantments);
    }

    public static void handle(final EnchantingPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> EnchantmentMachineMod.PROXY.handleEnchantingPacket(msg, contextSupplier.get().getSender()));
        ctx.setPacketHandled(true);
    }

    public List<EnchantmentInstance> getEnchantments() {
        return enchantments;
    }
}
