package de.cheaterpaul.enchantmentmachine.core;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.DisenchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.EnchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.EnchantmentBlock;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainer;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.tiles.DisenchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModData {

    public static final EnchanterBlock enchanter_block;
    public static final DisenchanterBlock disenchanter_block;
    public static final EnchantmentBlock enchantment_block;
    public static final TileEntityType<EnchanterTileEntity> enchanter_tile;
    public static final TileEntityType<DisenchanterTileEntity> disenchanter_tile;
    public static final TileEntityType<EnchantmentTileEntity> enchantment_tile;
    public static final ContainerType<EnchanterContainer> enchanter_container;
    public static final ContainerType<DisenchanterContainer> disenchanter_container;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(enchanter_block);
        event.getRegistry().register(disenchanter_block);
        event.getRegistry().register(enchantment_block);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(enchanter_block, new Item.Properties().group(EnchantmentMachineMod.CREATIVE_TAB)).setRegistryName(REFERENCE.MODID,"enchanter_block"));
        event.getRegistry().register(new BlockItem(disenchanter_block, new Item.Properties().group(EnchantmentMachineMod.CREATIVE_TAB)).setRegistryName(REFERENCE.MODID,"disenchanter_block"));
        event.getRegistry().register(new BlockItem(enchantment_block, new Item.Properties().group(EnchantmentMachineMod.CREATIVE_TAB).maxStackSize(1).rarity(Rarity.RARE)).setRegistryName(REFERENCE.MODID,"enchantment_block"));
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(enchanter_tile);
        event.getRegistry().register(disenchanter_tile);
        event.getRegistry().register(enchantment_tile);
    }

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(enchanter_container);
        event.getRegistry().register(disenchanter_container);
    }

    static {
        (enchanter_block = new EnchanterBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 1200.0F))).setRegistryName(REFERENCE.MODID, "enchanter_block");
        (disenchanter_block = new DisenchanterBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 1200.0F))).setRegistryName(REFERENCE.MODID, "disenchanter_block");
        (enchantment_block = new EnchantmentBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F, 1200.0F).notSolid())).setRegistryName(REFERENCE.MODID, "enchantment_block");
        (enchanter_tile = TileEntityType.Builder.create(EnchanterTileEntity::new, enchanter_block).build(null)).setRegistryName(REFERENCE.MODID, "enchanter_tile");
        (disenchanter_tile = TileEntityType.Builder.create(DisenchanterTileEntity::new, disenchanter_block).build(null)).setRegistryName(REFERENCE.MODID, "disenchanter_tile");
        (enchantment_tile = TileEntityType.Builder.create(EnchantmentTileEntity::new, enchantment_block).build(null)).setRegistryName(REFERENCE.MODID, "enchantment_tile");
        (enchanter_container = new ContainerType<>(EnchanterContainer::new)).setRegistryName(REFERENCE.MODID, "enchanter_container");
        (disenchanter_container = new ContainerType<>(DisenchanterContainer::new)).setRegistryName(REFERENCE.MODID, "disenchanter_container");
    }
}
