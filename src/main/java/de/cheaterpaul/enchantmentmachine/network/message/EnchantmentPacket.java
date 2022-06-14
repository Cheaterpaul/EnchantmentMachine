package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.network.IMessage;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record EnchantmentPacket(
        Object2IntMap<EnchantmentInstanceMod> enchantments,
        boolean shouldOpenEnchantmentListScreen)
        implements IMessage {

    public static void encode(EnchantmentPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.shouldOpenEnchantmentListScreen);
        buf.writeVarInt(msg.enchantments.size());
        msg.enchantments.forEach((inst, count) -> {
            //noinspection ConstantConditions
            buf.writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(inst.getEnchantment()));
            buf.writeVarInt(inst.getLevel());
            buf.writeVarInt(count);
        });
    }

    public static EnchantmentPacket decode(FriendlyByteBuf buf) {
        boolean open = buf.readBoolean();
        Object2IntMap<EnchantmentInstanceMod> enchantments = new Object2IntArrayMap<>();

        int enchantmentCount = buf.readVarInt();
        for (int i = 0; i < enchantmentCount; i++) {
            ResourceLocation enchantment = buf.readResourceLocation();
            int level = buf.readVarInt();
            int count = buf.readVarInt();
            if (ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
                //noinspection ConstantConditions
                enchantments.put(new EnchantmentInstanceMod(ForgeRegistries.ENCHANTMENTS.getValue(enchantment), level), count);
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
