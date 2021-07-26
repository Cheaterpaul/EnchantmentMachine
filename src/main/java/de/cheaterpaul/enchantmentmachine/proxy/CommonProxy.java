package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public abstract class CommonProxy implements Proxy {

    @Override
    public void handleEnchantingPacket(EnchantingPacket packet, Player playerEntity) {
        if (playerEntity.containerMenu instanceof EnchanterContainerMenu) {
            Optional<EnchanterBlockEntity> tile = ((EnchanterContainerMenu) playerEntity.containerMenu).getWorldPosCallable().evaluate((world, pos) -> {
                BlockEntity tile2 = world.getBlockEntity(pos);
                if (tile2 instanceof EnchanterBlockEntity) {
                    return ((EnchanterBlockEntity) tile2);
                }
                return null;
            });
            tile.ifPresent(enchanterBlockEntity -> enchanterBlockEntity.executeEnchantments(playerEntity, packet.getEnchantments()));
        }
    }
}
