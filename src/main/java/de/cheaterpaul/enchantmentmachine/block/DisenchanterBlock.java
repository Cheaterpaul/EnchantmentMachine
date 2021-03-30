package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.tiles.DisenchanterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DisenchanterBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();


    public DisenchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return ModData.disenchanter_tile.create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tile = p_225533_2_.getTileEntity(p_225533_3_);
        if (tile instanceof DisenchanterTileEntity) {
            p_225533_4_.openContainer(((DisenchanterTileEntity) tile));
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape a = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
        VoxelShape a1 = Block.makeCuboidShape(5, 11, 5, 11, 12, 11);
        VoxelShape a2 = Block.makeCuboidShape(6, 10, 6, 10, 11, 10);
        VoxelShape a3 = Block.makeCuboidShape(7, 9, 7, 9, 10, 9);
        a1 = VoxelShapes.or(a1, a2, a3);


        VoxelShape b = Block.makeCuboidShape(3, 12, 3, 13, 13, 13);
        VoxelShape b1 = Block.makeCuboidShape(4, 12, 4, 12, 13, 12);

        VoxelShape c = Block.makeCuboidShape(2, 13, 2, 14, 14, 14);
        VoxelShape c1 = Block.makeCuboidShape(3, 13, 3, 13, 14, 13);

        VoxelShape d = Block.makeCuboidShape(1, 14, 1, 15, 15, 15);
        VoxelShape d1 = Block.makeCuboidShape(2, 14, 2, 14, 15, 14);

        VoxelShape e = Block.makeCuboidShape(0, 15, 0, 16, 16, 16);
        VoxelShape e1 = Block.makeCuboidShape(1, 15, 1, 15, 16, 15);


        a = VoxelShapes.combine(a, a1, (first, second) -> first & !second || !first & second);
        b = VoxelShapes.combine(b, b1, (first, second) -> first & !second || !first & second);
        c = VoxelShapes.combine(c, c1, (first, second) -> first & !second || !first & second);
        d = VoxelShapes.combine(d, d1, (first, second) -> first & !second || !first & second);
        e = VoxelShapes.combine(e, e1, (first, second) -> first & !second || !first & second);

        return VoxelShapes.or(a, b, c, d, e);
    }
}
