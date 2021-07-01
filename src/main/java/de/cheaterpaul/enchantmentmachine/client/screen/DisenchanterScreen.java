package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DisenchanterScreen extends EnchantmentBaseScreen<DisenchanterContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/disenchanter.png");

    public DisenchanterScreen(DisenchanterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
