package de.cheaterpaul.enchantmentmachine.network;

import com.mojang.serialization.Codec;
import de.cheaterpaul.enchantmentmachine.client.ClientPayloadHandler;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.server.ServerPayloadHandler;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ModPacketDispatcher {

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    @SubscribeEvent
    public static void registerHandler(RegisterPayloadHandlerEvent event) {
        registerPackets(event.registrar(REFERENCE.MODID).versioned(PROTOCOL_VERSION));
    }

    @SuppressWarnings("Convert2MethodRef")
    public static void registerPackets(IPayloadRegistrar registrar) {
        registrar.play(EnchantingPacket.ID, jsonReader(EnchantingPacket.CODEC), handlder -> handlder.server(ServerPayloadHandler.getInstance()::handleEnchantingPacket));
        registrar.play(EnchantmentPacket.ID, jsonReader(EnchantmentPacket.CODEC), handlder -> handlder.client((a,b) -> ClientPayloadHandler.handleEnchantmentPacket(a,b)));
    }

    protected static <T> FriendlyByteBuf.Reader<T> jsonReader(Codec<T> codec) {
        return buf -> buf.readJsonWithCodec(codec);
    }

}
