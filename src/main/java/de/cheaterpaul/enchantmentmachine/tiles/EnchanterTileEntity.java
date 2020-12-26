package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchanterTileEntity extends EnchantmentBaseTileEntity {

    private static final ITextComponent name = Utils.genTranslation("tile", "enchanter.name");


    public EnchanterTileEntity() {
        super(ModData.enchanter_tile);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return name;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerInventory) {
        return ModData.enchanter_container.create(i, playerInventory);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {

    }

    @Override
    public void clear() {

    }
}
