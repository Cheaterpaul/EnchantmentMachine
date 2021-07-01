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
    public TileEntity newBlockEntity(IBlockReader iBlockReader) {
        return ModData.disenchanter_tile.create();
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tile = p_225533_2_.getBlockEntity(p_225533_3_);
        if (tile instanceof DisenchanterTileEntity) {
            p_225533_4_.openMenu(((DisenchanterTileEntity) tile));
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape a = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
        VoxelShape a1 = Block.box(5, 11, 5, 11, 12, 11);
        VoxelShape a2 = Block.box(6, 10, 6, 10, 11, 10);
        VoxelShape a3 = Block.box(7, 9, 7, 9, 10, 9);
        a1 = VoxelShapes.or(a1, a2, a3);


        VoxelShape b = Block.box(3, 12, 3, 13, 13, 13);
        VoxelShape b1 = Block.box(4, 12, 4, 12, 13, 12);

        VoxelShape c = Block.box(2, 13, 2, 14, 14, 14);
        VoxelShape c1 = Block.box(3, 13, 3, 13, 14, 13);

        VoxelShape d = Block.box(1, 14, 1, 15, 15, 15);
        VoxelShape d1 = Block.box(2, 14, 2, 14, 15, 14);

        VoxelShape e = Block.box(0, 15, 0, 16, 16, 16);
        VoxelShape e1 = Block.box(1, 15, 1, 15, 16, 15);


        a = VoxelShapes.joinUnoptimized(a, a1, (first, second) -> first & !second || !first & second);
        b = VoxelShapes.joinUnoptimized(b, b1, (first, second) -> first & !second || !first & second);
        c = VoxelShapes.joinUnoptimized(c, c1, (first, second) -> first & !second || !first & second);
        d = VoxelShapes.joinUnoptimized(d, d1, (first, second) -> first & !second || !first & second);
        e = VoxelShapes.joinUnoptimized(e, e1, (first, second) -> first & !second || !first & second);

        return VoxelShapes.or(a, b, c, d, e);
    }
}
