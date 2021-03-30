package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.network.IMessage;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EnchantmentPacket implements IMessage {

    private final Object2IntMap<EnchantmentInstance> enchantments;
    private final boolean shouldOpenEnchantmentListScreen;

    public EnchantmentPacket(Object2IntMap<EnchantmentInstance> enchantments, boolean shouldOpenEnchantmentListScreen) {
        this.enchantments = enchantments;
        this.shouldOpenEnchantmentListScreen = shouldOpenEnchantmentListScreen;
    }

    public Object2IntMap<EnchantmentInstance> getEnchantments() {
        return enchantments;
    }

    public boolean shouldOpenEnchantmentScreen() {
        return shouldOpenEnchantmentListScreen;
    }

    public static void encode(EnchantmentPacket msg, PacketBuffer buf) {
        buf.writeBoolean(msg.shouldOpenEnchantmentListScreen);
        buf.writeVarInt(msg.enchantments.size());
        msg.enchantments.forEach((inst, count) -> {
            buf.writeResourceLocation(inst.getEnchantment().getRegistryName());
            buf.writeVarInt(inst.getLevel());
            buf.writeVarInt(count);
        });
    }

    public static EnchantmentPacket decode(PacketBuffer buf) {
        boolean open = buf.readBoolean();
        Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();

        int enchantmentCount = buf.readVarInt();
        for (int i = 0; i < enchantmentCount; i++) {
            ResourceLocation enchantment = buf.readResourceLocation();
            int level = buf.readVarInt();
            int count = buf.readVarInt();
            if (ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
                //noinspection ConstantConditions
                enchantments.put(new EnchantmentInstance(ForgeRegistries.ENCHANTMENTS.getValue(enchantment), level), count);
            }
        }

        return new EnchantmentPacket(enchantments, open);
    }

    public static void handle(final EnchantmentPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> EnchantmentMachineMod.PROXY.handleEnchantmentPacket(msg));
        ctx.setPacketHandled(true);
    }

}
