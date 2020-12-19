package de.cheaterpaul.enchantmentmachine.block;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class EnchantmentMachineBlock extends Block {
    public EnchantmentMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModData.enchantment_machine_tile.create();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Item asItem() {
        return ModData.enchantment_machine_item;
    }
}
