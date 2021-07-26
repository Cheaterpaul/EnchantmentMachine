package de.cheaterpaul.enchantmentmachine.core;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.DisenchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.EnchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.StorageBlock;
import de.cheaterpaul.enchantmentmachine.block.entity.DisenchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.StorageBlockEntity;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModData {

    public static final EnchanterBlock enchanter_block;
    public static final DisenchanterBlock disenchanter_block;
    public static final StorageBlock storage_block;
    public static final BlockEntityType<EnchanterBlockEntity> enchanter_tile;
    public static final BlockEntityType<DisenchanterBlockEntity> disenchanter_tile;
    public static final BlockEntityType<StorageBlockEntity> storage_tile;
    public static final MenuType<EnchanterContainerMenu> enchanter_container;
    public static final MenuType<DisenchanterContainerMenu> disenchanter_container;

    static {
        (enchanter_block = new EnchanterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F))).setRegistryName(REFERENCE.MODID, "enchanter_block");
        (disenchanter_block = new DisenchanterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F))).setRegistryName(REFERENCE.MODID, "disenchanter_block");
        (storage_block = new StorageBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).noOcclusion())).setRegistryName(REFERENCE.MODID, "enchantment_block");
        //noinspection ConstantConditions
        (enchanter_tile = BlockEntityType.Builder.of(EnchanterBlockEntity::new, enchanter_block).build(null)).setRegistryName(REFERENCE.MODID, "enchanter_tile");
        //noinspection ConstantConditions
        (disenchanter_tile = BlockEntityType.Builder.of(DisenchanterBlockEntity::new, disenchanter_block).build(null)).setRegistryName(REFERENCE.MODID, "disenchanter_tile");
        //noinspection ConstantConditions
        (storage_tile = BlockEntityType.Builder.of(StorageBlockEntity::new, storage_block).build(null)).setRegistryName(REFERENCE.MODID, "enchantment_tile");
        (enchanter_container = new MenuType<>(EnchanterContainerMenu::new)).setRegistryName(REFERENCE.MODID, "enchanter_container");
        (disenchanter_container = new MenuType<>(DisenchanterContainerMenu::new)).setRegistryName(REFERENCE.MODID, "disenchanter_container");
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(enchanter_block);
        event.getRegistry().register(disenchanter_block);
        event.getRegistry().register(storage_block);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(enchanter_block, new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB)).setRegistryName(REFERENCE.MODID, "enchanter_block"));
        event.getRegistry().register(new BlockItem(disenchanter_block, new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB)).setRegistryName(REFERENCE.MODID, "disenchanter_block"));
        event.getRegistry().register(new BlockItem(storage_block, new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB).stacksTo(1).rarity(Rarity.RARE)).setRegistryName(REFERENCE.MODID, "enchantment_block"));
    }

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(enchanter_container);
        event.getRegistry().register(disenchanter_container);
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(enchanter_tile);
        event.getRegistry().register(disenchanter_tile);
        event.getRegistry().register(storage_tile);
    }
}
