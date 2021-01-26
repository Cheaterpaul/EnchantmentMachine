package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentBaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
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
        if (worldIn.getBlockState(fromPos).getBlock() == ModData.enchantment_block) {
            TileEntity te = worldIn.getTileEntity(pos);
            if(te instanceof EnchantmentBaseTileEntity){
                ((EnchantmentBaseTileEntity) te).onNeighbourChanged(worldIn, fromPos);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

}
