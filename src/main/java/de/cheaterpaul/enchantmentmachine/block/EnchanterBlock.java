package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.StorageBlockEntity;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EnchanterBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();


    public EnchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModData.enchanter_tile.create(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState blockState, Level world, @Nonnull BlockPos blockPos, @Nonnull Player playerEntity, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rayTraceResult) {
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof EnchanterBlockEntity) {
            playerEntity.openMenu(((EnchanterBlockEntity) tile));
            if (!world.isClientSide() && playerEntity instanceof ServerPlayer) {
                Optional<StorageBlockEntity> s = ((EnchanterBlockEntity) tile).getConnectedEnchantmentTE();
                s.ifPresent(enchantmentTileEntity -> EnchantmentMachineMod.DISPATCHER.sendTo(new EnchantmentPacket(enchantmentTileEntity.getEnchantments(), false), ((ServerPlayer) playerEntity)));
            }
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

    public static VoxelShape makeShape() {
        VoxelShape a = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

        VoxelShape b = Block.box(2, 12, 2, 7, 15, 5);

        VoxelShape c = Block.box(9, 12, 11, 14, 15, 14);

        VoxelShape d = Block.box(4, 13, 5, 5, 14, 11);

        VoxelShape e = Block.box(11, 13, 5, 12, 14, 11);

        return Shapes.or(a,b,c,d,e);
    }
}
