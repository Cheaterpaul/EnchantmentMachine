package de.cheaterpaul.enchantmentmachine.data;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
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
            BlockModelBuilder enchanter = models().withExistingParent(ModData.enchanter_block.getRegistryName().toString(), "block/enchanting_table")
                    .texture("particle",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"));
            BlockModelBuilder disenchanter = models().withExistingParent(ModData.disenchanter_block.getRegistryName().toString(), "block/enchanting_table")
                    .texture("particle",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"));
            BlockModelBuilder enchantment_block = models().withExistingParent(ModData.enchantment_block.getRegistryName().toString(), "block/enchanting_table")
                    .texture("particle",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"));
            simpleBlock(ModData.enchanter_block, enchanter);
            simpleBlock(ModData.disenchanter_block, disenchanter);
            simpleBlock(ModData.enchantment_block, enchantment_block);
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
