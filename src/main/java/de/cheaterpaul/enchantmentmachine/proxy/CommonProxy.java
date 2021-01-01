package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public abstract class CommonProxy implements Proxy {

    @Override
    public void handleEnchantingPacket(EnchantingPacket packet, PlayerEntity playerEntity) {
        if (playerEntity.openContainer instanceof EnchanterContainer) {
            Optional<EnchanterTileEntity> tile =  ((EnchanterContainer) playerEntity.openContainer).getWorldPosCallable().apply((world, pos) -> {
                TileEntity tile2 = world.getTileEntity(pos);
                if (tile2 instanceof EnchanterTileEntity) {
                    return ((EnchanterTileEntity) tile2);
                }
                return null;
            });
            tile.ifPresent(enchanterTileEntity -> enchanterTileEntity.executeEnchantments(playerEntity, packet.getEnchantments()));
        }
    }
}
