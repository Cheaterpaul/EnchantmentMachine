package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnchantmentBlock extends EnchantmentBaseBlock {
    public EnchantmentBlock(Properties properties) {
        super(properties);
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
       return ModData.enchantment_tile.create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tile = p_225533_2_.getTileEntity(p_225533_3_);
        if (tile instanceof EnchantmentTileEntity) {
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}
