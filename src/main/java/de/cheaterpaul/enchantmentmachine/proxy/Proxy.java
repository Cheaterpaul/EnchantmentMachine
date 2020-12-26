package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;

public interface Proxy {

    default void onLoadComplete() {
    }

    default void handleEnchantmentpacket(EnchantmentPacket tileEntity) {
    }
}
