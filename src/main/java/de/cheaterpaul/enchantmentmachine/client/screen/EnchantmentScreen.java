package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchantment.png");

    private Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();
    private ScrollableListButton list;

    private final int xSize = 176;
    private final int ySize = 222;

    private int guiLeft;
    private int guiTop;

    public EnchantmentScreen() {
        super(new StringTextComponent("Test"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(list = new ScrollableListButton(100,100,130,10,enchantments.size(),null, new TranslationTextComponent("testname"), this::onPress));
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onPress(Integer pressed) {

    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.enchantments = enchantments;
        this.list.updateList(enchantments);
    }

}
