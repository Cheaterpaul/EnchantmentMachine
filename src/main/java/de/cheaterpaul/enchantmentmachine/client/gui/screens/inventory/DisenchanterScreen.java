package de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DisenchanterScreen extends EnchantmentBaseScreen<DisenchanterContainerMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/disenchanter.png");

    public DisenchanterScreen(DisenchanterContainerMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}
