package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.StorageTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class StorageBlock extends EnchantmentBaseBlock {

    protected static final VoxelShape SHAPE = makeShape();

    public StorageBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return ModData.storage_tile.create();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundNBT nbt = stack.getTag();
        int count = nbt != null ? nbt.getInt("enchantmentcount") : 0;
        tooltip.add(new TranslationTextComponent("text.enchantment_block.contained_enchantments", count));
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack heldStack) {
        ItemStack stack = new ItemStack(ModData.storage_block, 1);
        if (te instanceof StorageTileEntity) {
            ((StorageTileEntity) te).writeEnchantments(stack.getOrCreateTagElement("BlockEntityTag"));
            stack.getOrCreateTag().putInt("enchantmentcount", ((StorageTileEntity) te).getEnchantmentCount());
        }
        popResource(worldIn, pos, stack);
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        TileEntity tile = world.getBlockEntity(blockPos);
        if (tile instanceof StorageTileEntity && playerEntity instanceof ServerPlayerEntity) {
            EnchantmentMachineMod.DISPATCHER.sendTo(new EnchantmentPacket(((StorageTileEntity) tile).getEnchantments(), true), ((ServerPlayerEntity) playerEntity));
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public static VoxelShape makeShape(){
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    }
}
