package de.cheaterpaul.enchantmentmachine.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class EnchanterBlock extends EnchantmentBaseBlock {

    public EnchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return null;
    }
}
