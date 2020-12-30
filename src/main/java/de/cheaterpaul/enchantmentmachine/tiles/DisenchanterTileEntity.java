package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class DisenchanterTileEntity extends EnchantmentBaseTileEntity implements ITickableTileEntity, ISidedInventory {

    private static final ITextComponent name = Utils.genTranslation("tile", "disenchanter.name");

    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private final static int DURATION = 20;
    /**
     * Countdown to disenchantment
     * >0 if waiting
     */
    private int timer;

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
        if(getStackInSlot(1).isEmpty()&&!getStackInSlot(0).isEmpty()){
            this.timer = DURATION;
        }
        else{
            this.timer = 0;
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void tick() {
        if(this.timer>0&&this.hasConnectedTE()){
            if(--this.timer==0){
                getConnectedEnchantmentTE().ifPresent(te-> {
                    ItemStack stack = this.inventory.get(0);
                    Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
                    map.entrySet().forEach(e->{
                        EnchantmentInstance inst = new EnchantmentInstance(e.getKey(),e.getValue());
                        te.addEnchantment(inst);
                    });
                    EnchantmentHelper.setEnchantments(Collections.emptyMap(),stack);
                    this.inventory.set(1,stack);
                    this.inventory.set(0,ItemStack.EMPTY);
                });
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        switch (side){
            case DOWN:
                return new int[]{1};
            default:
                return new int[]{0};
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if(index==0){
            return !EnchantmentHelper.getEnchantments(stack).isEmpty();
        }
        else{
            return EnchantmentHelper.getEnchantments(stack).isEmpty();
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return index==0&&isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index==1;
    }
}
