package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class DisenchanterTileEntity extends EnchantmentBaseTileEntity {

    private static final ITextComponent name = Utils.genTranslation("tile", "disenchanter.name");

    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public DisenchanterTileEntity() {
        super(ModData.disenchanter_tile);
    }

    @Override
    protected ITextComponent getDefaultName() {
        return name;
    }

    @Override
    protected Container createMenu(int i, PlayerInventory playerInventory) {
        return new DisenchanterContainer(i, this, playerInventory);
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.inventory) {
            if (!item.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.inventory.get(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        return ItemStackHelper.getAndSplit(this.inventory, i, i1);
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return ItemStackHelper.getAndRemove(this.inventory, i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        this.inventory.set(i, itemStack);
        if (itemStack.getCount() > this.getInventoryStackLimit()) {
            itemStack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
