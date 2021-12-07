package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.block.entity.DisenchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DisenchanterBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();


    public DisenchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModData.disenchanter_tile.create(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState p_225533_1_, Level p_225533_2_, @Nonnull BlockPos p_225533_3_, @Nonnull Player p_225533_4_, @Nonnull InteractionHand p_225533_5_, @Nonnull BlockHitResult p_225533_6_) {
        BlockEntity tile = p_225533_2_.getBlockEntity(p_225533_3_);
        if (tile instanceof DisenchanterBlockEntity) {
            p_225533_4_.openMenu(((DisenchanterBlockEntity) tile));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable BlockGetter p_49817_, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, p_49817_, tooltips, flag);
        tooltips.add(new TranslatableComponent("text.enchantmentmachine.next_to_storage_block", ModData.storage_block.getName()).withStyle(ChatFormatting.GRAY));

    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return createStorageTicker(level, type, ModData.disenchanter_tile);
    }

    protected static <T extends BlockEntity> BlockEntityTicker<T> createStorageTicker(Level level, BlockEntityType<T> type, @SuppressWarnings("SameParameterValue") BlockEntityType<? extends DisenchanterBlockEntity> tile) {
        return level.isClientSide ? null : createTickerHelper(type, tile, DisenchanterBlockEntity::serverTick);
    }

    public static VoxelShape makeShape() {
        VoxelShape a = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
        VoxelShape a1 = Block.box(5, 11, 5, 11, 12, 11);
        VoxelShape a2 = Block.box(6, 10, 6, 10, 11, 10);
        VoxelShape a3 = Block.box(7, 9, 7, 9, 10, 9);
        a1 = Shapes.or(a1, a2, a3);


        VoxelShape b = Block.box(3, 12, 3, 13, 13, 13);
        VoxelShape b1 = Block.box(4, 12, 4, 12, 13, 12);

        VoxelShape c = Block.box(2, 13, 2, 14, 14, 14);
        VoxelShape c1 = Block.box(3, 13, 3, 13, 14, 13);

        VoxelShape d = Block.box(1, 14, 1, 15, 15, 15);
        VoxelShape d1 = Block.box(2, 14, 2, 14, 15, 14);

        VoxelShape e = Block.box(0, 15, 0, 16, 16, 16);
        VoxelShape e1 = Block.box(1, 15, 1, 15, 16, 15);


        a = Shapes.joinUnoptimized(a, a1, (first, second) -> first & !second || !first & second);
        b = Shapes.joinUnoptimized(b, b1, (first, second) -> first & !second || !first & second);
        c = Shapes.joinUnoptimized(c, c1, (first, second) -> first & !second || !first & second);
        d = Shapes.joinUnoptimized(d, d1, (first, second) -> first & !second || !first & second);
        e = Shapes.joinUnoptimized(e, e1, (first, second) -> first & !second || !first & second);

        return Shapes.or(a, b, c, d, e);
    }
}
