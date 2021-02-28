package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

import java.util.Optional;

public class EnchanterBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();


    public EnchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return ModData.enchanter_tile.create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult rayTraceResult) {
        TileEntity tile = world.getTileEntity(blockPos);
        if (tile instanceof EnchanterTileEntity) {
            playerEntity.openContainer(((EnchanterTileEntity) tile));
            if (!world.isRemote() && playerEntity instanceof ServerPlayerEntity) {
                Optional<EnchantmentTileEntity> s = ((EnchanterTileEntity) tile).getConnectedEnchantmentTE();
                s.ifPresent(enchantmentTileEntity -> EnchantmentMachineMod.DISPATCHER.sendTo(new EnchantmentPacket(enchantmentTileEntity.getEnchantments(), false), ((ServerPlayerEntity) playerEntity)));
            }
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

        VoxelShape b = Block.makeCuboidShape(2,12, 2, 7,15,5);

        VoxelShape c = Block.makeCuboidShape(14,12,14,9,15,11);

        VoxelShape d = Block.makeCuboidShape(4,13,5,5,14,11);

        VoxelShape e = Block.makeCuboidShape(12,13,11,11,14,5);

        return VoxelShapes.or(a,b,c,d,e);
    }
}
