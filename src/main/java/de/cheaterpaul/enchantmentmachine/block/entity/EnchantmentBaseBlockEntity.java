package de.cheaterpaul.enchantmentmachine.block.entity;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class EnchantmentBaseBlockEntity extends BaseContainerBlockEntity implements IEnchantmentMachine {

    /**
     * Stores the last known location of an adjacent enchantment storage block.
     * Set when this TE receives its world and position.
     * Updated on neighbour changed (triggered from block)
     */
    @Nullable
    private BlockPos storageBlockPos;


    public EnchantmentBaseBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    @Override
    public boolean stillValid(@Nonnull Player playerEntity) {
        //noinspection ConstantConditions
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return playerEntity.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public Optional<StorageBlockEntity> getConnectedEnchantmentTE() {
        if (storageBlockPos == null) return Optional.empty();
        //noinspection ConstantConditions
        BlockEntity te = this.level.getBlockEntity(storageBlockPos);
        if (te instanceof StorageBlockEntity) {
            return Optional.of((StorageBlockEntity) te);
        }
        return Optional.empty();
    }

    public void onNeighbourChanged(LevelReader iWorld, BlockPos neighborPos) {
        if (this.storageBlockPos == null) {
            if (iWorld.getBlockState(neighborPos).getBlock() == ModData.storage_block) {
                this.storageBlockPos = neighborPos;
            }
        } else if (this.storageBlockPos.equals(neighborPos)) {
            if (iWorld.getBlockState(neighborPos).getBlock() != ModData.storage_block) {
                this.storageBlockPos = null;
            }
        }
    }

    @Override
    public boolean hasConnectedTE() {
        return storageBlockPos != null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.storageBlockPos != null) {
            compound.putIntArray("storageblock", new int[]{this.storageBlockPos.getX(), this.storageBlockPos.getY(), this.storageBlockPos.getZ()});
        }
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("storageblock")) {
            int[] pos = nbt.getIntArray("storageblock");
            this.storageBlockPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }
}
