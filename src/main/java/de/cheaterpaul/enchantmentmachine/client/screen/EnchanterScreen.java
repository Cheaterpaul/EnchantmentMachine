package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainer> {

    private static final ResourceLocation MISC = new ResourceLocation(REFERENCE.MODID, "textures/gui/misc.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();
    private List<EnchantmentInstance> selectedEnchantments = new ArrayList<>();


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

        this.addButton(new ImageButton(this.guiLeft + 148,this.guiTop + 40, 14,12, 46,0, 13, MISC, this::apply));

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

    private void apply(Button button) {
        if (this.container.getSlot(0).getHasStack()) {
            EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(selectedEnchantments));
        }
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.enchantments = enchantments;
    }
}
