package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
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
import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private Map<EnchantmentInstance,Pair<EnchantmentInstance,Integer>> enchantments = new HashMap<>();
    private ScrollableListButton<Pair<EnchantmentInstance,Integer>> list;


    public EnchanterScreen(EnchanterContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
        this.xSize = 232;
        this.ySize = 241;
        this.playerInventoryTitleX = 36;
        this.playerInventoryTitleY = this.ySize - 94;
        container.setListener(this::refreshActiveEnchantments);
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
        this.addButton(list = new ScrollableListButton<>(this.guiLeft + 8,this.guiTop +  15,this.xSize - 50,this.ySize - 94 - 17, 21,EnchantmentItem::new));
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.enchantments.clear();
        enchantments.forEach((instance, integer) -> {
            this.enchantments.put(instance, Pair.of(instance, integer));
        });
        refreshActiveEnchantments();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (!this.dragSplitting) {
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    public void refreshActiveEnchantments() {
        ItemStack stack = this.container.getSlot(0).getStack();
        if (stack.isEmpty()) {
            this.list.setItems(this.enchantments.values());
        } else {
            this.list.setItems(this.enchantments.values().stream().filter(pair -> stack.canApplyAtEnchantingTable(pair.getKey().getEnchantment())).collect(Collectors.toList()));
        }
    }

    private void apply(EnchantmentInstance instance) {
        if (this.container.getSlot(0).getHasStack()) {
            EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(Collections.singletonList(instance)));
            Pair<EnchantmentInstance, Integer> value = this.enchantments.get(instance);
            if (value.getValue() > 1) {
                this.enchantments.put(instance, Pair.of(instance, value.getValue() -1));
            }else {
                this.enchantments.remove(instance);
            }
        }
        refreshActiveEnchantments();
    }

    private class EnchantmentItem extends ScrollableListButton.ListItem<Pair<EnchantmentInstance,Integer>> {

        private final ItemStack bookStack;
        private final ITextComponent name;
        private final Button button;

        public EnchantmentItem(Pair<EnchantmentInstance, Integer> item) {
            super(item);
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK,item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(),item.getKey().getLevel()), bookStack);
            this.name = ((IFormattableTextComponent) item.getKey().getEnchantment().getDisplayName(item.getKey().getLevel())).modifyStyle(style -> style.getColor().getColor() == TextFormatting.GRAY.getColor()? style.applyFormatting(TextFormatting.WHITE):style);
            this.button = new ImageButton(0,0,11,17,1,208,18,new ResourceLocation("textures/gui/recipe_book.png"), (button) -> EnchanterScreen.this.apply(item.getKey()));
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
            EnchanterScreen.this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(bookStack, x + 5,y +2 + yOffset);

//            EnchanterScreen.this.font.drawString(matrixStack, name.getString(), x + 25,y + yOffset + 5, name.getStyle().getColor().getColor());
//
//
//            String count = String.valueOf(bookStack.getCount());
//
//            EnchanterScreen.this.font.drawString(matrixStack, count, x + listWidth - 20, y + yOffset + 5, 0xffffff);

            this.button.x = x + listWidth - 12;
            this.button.y = y + yOffset + 2;

            this.button.visible = EnchanterScreen.this.container.getSlot(0).getHasStack();
            this.button.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth - 20 && mouseY > y && mouseY < y + itemHeight) {
                EnchanterScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (mouseX > this.button.x && mouseX < this.button.x + this.button.getWidth() && mouseY > this.button.y && mouseY < this.button.y + this.button.getHeightRealms()) {
                this.button.onClick(mouseX, mouseY);
                return true;
            }
            return false;
        }
    }
}
