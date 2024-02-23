package de.cheaterpaul.enchantmentmachine.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.entity.StorageBlockEntity;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class StorageBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();
    private static final MapCodec<StorageBlock> CODEC = simpleCodec(StorageBlock::new);

    public StorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return ModData.storage_tile.get().create(pos, state);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag nbt = stack.getTag();
        int count = nbt != null ? nbt.getInt("enchantmentcount") : 0;
        tooltip.add(Component.translatable("text.enchantment_block.contained_enchantments", count));
    }

    @Override
    public void playerDestroy(@Nonnull Level worldIn, @Nonnull Player player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable BlockEntity te, @Nonnull ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModData.storage_block.get(), 1);
        if (te instanceof StorageBlockEntity) {
            ((StorageBlockEntity) te).writeEnchantments(stack.getOrCreateTagElement("BlockEntityTag"));
            stack.getOrCreateTag().putInt("enchantmentcount", ((StorageBlockEntity) te).getEnchantmentCount());
        }
        popResource(worldIn, pos, stack);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState blockState, Level world, @Nonnull BlockPos blockPos, @Nonnull Player playerEntity, @Nonnull InteractionHand p_225533_5_, @Nonnull BlockHitResult p_225533_6_) {
        BlockEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof StorageBlockEntity && playerEntity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new EnchantmentPacket(((StorageBlockEntity) tile).getEnchantments(), true));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    public static VoxelShape makeShape(){
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return createStorageTicker(level, type, ModData.storage_tile.get());
    }

    protected static <T extends BlockEntity> BlockEntityTicker<T> createStorageTicker(Level level, BlockEntityType<T> type, @SuppressWarnings("SameParameterValue") BlockEntityType<? extends StorageBlockEntity> tile) {
        return level.isClientSide ? createTickerHelper(type, tile, StorageBlockEntity::clientTick) : null;
    }
}
