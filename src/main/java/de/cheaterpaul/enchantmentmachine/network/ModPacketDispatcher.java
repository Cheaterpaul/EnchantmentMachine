package de.cheaterpaul.enchantmentmachine.network;

import de.cheaterpaul.enchantmentmachine.client.ClientPayloadHandler;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.server.ServerPayloadHandler;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlersEvent event) {
        registerPackets(event.registrar(REFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(PayloadRegistrar registrar) {
        registrar.playToServer(EnchantingPacket.TYPE, EnchantingPacket.CODEC, (pkt, context) -> ServerPayloadHandler.handleEnchantingPacket(pkt, context));
        registrar.playToClient(EnchantmentPacket.TYPE, EnchantmentPacket.CODEC, (pkt, context) -> ClientPayloadHandler.handleEnchantmentPacket(pkt, context));
    }

}
