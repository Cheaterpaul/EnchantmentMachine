package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.block.entity.EnchantmentBaseBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.StorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public abstract class EnchantmentBaseBlock extends BaseEntityBlock {

    public EnchantmentBaseBlock(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (worldIn.getBlockState(fromPos).getBlock() instanceof EnchantmentBaseBlock) {
            BlockPos mainPos = fromPos;
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof StorageBlockEntity) {
                te = worldIn.getBlockEntity(fromPos);
                mainPos = pos;
            }
            if (te instanceof EnchantmentBaseBlockEntity) {
                ((EnchantmentBaseBlockEntity) te).onNeighbourChanged(worldIn, mainPos);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

}
