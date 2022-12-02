package de.cheaterpaul.enchantmentmachine.block.entity;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DisenchanterBlockEntity extends EnchantmentBaseBlockEntity implements WorldlyContainer, Hopper {

    private static final Component name = Utils.genTranslation("tile", "disenchanter.name");
    private static final int DURATION = 20;
    private final LazyOptional<? extends IItemHandler>[] itemHandler = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
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


    private void setTimer() {
        if (!getItem(0).isEmpty() && (getItem(1).isEmpty() || (resultItem(getItem(0)).sameItem(getItem(1)) && getItem(1).getCount() + 1 <= getItem(1).getMaxStackSize()))) {
            this.timer = DURATION;
        } else {
            this.timer = 0;
        }
    }

    private ItemStack resultItem(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem) {
            return new ItemStack(Items.BOOK);
        }
        ItemStack stack1 = stack.copy();
        stack1.getOrCreateTag().remove("StoredEnchantments");
        EnchantmentHelper.setEnchantments(getRemainingEnchantments(stack), stack1);
        return stack1;
    }

    private Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
        map.putAll(EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(stack)));
        return map;
    }

    private Map<Enchantment, Integer> getRemainingEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        boolean allowCurses = ModConfig.SERVER.allowDisenchantingCurses.get();
        Set<Enchantment> disallowedEnchantments = ModConfig.SERVER.getDisallowedDisenchantingEnchantments();
        map.entrySet().removeIf(entry -> !disallowedEnchantments.contains(entry.getKey()) && (allowCurses || !entry.getKey().isCurse()));
        return map;
    }

    private Map<Enchantment, Integer> getExtractedEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
        boolean allowCurses = ModConfig.SERVER.allowDisenchantingCurses.get();
        Set<Enchantment> disallowedEnchantments = ModConfig.SERVER.getDisallowedDisenchantingEnchantments();
        map.entrySet().removeIf(entry -> disallowedEnchantments.contains(entry.getKey()) || (!allowCurses && entry.getKey().isCurse()));
        return map;
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
            if (!ModConfig.SERVER.allowDisenchantingItems.get()) {
                if (EnchantedBookItem.getEnchantments(stack).isEmpty()) return false;
            }
            return !EnchantmentHelper.getEnchantments(stack).isEmpty();
        } else {
            return false;
        }
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
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.inventory.clear();
        ContainerHelper.loadAllItems(nbt, this.inventory);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.inventory);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction facing) {
        if (!this.remove && facing != null && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return itemHandler[0].cast();
            else if (facing == Direction.DOWN)
                return itemHandler[1].cast();
            else
                return itemHandler[2].cast();
        }
        return super.getCapability(cap, facing);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> opt : itemHandler) {
            opt.invalidate();
        }
    }

    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5;
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, DisenchanterBlockEntity entity) {
        if (entity.timer > 0 && entity.hasConnectedTE()) {
            if (--entity.timer == 0) {
                entity.getConnectedEnchantmentTE().ifPresent(te -> {
                    ItemStack stack = entity.inventory.get(0);
                    if (entity.canDisenchant(stack)) {
                        Map<Enchantment, Integer> map = entity.getExtractedEnchantments(stack);
                        map.forEach((key, value) -> {
                            te.addEnchantment(new EnchantmentInstanceMod(key, value));
                        });
                        stack = entity.resultItem(stack);
                        ItemStack slot = entity.getItem(1);
                        if (!slot.isEmpty() && slot.sameItem(stack)) {
                            stack.shrink(-slot.getCount());
                        }
                        entity.setItem(1, stack);
                        entity.setItem(0, ItemStack.EMPTY);
                    } else {
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
        if (ModConfig.SERVER.allowDisenchantingItems.get()) {
            return getExtractedEnchantments(stack).size() > 0;
        }
        return !EnchantedBookItem.getEnchantments(stack).isEmpty();
    }
}
