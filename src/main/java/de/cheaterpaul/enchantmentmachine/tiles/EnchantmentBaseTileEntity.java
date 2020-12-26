package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class EnchantmentBaseTileEntity extends LockableTileEntity implements IEnchantmentMachine{

    /**
     * Stores the last known location of an adjacent enchantment storage block.
     * Set when this TE receives its world and position.
     * Updated on neighbour changed (triggered from block)
     */
    @Nullable
    private BlockPos storageBlockPos;


    public EnchantmentBaseTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return playerEntity.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }


    public void onNeighbourChanged(IWorldReader iWorld, BlockPos neighborPos){
            if(this.storageBlockPos==null){
                if(iWorld.getBlockState(neighborPos).getBlock() == ModData.enchantment_block){
                    this.storageBlockPos = neighborPos;
                }
            }
            else if(this.storageBlockPos.equals(neighborPos)){
                if(iWorld.getBlockState(neighborPos).getBlock() != ModData.enchantment_block){
                    this.storageBlockPos = null;
                }
            }
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);

        for(Direction d : Direction.values()) {
            TileEntity te = world.getTileEntity(pos.offset(d));
            if (te instanceof EnchantmentTileEntity) {
                this.storageBlockPos = pos.offset(d);
            }
        }
    }
    

    @Override
    public Optional<EnchantmentTileEntity> getConnectedEnchantmentTE() {
        if(storageBlockPos==null)return Optional.empty();
        TileEntity te = this.world.getTileEntity(storageBlockPos);
        if(te instanceof EnchantmentTileEntity){
            return Optional.of((EnchantmentTileEntity)te);
        }
        return Optional.empty();
    }

    @Override
    public boolean hasConnectedTE() {
        return storageBlockPos!=null;
    }

}
