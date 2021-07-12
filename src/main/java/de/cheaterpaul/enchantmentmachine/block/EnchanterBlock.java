package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.StorageTileEntity;
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

import javax.annotation.Nonnull;
import java.util.Optional;

public class EnchanterBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();


    public EnchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity newBlockEntity(@Nonnull IBlockReader iBlockReader) {
        return ModData.enchanter_tile.create();
    }

    @Nonnull
    @Override
    public ActionResultType use(@Nonnull BlockState blockState, World world, @Nonnull BlockPos blockPos, @Nonnull PlayerEntity playerEntity, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rayTraceResult) {
        TileEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof EnchanterTileEntity) {
            playerEntity.openMenu(((EnchanterTileEntity) tile));
            if (!world.isClientSide() && playerEntity instanceof ServerPlayerEntity) {
                Optional<StorageTileEntity> s = ((EnchanterTileEntity) tile).getConnectedEnchantmentTE();
                s.ifPresent(enchantmentTileEntity -> EnchantmentMachineMod.DISPATCHER.sendTo(new EnchantmentPacket(enchantmentTileEntity.getEnchantments(), false), ((ServerPlayerEntity) playerEntity)));
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape a = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

        VoxelShape b = Block.box(2, 12, 2, 7, 15, 5);

        VoxelShape c = Block.box(14, 12, 14, 9, 15, 11);

        VoxelShape d = Block.box(4, 13, 5, 5, 14, 11);

        VoxelShape e = Block.box(12, 13, 11, 11, 14, 5);

        return VoxelShapes.or(a,b,c,d,e);
    }
}
