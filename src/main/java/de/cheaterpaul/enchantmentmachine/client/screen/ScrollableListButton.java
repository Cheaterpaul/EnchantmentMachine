package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * add this buttons first and render them last and call {@link #mouseDragged(double, double, int, double, double)}
 */
public class ScrollableListButton<T> extends ExtendedButton {

    private static final ResourceLocation MISC = new ResourceLocation(REFERENCE.MODID, "textures/gui/misc.png");
//    protected final int menuSize;
//    protected int itemCount;
//    protected int scrolled;
//    private boolean scrollerPressed;
//    protected final Button[] elements;
//    private final Consumer<Integer> pressConsumer;
//    private ITextComponent[] desc;
//    private final boolean alternate;

    private final ItemCreator<T> itemSupplier;
    private final int itemHeight;
    private final int scrollerWidth = 9;

    private final List<ListItem<T>> listItems = new ArrayList<>();
    private int scrolled;
    private double scrolledD;
    private boolean scrollerClicked;


    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight) {
        this(xPos, yPos, width, height, itemHeight, ListItem::new);
    }
    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight, ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, new StringTextComponent(""), (button) ->{});
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
    }

    public void setItems(Set<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(item, this.width, this.itemHeight)));
    }

    public void addItem(T element) {
        this.listItems.add(this.itemSupplier.apply(element, this.width, this.itemHeight));
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int itemHeight = this.itemHeight-1; // only 1 pixel between items
        for (int i = 0; i < this.listItems.size(); i++) {

            int y = i*itemHeight - scrolled;

            if (y < -itemHeight) {
                continue;
            }


            ListItem<T> item = this.listItems.get(i);
            item.render(matrixStack, this.x, this.y, this.width  - scrollerWidth, this.height, this.itemHeight, y,  mouseX, mouseY, partialTicks, this.getBlitOffset());

        }
        this.renderScrollBar(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScrollBar(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        GuiUtils.drawContinuousTexturedBox(matrixStack, MISC, this.x + this.width - this.scrollerWidth, this.y,0,0,9, this.height, 9, 200,2, getBlitOffset());
        this.renderScroller(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScroller(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int scrollerHeight = 27;
        int scrollHeight = this.height - 2 - scrollerHeight;
        float perc = (float)this.scrolled/(float)(this.listItems.size() * this.itemHeight - this.height);
        int yOffset = (int)(scrollHeight * perc);
        Minecraft.getInstance().textureManager.bindTexture(MISC);
        blit(matrixStack, this.x + this.width - this.scrollerWidth + 1, this.y + yOffset+ 1, 9,0,7,27);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scrolled = Math.max(this.scrolled +  4 *((int) delta), 0);
        this.scrolled = Math.min(this.scrolled, this.listItems.size() * this.itemHeight - this.height);
        this.scrolledD = scrolled;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(scrollerClicked) {
            this.scrolledD += dragY * 1.5;
            this.scrolled = ((int) this.scrolledD);
            this.scrolled = Math.max(this.scrolled, 0);
            this.scrolled = Math.min(this.scrolled, this.listItems.size() * this.itemHeight - this.height);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolledD = this.scrolled;
        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            if (mouseX >= this.x + this.width - this.scrollerWidth) {
                this.scrollerClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollerClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(T item, int width, int height);
    }

    public static class ListItem<T> {

        private final T item;
        private final int width;

        public ListItem(T item, int width, int height) {
            this.item = item;
            this.width = width;
        }

        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            int ySize = MathHelper.clamp(listHeight - yOffset,0,itemHeight);
            int yTopBorder = 3;
            int yBottomBorder = 3;
            int v = 66;
            int textureHeight= 20;
            if (ySize <= 0)return;
            if (ySize < itemHeight) {
                if (ySize < 3) {
                    yTopBorder = ySize;
                }
                yBottomBorder = 0;
                textureHeight-=3;
            }
            if (yOffset < 0) {
                ySize+=yOffset;
                yTopBorder = Math.max(0, yTopBorder + yOffset);
                v += 3-yTopBorder;
                textureHeight -=3-yTopBorder;
                if (-yOffset >= itemHeight-2) {
                    yBottomBorder = 1;
                }
                yOffset=0;
            }
            GuiUtils.drawContinuousTexturedBox(matrixStack, new ResourceLocation("textures/gui/widgets.png"), x, y + yOffset,0,v, listWidth+1, ySize, 200, textureHeight, yTopBorder, yBottomBorder,3,3, zLevel);
        }

    }


    //    public ScrollableListButton(int xPos, int yPos, int width, int shownItems, int maxItemCount, @Nullable ITextComponent[] strings, ITextComponent displayString, Consumer<Integer> elementPressAction, boolean alternate) {
//        super(xPos, yPos + 1, width, Math.min(shownItems, maxItemCount) * 20, displayString, button -> {
//        });
//        this.itemCount = maxItemCount;
//        this.menuSize = shownItems;
//        this.visible = true;
//        this.elements = new Button[menuSize];
//        this.pressConsumer = elementPressAction;
//        this.desc = strings;
//        this.alternate = alternate;
//        this.fillElements();
//    }

//    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemCount, ITextComponent[] strings, ITextComponent displayString, Consumer<Integer> elementPressAction) {
//        this(xPos, yPos, width, height, itemCount, strings, displayString, elementPressAction, false);
//    }

//    public void updateList(Object2IntMap<EnchantmentInstance> list) {
//        this.itemCount = list.size();
//        this.desc = new ITextComponent[this.itemCount];
//        AtomicInteger index = new AtomicInteger();
//        list.forEach((inst, count) -> {
//            this.desc[index.getAndIncrement()] = new TranslationTextComponent(inst.getEnchantment().getRegistryName().toString()).appendString(" " + inst.getLevel() + "    " + count);
//        });
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
//        if (this.visible) {
//            this.scrollerPressed = false;
//            if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + height) {
//                if (this.itemCount - this.menuSize > 0 && mouseX > this.x + this.width - 8) {
//                    this.scrollerPressed = true;
//                }
//                for (Button button : this.elements) {
//                    if (button.mouseClicked(mouseX, mouseY, buttonId)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return super.mouseClicked(mouseX, mouseY, buttonId);
//    }

//    @Override
//    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
//        if (this.visible) {
//            this.hLine(mStack, this.x, this.x + this.width, this.y - 1, alternate ? 0xff373737 : 0xff000000);
//            GuiUtils.drawContinuousTexturedBox(mStack, MISC, this.x + width - 8, this.y - 1, alternate ? 23 : 0, 0, 9, this.height + 2, 9, 200, 2, 2, 2, 2, this.getBlitOffset());
//            this.renderScroller(mStack);
//            this.renderListButtons(mStack, mouseX, mouseY, partialTicks);
//            if (this.elements.length != 0 && this.elements[this.elements.length - 1].visible) {
//                this.hLine(mStack, this.x, this.x + this.width, this.y + this.height, alternate ? 0xffffffff : 0xff000000);
//            }
//        }
//    }
//
//    protected void fillElements() {
//        for (int i = 0; i < this.elements.length; i++) {
//            int finalI = i;
//            this.elements[i] = new ExtendedButton(this.x, this.y + i * 20, width - 7, 20, new StringTextComponent(""), (button -> this.pressConsumer.accept(finalI + this.scrolled)));
//        }
//    }
//
//    protected void renderScroller(MatrixStack mStack) {
//        Minecraft.getInstance().textureManager.bindTexture(MISC);
//        int i = this.itemCount - this.menuSize;
//        if (i >= 1) {
//            float k = (float) (this.height + 3 - 30) / i;
//            int i1 = Math.min(this.height + 3 - 30, (int) (this.scrolled * k));
//            if (this.scrolled >= i) {
//                i1 = this.height + 3 - 30;
//            }
//            blit(mStack, x + this.width - 7, y + i1, this.getBlitOffset(), (alternate ? 23 : 0) + 10 - 1, 0, 7, 27, 256, 256);
//        } else {
//            //blit(mStack, x + this.width - 7, y, this.getBlitOffset(), (alternate ? 23 : 0) + 10 + 6, 0, 7, this.elements.length == 1 ? 20 : 27, 256, 256); Don't render (disabled) scroller if there are not enough items
//        }
//    }
//
//    private void renderListButtons(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
//        for (int i = 0; i < this.elements.length; i++) {
//            this.elements[i].visible = itemCount > menuSize || i < itemCount;
//            if (this.elements[i].visible) {
//                this.elements[i].render(mStack, mouseX, mouseY, partialTicks);
//                ITextComponent desc = this.desc != null ? this.desc[this.scrolled + i] : new StringTextComponent("Type " + (i + this.scrolled + 1));
//                int x = this.x + (this.width - 8) / 2 - Minecraft.getInstance().fontRenderer.getStringPropertyWidth(desc) / 2;
//                Minecraft.getInstance().fontRenderer.func_243246_a(mStack, desc, x, this.y + 6 + i * 20, this.elements[i].getFGColor());
//            }
//        }
//    }
//
//    @Override
//    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
//        int scrollItems = this.itemCount - this.menuSize;
//        if (scrollItems > 0) {
//            this.scrolled = (int) ((double) this.scrolled - p_mouseScrolled_5_);
//            this.scrolled = MathHelper.clamp(this.scrolled, 0, scrollItems);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
//        if (scrollerPressed) {
//            float amount = ((float) p_mouseDragged_3_ - (float) this.y - 13.5F) / ((float) (this.height) - 27.0F);
//            amount = amount * (float) (this.itemCount - this.menuSize) + 0.5f;
//            this.scrolled = MathHelper.clamp((int) amount, 0, this.itemCount - this.menuSize);
//            return true;
//        } else {
//            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
//        }
//    }
}
