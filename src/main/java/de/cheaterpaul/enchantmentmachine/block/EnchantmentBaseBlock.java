package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentBaseTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.StorageTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EnchantmentBaseBlock extends ContainerBlock {

    public EnchantmentBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.getBlockState(fromPos).getBlock() instanceof EnchantmentBaseBlock) {
            BlockPos mainPos = fromPos;
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof StorageTileEntity) {
                te = worldIn.getTileEntity(fromPos);
                mainPos = pos;
            }
            if (te instanceof EnchantmentBaseTileEntity) {
                ((EnchantmentBaseTileEntity) te).onNeighbourChanged(worldIn, mainPos);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

}
