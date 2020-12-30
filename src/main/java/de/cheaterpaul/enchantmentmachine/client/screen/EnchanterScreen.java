package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainer> {

//    public static final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    public EnchanterScreen(EnchanterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.xSize = 176;
        this.ySize = 222;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();

        //this.addButton(new ImageButton());

        /*minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        GlStateManager.disableDepthTest();
        int i = 0;
        if (this.isHovered()) {
            i += 13;
        }
        int j;
        switch (action) {
            case ACCEPT:
                j = 190;
                break;
            case COMPLETE:
                j = 176;
                break;
            default:
                j = 204;

        }

        blit(this.x, this.y, (float) j, (float) i, this.width, this.height, 256, 256);
        GlStateManager.enableDepthTest();*/
    }
}
