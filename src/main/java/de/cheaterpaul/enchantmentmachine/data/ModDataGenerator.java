package de.cheaterpaul.enchantmentmachine.data;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class ModDataGenerator {

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            //this.withExistingParent(ModData.enchanter_block.getRegistryName().getPath(), REFERENCE.MODID + ":block/" + ModData.enchanter_block.getRegistryName().getPath());
            //this.withExistingParent(ModData.disenchanter_block.getRegistryName().getPath(), REFERENCE.MODID + ":block/" + ModData.disenchanter_block.getRegistryName().getPath());
            //this.withExistingParent(ModData.enchantment_block.getRegistryName().getPath(), REFERENCE.MODID + ":block/" + ModData.enchantment_block.getRegistryName().getPath());
            getBuilder(ModData.enchanter_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.enchanter_block.getRegistryName().getPath()));
            getBuilder(ModData.disenchanter_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.disenchanter_block.getRegistryName().getPath()));
            getBuilder(ModData.enchantment_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.enchantment_block.getRegistryName().getPath()));
        }
    }

    public static class BlockStateGenerator extends BlockStateProvider {
        public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
            super(gen, REFERENCE.MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            simpleBlock(ModData.enchanter_block,models().cubeAll(ModData.enchanter_block.getRegistryName().getPath(), mcLoc("block/stone")));
            simpleBlock(ModData.disenchanter_block,models().cubeAll(ModData.disenchanter_block.getRegistryName().getPath(), mcLoc("block/stone")));
            simpleBlock(ModData.enchantment_block,models().cubeAll(ModData.enchantment_block.getRegistryName().getPath(), mcLoc("block/stone")));
        }
    }

    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(new BlockStateGenerator(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
        }
    }
}
