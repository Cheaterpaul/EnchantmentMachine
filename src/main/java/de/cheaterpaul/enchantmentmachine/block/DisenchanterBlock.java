package de.cheaterpaul.enchantmentmachine.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class DisenchanterBlock extends EnchantmentBaseBlock {

    public DisenchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return null;
    }
}
