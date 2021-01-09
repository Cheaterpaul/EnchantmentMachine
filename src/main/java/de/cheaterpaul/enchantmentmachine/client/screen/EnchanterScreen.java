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
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainer> {

    private static final ResourceLocation MISC = new ResourceLocation(REFERENCE.MODID, "textures/gui/misc.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();
    private List<EnchantmentInstance> selectedEnchantments = new ArrayList<>();

    private ScrollableListButton<Pair<EnchantmentInstance,Integer>> list;


    public EnchanterScreen(EnchanterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.xSize = 232;
        this.ySize = 241;
        this.playerInventoryTitleX = 36;
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
        this.addButton(new ImageButton(this.guiLeft + 203,this.guiTop + 40, 14,12, 46,0, 13, MISC, this::apply));
        this.addButton(list = new ScrollableListButton<>(this.guiLeft + 8,this.guiTop +  15,this.xSize - 60,this.ySize - 94 - 17, 21, EnchantmentItem::new));
    }

    private void apply(Button button) {
        if (this.container.getSlot(0).getHasStack()) {
            EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(selectedEnchantments));
        }
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.list.setItems(enchantments.object2IntEntrySet().stream().map(s -> Pair.of(s.getKey(), s.getIntValue())).collect(Collectors.toSet()));
        this.enchantments = enchantments;
    }

    private class EnchantmentItem extends ScrollableListButton.ListItem<Pair<EnchantmentInstance,Integer>> {

        private final ItemStack bookStack;
        private final ITextComponent name;

        public EnchantmentItem(Pair<EnchantmentInstance, Integer> item) {
            super(item);
            bookStack = new ItemStack(Items.ENCHANTED_BOOK,item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(),item.getKey().getLevel()), bookStack);
            name = ((IFormattableTextComponent) item.getKey().getEnchantment().getDisplayName(item.getKey().getLevel())).modifyStyle(style -> style.getColor().getColor() == TextFormatting.GRAY.getColor()? style.applyFormatting(TextFormatting.WHITE):style);
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
            EnchanterScreen.this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(bookStack, x + 5,y +2 + yOffset);
            EnchanterScreen.this.font.drawStringWithShadow(matrixStack, name.getString(), x + 25,y + yOffset + 5, name.getStyle().getColor().getColor());


            String count = String.valueOf(bookStack.getCount());

            EnchanterScreen.this.font.drawStringWithShadow(matrixStack, count, x + listWidth - 10, y + yOffset + 5, 0xffffff);
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                EnchanterScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
        }
    }
}
