package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class EnchanterTileEntity extends EnchantmentBaseTileEntity {
    
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ITextComponent name = Utils.genTranslation("tile", "enchanter.name");

    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);


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

    /**
     * Apply the given list of enchantments to the item in the inventory.
     * It is expected that only available and valid (for the itemstack) enchantments are requested. Otherwise this will log a warning and return false.
     * @param enchantments List of enchantments to apply
     * @param user The player entity that provides the experience points
     * @return If all enchantments and sufficient skill points were available
     */
    public boolean executeEnchantments(PlayerEntity user, List<EnchantmentInstance> enchantments){
        if(!getConnectedEnchantmentTE().isPresent())return false;
        ItemStack stack = inventory.get(0);
        if(stack.isEmpty())return false;
        Map<Enchantment, Integer> enchantmentMap =EnchantmentHelper.getEnchantments(stack);
        EnchantmentTileEntity te=        getConnectedEnchantmentTE().get();

        for(EnchantmentInstance enchInst : enchantments){
            if(!te.hasEnchantment(enchInst)){
                LOGGER.warn("Enchantment {} requested but not available",enchInst);
                return false;
            }
            if(!enchInst.getEnchantment().canApply(stack)){
                LOGGER.warn("Enchantment {} cannot be applied to {}",enchInst.getEnchantment(),stack );
                return false;
            }
            //TODO experience
            for (Map.Entry<Enchantment,Integer> entry : enchantmentMap.entrySet()) {
                Enchantment enchantment = entry.getKey();
                if(enchantment==enchInst.getEnchantment()){ //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                    int newLevel = Math.min(enchantment.getMaxLevel(),enchInst.getLevel()==entry.getValue() ? enchInst.getLevel()+1 : Math.max(enchInst.getLevel(), entry.getValue()));
                    enchInst = new EnchantmentInstance(enchantment,newLevel); //Override enchInst in loop. It will be added to the map later on (and override previous entry for this enchantment)
                    continue;
                }
               else if(enchInst.getEnchantment().isCompatibleWith(enchantment)){
                   LOGGER.warn("Incompatible enchantments {} and {}", enchInst, enchantment);
                   return false;
               }
            }
            enchantmentMap.put(enchInst.getEnchantment(), enchInst.getLevel());
        }
        //Everything good
        EnchantmentHelper.setEnchantments(enchantmentMap, stack);
        enchantments.forEach(te::consumeEnchantment);
        return true;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }
}
