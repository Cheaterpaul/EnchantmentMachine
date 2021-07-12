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

import javax.annotation.Nonnull;

public abstract class EnchantmentBaseBlock extends ContainerBlock {

    public EnchantmentBaseBlock(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (worldIn.getBlockState(fromPos).getBlock() instanceof EnchantmentBaseBlock) {
            BlockPos mainPos = fromPos;
            TileEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof StorageTileEntity) {
                te = worldIn.getBlockEntity(fromPos);
                mainPos = pos;
            }
            if (te instanceof EnchantmentBaseTileEntity) {
                ((EnchantmentBaseTileEntity) te).onNeighbourChanged(worldIn, mainPos);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

}
