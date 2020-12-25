package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class EnchantmentMachineBlock extends EnchantmentMachineBaseBlock {
    public EnchantmentMachineBlock(Properties properties) {
        super(properties);
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public Item asItem() {
        return ModData.enchantment_machine_item;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
       return ModData.enchantment_machine_tile.create();
    }
}
