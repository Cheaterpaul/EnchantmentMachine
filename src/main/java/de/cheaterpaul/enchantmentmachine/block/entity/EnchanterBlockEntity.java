package de.cheaterpaul.enchantmentmachine.block.entity;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnchanterBlockEntity extends EnchantmentBaseBlockEntity {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Component name = Utils.genTranslation("tile", "enchanter.name");

    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);


    public EnchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModData.enchanter_tile.get(), pos, state);
    }

    @Nonnull
    @Override
    protected Component getDefaultName() {
        return name;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.inventory = nonNullList;
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    protected AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory) {
        return new EnchanterContainerMenu(i, this, playerInventory, ContainerLevelAccess.create(this.level, this.worldPosition));
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
        if (getConnectedEnchantmentTE().isEmpty()) return false;
        ItemStack stack = inventory.get(0);
        if (stack.isEmpty()) return false;
        stack = stack.getItem().applyEnchantments(stack, Collections.emptyList());
        var type = EnchantmentHelper.getComponentType(stack);
        var mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(stack));
        StorageBlockEntity te = getConnectedEnchantmentTE().get();
        int requiredLevels = 0;
        for (EnchantmentInstanceMod enchInst : enchantments) {
            if (!te.hasEnchantment(enchInst)) {
                LOGGER.warn("Enchantment {} requested but not available", enchInst);
                return false;
            }
            if (!(type == DataComponents.STORED_ENCHANTMENTS || stack.is(enchInst.getEnchantment().definition().supportedItems()))) {
                LOGGER.warn("Enchantment {} cannot be applied to {}", enchInst.getEnchantment(), stack);
                return false;
            }
            int levelCost = Utils.tryApplyEnchantment(enchInst, mutable, true);
            if (levelCost == -1) {
                return false;
            }
            requiredLevels += levelCost;
        }
        if (!user.getAbilities().instabuild) {
            if (user.experienceLevel < requiredLevels) {
                LOGGER.warn("Not enough levels to enchant {} {}", requiredLevels, user.experienceLevel);
                return false;
            }
            user.giveExperienceLevels(-requiredLevels);
        }
        stack.set(type, mutable.toImmutable());
        enchantments.forEach(te::consumeEnchantment);
        inventory.set(0, stack);
        return true;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}
