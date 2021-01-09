package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL20;

import java.util.Collections;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchantment.png");

    private Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();
    private ScrollableListButton<Pair<EnchantmentInstance,Integer>> list;

    private final int xSize = 197;
    private final int ySize = 222;

    private int guiLeft;
    private int guiTop;

    public EnchantmentScreen() {
        super(new StringTextComponent("Enchantments"));
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.addButton(list = new ScrollableListButton<>(this.guiLeft + 10,this.guiTop + 10,this.xSize - 20,this.ySize - 20, 21, EnchantmentItem::new));
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

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.enchantments = enchantments;
        this.list.setItems(enchantments.object2IntEntrySet().stream().map(s -> Pair.of(s.getKey(), s.getIntValue())).collect(Collectors.toSet()));
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
            EnchantmentScreen.this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(bookStack, x + 5,y +2 + yOffset);
            EnchantmentScreen.this.font.drawStringWithShadow(matrixStack, name.getString(), x + 25,y + yOffset + 5, name.getStyle().getColor().getColor());


            String count = String.valueOf(bookStack.getCount());

            EnchantmentScreen.this.font.drawStringWithShadow(matrixStack, count, x + listWidth - 10, y + yOffset + 5, 0xffffff);
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                EnchantmentScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
        }
    }
}
