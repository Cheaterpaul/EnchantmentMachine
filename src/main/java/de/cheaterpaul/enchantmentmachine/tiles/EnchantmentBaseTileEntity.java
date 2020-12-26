package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class EnchantmentBaseTileEntity extends LockableTileEntity {
    public EnchantmentBaseTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return playerEntity.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
