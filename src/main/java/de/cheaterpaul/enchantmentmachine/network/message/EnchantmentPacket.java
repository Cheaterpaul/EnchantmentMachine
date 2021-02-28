package de.cheaterpaul.enchantmentmachine.network.message;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.network.IMessage;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EnchantmentPacket implements IMessage {

    private final BlockPos pos;
    private final Object2IntMap<EnchantmentInstance> enchantments;
    public static void encode(EnchantmentPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.openEnchantmentListGUI);
        buf.writeVarInt(msg.enchantments.size());
        msg.enchantments.forEach((inst, count) -> {
            buf.writeResourceLocation(inst.getEnchantment().getRegistryName());
            buf.writeVarInt(inst.getLevel());
            buf.writeVarInt(count);
        });
    }

    public static EnchantmentPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        boolean open = buf.readBoolean();
        Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();

        int enchantmentCount = buf.readVarInt();
        for (int i = 0; i < enchantmentCount; i++) {
            ResourceLocation enchantment = buf.readResourceLocation();
            int level = buf.readVarInt();
            int count = buf.readVarInt();
            if (ForgeRegistries.ENCHANTMENTS.containsKey(enchantment)) {
                enchantments.put(new EnchantmentInstance(ForgeRegistries.ENCHANTMENTS.getValue(enchantment), level), count);
            }
        }

        return new EnchantmentPacket(pos, enchantments, open);
    }

    public Object2IntMap<EnchantmentInstance> getEnchantments() {
        return enchantments;
    }

    private final boolean openEnchantmentListGUI;


    public EnchantmentPacket(BlockPos pos, Object2IntMap<EnchantmentInstance> enchantments, boolean openEnchantmentListGUI) {
        this.pos = pos;
        this.enchantments = enchantments;
        this.openEnchantmentListGUI = openEnchantmentListGUI;
    }


    public static void handle(final EnchantmentPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> EnchantmentMachineMod.PROXY.handleEnchantmentpacket(msg));
        ctx.setPacketHandled(true);
    }

    public boolean shouldOpenEnchantmentScreen() {
        return openEnchantmentListGUI;
    }
}
