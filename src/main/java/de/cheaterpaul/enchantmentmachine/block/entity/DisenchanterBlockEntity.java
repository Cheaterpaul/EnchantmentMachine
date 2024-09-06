package de.cheaterpaul.enchantmentmachine.block.entity;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DisenchanterBlockEntity extends EnchantmentBaseBlockEntity implements WorldlyContainer, Hopper {

    private static final Component name = Utils.genTranslation("tile", "disenchanter.name");
    private static final int DURATION = 20;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    /**
     * Countdown to disenchantment
     * >0 if waiting
     */
    private int timer;
    private int transferCooldown;

    public DisenchanterBlockEntity(BlockPos pos, BlockState state) {
        super(ModData.disenchanter_tile.get(), pos, state);
    }

    @Nonnull
    @Override
    protected Component getDefaultName() {
        return name;
    }

    @Nonnull
    @Override
    protected AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory) {
        return new DisenchanterContainerMenu(i, this, playerInventory);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int i, int i1) {
        ItemStack result = ContainerHelper.removeItem(this.inventory, i, i1);
        this.setTimer();
        return result;
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int i) {
        ItemStack stack = ContainerHelper.takeItem(this.inventory, i);
        this.setTimer();
        return stack;
    }

    @Override
    public void setItem(int i, @Nonnull ItemStack itemStack) {
        this.inventory.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        setTimer();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> nonNullList) {
        this.inventory = nonNullList;
    }

    private void setTimer() {
        if (!getItem(0).isEmpty() && (getItem(1).isEmpty() || ItemStack.isSameItem(resultItem(getItem(0)), getItem(1)) && getItem(1).getCount() + 1 <= getItem(1).getMaxStackSize())) {
            this.timer = DURATION;
        } else {
            this.timer = 0;
        }
    }

    private ItemStack resultItem(ItemStack stack) {
        var remainingEnchantments = getRemainingEnchantments(stack);
        if (stack.getItem() instanceof EnchantedBookItem && remainingEnchantments.keySet().isEmpty()) {
            return new ItemStack(Items.BOOK);
        }
        stack = stack.copy();
        EnchantmentHelper.setEnchantments(stack, remainingEnchantments.toImmutable());
        return stack;
    }

    private ItemEnchantments.Mutable getRemainingEnchantments(ItemStack stack) {
        ItemEnchantments allEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        boolean allowCurses = ModConfig.SERVER.allowDisenchantingCurses.get();
        Set<ResourceLocation> disallowedEnchantments = ModConfig.SERVER.getDisallowedDisenchantingEnchantments();
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(allEnchantments);
        mutable.removeIf(holder -> !disallowedEnchantments.contains(holder.getKey().location()) && (allowCurses || !holder.is(EnchantmentTags.CURSE)));
        return mutable;
    }

    private ItemEnchantments.Mutable getExtractedEnchantments(ItemStack stack) {
        ItemEnchantments allEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(allEnchantments);
        boolean allowCurses = ModConfig.SERVER.allowDisenchantingCurses.get();
        Set<ResourceLocation> disallowedEnchantments = ModConfig.SERVER.getDisallowedDisenchantingEnchantments();
        mutable.removeIf(entry -> disallowedEnchantments.contains(entry.getKey().location()) || (!allowCurses && entry.is(EnchantmentTags.CURSE)));
        return mutable;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public double getLevelX() {
        return this.worldPosition.getX() + 0.5;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{1};
        }
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        if (index == 0) {
            DataComponentType<ItemEnchantments> componentType = EnchantmentHelper.getComponentType(stack);
            if (ModConfig.SERVER.allowDisenchantingItems.get() || componentType == DataComponents.STORED_ENCHANTMENTS) {
                ItemEnchantments allEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
                return ModConfig.SERVER.allowDisenchantingCurses.get() || allEnchantments.entrySet().stream().noneMatch(s -> s.getKey().is(EnchantmentTags.CURSE));
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack itemStackIn, @Nullable Direction direction) {
        return index == 0 && canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack stack, @Nonnull Direction direction) {
        return index == 1;
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.inventory.clear();
        ContainerHelper.loadAllItems(nbt, this.inventory, provider);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        ContainerHelper.saveAllItems(compound, this.inventory, provider);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        this.loadAdditional(pkt.getTag(), provider);
    }

    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5;
    }

    @Override
    public boolean isGridAligned() {
        return false;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, DisenchanterBlockEntity entity) {
        if (entity.timer > 0 && entity.hasConnectedTE()) {
            if (--entity.timer == 0) {
                entity.getConnectedEnchantmentTE().ifPresent(te -> {
                    ItemStack stack = entity.inventory.get(0);
                    if (entity.canDisenchant(stack)) {
                        ItemEnchantments.Mutable map = entity.getExtractedEnchantments(stack);
                        map.keySet().forEach(e -> {
                            te.addEnchantment(new EnchantmentInstanceMod(e, map.getLevel(e)));
                        });
                        stack = entity.resultItem(stack);
                        ItemStack slot = entity.getItem(1);
                        if (!slot.isEmpty() && slot.is(stack.getItem())) {
                            stack.shrink(-slot.getCount());
                        }
                        entity.setItem(1, stack);
                        entity.setItem(0, ItemStack.EMPTY);
                    }
                });
            }
        }
        if (entity.level != null) {
            --entity.transferCooldown;
            if (entity.transferCooldown <= 0) {
                entity.transferCooldown = 0;
                if (HopperBlockEntity.suckInItems(level, entity)) {
                    entity.transferCooldown = 0;
                }
            }
        }
    }

    private boolean canDisenchant(ItemStack stack) {
        DataComponentType<ItemEnchantments> componentType = EnchantmentHelper.getComponentType(stack);
        if (componentType == DataComponents.ENCHANTMENTS) {
            if (ModConfig.SERVER.allowDisenchantingItems.get()) {
                ItemEnchantments extractedEnchantments = getExtractedEnchantments(stack).toImmutable();
                if (!extractedEnchantments.isEmpty()) {
                    return this.inventory.get(1).isEmpty() || ModConfig.SERVER.allowDisenchantingCurses.get() || extractedEnchantments.keySet().stream().noneMatch(s -> s.is(EnchantmentTags.CURSE));
                }
            }
            return false;
        } else {
            return !stack.getOrDefault(componentType, ItemEnchantments.EMPTY).isEmpty();
        }
    }
}
