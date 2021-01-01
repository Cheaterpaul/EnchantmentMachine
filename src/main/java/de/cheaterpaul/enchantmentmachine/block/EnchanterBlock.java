package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Optional;

public class EnchanterBlock extends EnchantmentBaseBlock {

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
            if (!world.isRemote()) {
                Optional<EnchantmentTileEntity> s = ((EnchanterTileEntity) tile).getConnectedEnchantmentTE();
                s.ifPresent(enchantmentTileEntity -> EnchantmentMachineMod.DISPATCHER.sendTo(new EnchantmentPacket(blockPos, enchantmentTileEntity.getEnchantments()), ((ServerPlayerEntity) playerEntity)));
            }
            return ActionResultType.CONSUME;
        }
        return ActionResultType.SUCCESS;
    }
}
