package de.cheaterpaul.enchantmentmachine.data;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.function.Consumer;

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
            ModelFile enchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.enchanter_block), models().existingFileHelper);

            ModelFile enchantment_block = models().withExistingParent(ModData.enchantment_block.getRegistryName().toString(), "block/enchanting_table")
                    .texture("particle",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom",new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"));

            ModelFile disenchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.disenchanter_block), models().existingFileHelper);

            simpleBlock(ModData.enchanter_block, enchanter);
            simpleBlock(ModData.disenchanter_block, disenchanter);
            simpleBlock(ModData.enchantment_block, enchantment_block);
        }
    }

    public static class RecipeGenerator extends RecipeProvider {
        public RecipeGenerator(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder.shapedRecipe(ModData.enchantment_block).key('B', Items.BOOK).key('#', Blocks.CRYING_OBSIDIAN).key('D', Items.DIAMOND).patternLine("BBB").patternLine("D#D").patternLine("###").addCriterion("has_obsidian", hasItem(Blocks.CRYING_OBSIDIAN)).build(consumer);
            ShapedRecipeBuilder.shapedRecipe(ModData.disenchanter_block).key('B', Items.BOOK).key('#', Blocks.CRYING_OBSIDIAN).key('D', Items.DIAMOND_AXE).patternLine(" B ").patternLine("D#D").patternLine("###").addCriterion("has_obsidian", hasItem(Blocks.CRYING_OBSIDIAN)).build(consumer);
            ShapedRecipeBuilder.shapedRecipe(ModData.enchanter_block).key('B', Items.BOOK).key('#', Blocks.CRYING_OBSIDIAN).key('D', Items.DIAMOND).patternLine(" B ").patternLine("D#D").patternLine("###").addCriterion("has_obsidian", hasItem(Blocks.CRYING_OBSIDIAN)).build(consumer);
        }
    }

    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(new BlockStateGenerator(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
        }
        generator.addProvider(new RecipeGenerator(generator));
    }
}
