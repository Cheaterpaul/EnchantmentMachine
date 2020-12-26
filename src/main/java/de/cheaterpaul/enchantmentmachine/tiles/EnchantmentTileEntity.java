package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantmentTileEntity extends TileEntity {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    
    private final Object2IntArrayMap<EnchantmentInstance> enchantmentMaps = new Object2IntArrayMap<>();

    private static final ITextComponent name = Utils.genTranslation("tile", "enchantment.name");

    public EnchantmentTileEntity() {
        super(ModData.enchantment_tile);
    }


    public void addEnchantment(EnchantmentInstance enchInst, int count){
        int c = enchantmentMaps.getOrDefault(enchInst,0);
        enchantmentMaps.put(enchInst, c+count);
    }

    /**
     * Add one enchantment instance
     * @param enchInst
     */
    public void addEnchantment(EnchantmentInstance enchInst) {
        this.addEnchantment(enchInst,1);
    }

    /**
     * Consume 1 enchantment instance
     * @param enchInst The enchantment to consume
     * @return Whether the given enchantment existed and was consumed
     */
    public boolean consumeEnchantment(EnchantmentInstance enchInst){
        int count = enchantmentMaps.getOrDefault(enchInst,0);
        if(count<=0){
            return false;
        }
        else if(count==1){
            enchantmentMaps.removeInt(enchInst);
        }
        else{
            enchantmentMaps.put(enchInst, count-1);
        }
        return true;
    }

    /**
     *
     * @param enchInst
     * @return Whether the given enchantment is present
     */
    public boolean hasEnchantment(EnchantmentInstance enchInst){
        return enchantmentMaps.getOrDefault(enchInst, 0)>0;
    }

    /**
     *
     * @return Unmofifiable map of all enchantment instances and their count (>0)
     */
    public Object2IntMap<EnchantmentInstance> getEnchantments(){
        return Object2IntMaps.unmodifiable(enchantmentMaps);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if(nbt.hasUniqueId("enchantments")){
            enchantmentMaps.clear();
            nbt.getList("enchantment",10).forEach(i->{
                CompoundNBT entry = (CompoundNBT)i;
                try {
                    ResourceLocation eID = new ResourceLocation(entry.getString("id"));
                    int level = entry.getInt("level");
                    int count = entry.getInt("count");
                    Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(eID);
                    if(enchantment==null){
                        LOGGER.info("Cannot find stored enchantment {} in registry", eID);
                    }
                    else{
                        EnchantmentInstance inst = new EnchantmentInstance(enchantment,level);
                        if(enchantmentMaps.containsKey(inst)){
                            LOGGER.warn("Multiple entries of the same enchantment instance in NBT");
                        }
                        enchantmentMaps.put(inst, count);
                    }
                }
                catch (NullPointerException | ResourceLocationException e){
                    LOGGER.error("Illegal enchantment id in NBT {} {}",entry.getString("id"), e);
                }
            });
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);

        ListNBT enchantments = new ListNBT();
        enchantmentMaps.forEach((inst, count) -> {
            CompoundNBT enchantment = new CompoundNBT();
            enchantment.putString("id",inst.getEnchantment().getRegistryName().toString());
            enchantment.putInt("level",inst.getLevel());
            enchantment.putInt("count", count);
        });

        compound.put("enchantments",enchantments);
        return compound;
    }

}
