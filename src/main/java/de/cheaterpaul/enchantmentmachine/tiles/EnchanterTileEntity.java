package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class EnchanterTileEntity extends EnchantmentBaseTileEntity {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Component name = Utils.genTranslation("tile", "enchanter.name");

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);


    public EnchanterTileEntity(BlockPos pos, BlockState state) {
        super(ModData.enchanter_tile, pos, state);
    }

    @Nonnull
    @Override
    protected Component getDefaultName() {
        return name;
    }

    @Nonnull
    @Override
    protected AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory) {
        return new EnchanterContainer(i, this, playerInventory, ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.inventory) {
            if (!item.isEmpty()) return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int i, int i1) {
        return ContainerHelper.removeItem(this.inventory, i, i1);
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void setItem(int i, @Nonnull ItemStack itemStack) {
        this.inventory.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    /**
     * Apply the given list of enchantments to the item in the inventory.
     * It is expected that only available and valid (for the itemstack) enchantments are requested. Otherwise this will log a warning and return false.
     *
     * @param enchantments List of enchantments to apply
     * @param user         The player entity that provides the experience points
     * @return If all enchantments and sufficient skill points were available
     */
    public boolean executeEnchantments(Player user, List<EnchantmentInstanceMod> enchantments) {
        if (!getConnectedEnchantmentTE().isPresent()) return false;
        ItemStack stack = inventory.get(0);
        if (stack.isEmpty()) return false;
        Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.getEnchantments(stack);
        StorageTileEntity te = getConnectedEnchantmentTE().get();

        boolean book = stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK;
        if (book) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        int requiredLevels = 0;
        for (EnchantmentInstanceMod enchInst : enchantments) {
            if (!te.hasEnchantment(enchInst)) {
                LOGGER.warn("Enchantment {} requested but not available", enchInst);
                return false;
            }
            if (!(enchInst.getEnchantment().canEnchant(stack) || book)) {
                LOGGER.warn("Enchantment {} cannot be applied to {}", enchInst.getEnchantment(), stack);
                return false;
            }
            Pair<EnchantmentInstanceMod, Integer> result = Utils.tryApplyEnchantment(enchInst, enchantmentMap, true);
            if (result == null) {
                return false;
            }
            requiredLevels += result.getRight();
            enchantmentMap.put(result.getLeft().getEnchantment(), result.getLeft().getLevel()); //Override previous entry for this enchantment
        }
        if (!user.getAbilities().instabuild) {
            if (user.experienceLevel < requiredLevels) {
                LOGGER.warn("Not enough levels to enchant {} {}", requiredLevels, user.experienceLevel);
                return false;
            }
            user.giveExperienceLevels(-requiredLevels);
        }
        //Everything good
        if (book) {
            ItemStack finalStack = stack;
            enchantmentMap.forEach((ench, lvl) -> EnchantedBookItem.addEnchantment(finalStack, new EnchantmentInstance(ench, lvl)));
            this.inventory.set(0, stack);
        } else {
            EnchantmentHelper.setEnchantments(enchantmentMap, stack);
        }
        enchantments.forEach(te::consumeEnchantment);
        return true;
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }
}
