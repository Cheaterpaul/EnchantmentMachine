package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class EnchantmentBaseTileEntity extends LockableTileEntity {
    public EnchantmentBaseTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }
}
