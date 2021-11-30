package de.cheaterpaul.enchantmentmachine.network;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;

public class ModPacketDispatcher extends AbstractPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public ModPacketDispatcher() {
        super(NetworkRegistry.ChannelBuilder.named(new ResourceLocation(REFERENCE.MODID, "main")).clientAcceptedVersions(PROTOCOL_VERSION::equals).serverAcceptedVersions(PROTOCOL_VERSION::equals).networkProtocolVersion(() -> PROTOCOL_VERSION).simpleChannel());
    }

    @Override
    public void registerPackets() {
        dispatcher.registerMessage(nextID(), EnchantmentPacket.class, EnchantmentPacket::encode, EnchantmentPacket::decode, EnchantmentPacket::handle);
        dispatcher.registerMessage(nextID(), EnchantingPacket.class, EnchantingPacket::encode, EnchantingPacket::decode, EnchantingPacket::handle);
    }
}
