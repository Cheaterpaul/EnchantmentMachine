package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.world.entity.player.Player;

public interface Proxy {

    default void onLoadComplete() {
    }

    default void onClientSetup() {
    }

    default void handleEnchantmentPacket(EnchantmentPacket packet) {
    }

    default void handleEnchantingPacket(EnchantingPacket packet, Player playerEntity) {

    }
}
