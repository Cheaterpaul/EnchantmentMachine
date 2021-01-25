package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.TextureStitchEvent;

public interface Proxy {

    default void onLoadComplete() {
    }

    default void onClientSetup() {
    }

    default void onTextureStitchEvent(TextureStitchEvent.Pre event){
    }

    default void handleEnchantmentpacket(EnchantmentPacket packet) {
    }

    default void handleEnchantingPacket(EnchantingPacket packet, PlayerEntity playerEntity){

    }
}
