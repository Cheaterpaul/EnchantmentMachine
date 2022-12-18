package de.cheaterpaul.enchantmentmachine.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;

public class EnchantmentList extends AbstractScrollWidget {

    private Map<EnchantmentInstanceMod, Integer> enchantments;
    private GridWidget content;
    public EnchantmentList(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.empty());
        this.content = buildContent();
    }

    @Override
    protected int getInnerHeight() {
        return this.content.getHeight();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.getInnerHeight() > this.height;
    }

    @Override
    protected double scrollRate() {
        return 9.0;
    }

    @Override
    protected int innerPadding() {
        return 0;
    }

    @Override
    protected void renderContents(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        pMouseY += this.scrollAmount();
        int i = this.innerPadding();
        int j = this.innerPadding();
        pPoseStack.pushPose();
        pPoseStack.translate(i,j,0);
        this.content.setPosition(this.getX(), this.getY());
        this.content.render(pPoseStack,pMouseX,pMouseY,pPartialTick);
        pPoseStack.popPose();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
        super.renderBg(pPoseStack, pMinecraft, pMouseX, pMouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }

    protected int containerWidth() {
        return this.width - totalInnerPadding();
    }

    private GridWidget buildContent() {
        ContentBuilder builder = new ContentBuilder(this.containerWidth());



        return builder.build();
    }

    public void setItems(Object2IntMap<EnchantmentInstanceMod> collect) {
        this.enchantments = collect;
        this.content = buildContent();
    }

    static class ContentBuilder {
        private final int width;
        private final GridWidget grid;
        private final GridWidget.RowHelper helper;
        private final LayoutSettings alignHeader;
        private final MutableComponent narration = Component.empty();

        public ContentBuilder(int p_261784_) {
            this.width = p_261784_;
            this.grid = new GridWidget();
            this.grid.defaultCellSetting().alignHorizontallyLeft();
            this.helper = this.grid.createRowHelper(1);
//            this.helper.addChild(SpacerWidget.width(p_261784_));
            this.alignHeader = this.helper.newCellSettings().alignHorizontallyCenter().paddingHorizontal(0);
        }

        public void addWidget(AbstractWidget widget) {
            this.helper.addChild(widget, alignHeader.paddingBottom(-1));
        }

        public void addSpacer(int p_261997_) {
            this.helper.addChild(SpacerWidget.height(p_261997_));
        }

        public GridWidget build() {
            this.grid.pack();
            return this.grid;
        }
    }
}
