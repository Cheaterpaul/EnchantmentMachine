package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentBaseTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public abstract class EnchantmentBaseBlock extends ContainerBlock {

    public EnchantmentBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }


    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof EnchantmentBaseTileEntity){
            ((EnchantmentBaseTileEntity) te).onNeighbourChanged(world, neighbor);
        }
    }

}
