package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public abstract class CommonProxy implements Proxy {

    @Override
    public void handleEnchantingPacket(EnchantingPacket packet, Player playerEntity) {
        if (playerEntity.containerMenu instanceof EnchanterContainer) {
            Optional<EnchanterTileEntity> tile = ((EnchanterContainer) playerEntity.containerMenu).getWorldPosCallable().evaluate((world, pos) -> {
                BlockEntity tile2 = world.getBlockEntity(pos);
                if (tile2 instanceof EnchanterTileEntity) {
                    return ((EnchanterTileEntity) tile2);
                }
                return null;
            });
            tile.ifPresent(enchanterTileEntity -> enchanterTileEntity.executeEnchantments(playerEntity, packet.getEnchantments()));
        }
    }
}
