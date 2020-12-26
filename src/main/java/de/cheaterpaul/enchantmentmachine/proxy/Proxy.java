package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;

public interface Proxy {

    default void onLoadComplete() {
    }

    default void handleEnchantmentpacket(EnchantmentPacket packet) {
    }

    default void handleEnchantingPacket(EnchantingPacket packet){

    }
}
