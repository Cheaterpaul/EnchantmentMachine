package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.entity.player.PlayerEntity;

public interface Proxy {

    default void onLoadComplete() {
    }

    default void onClientSetup() {
    }

    default void handleEnchantmentpacket(EnchantmentPacket packet) {
    }

    default void handleEnchantingPacket(EnchantingPacket packet, PlayerEntity playerEntity){

    }
}
