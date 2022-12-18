package de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.client.gui.components.ScrollWidget;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScreenUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainerMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private final Map<EnchantmentInstanceMod, Pair<EnchantmentInstanceMod, Integer>> enchantments = new HashMap<>();
    private ScrollWidget<Pair<EnchantmentInstanceMod, Integer>> list;
    private Map<Enchantment, Integer> itemEnchantments = new HashMap<>();


    public EnchanterScreen(EnchanterContainerMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        this.imageWidth = 232;
        this.imageHeight = 241;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 94;
        container.setListener(this::refreshActiveEnchantments);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        refreshActiveEnchantments();
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

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(this.list = new ScrollWidget<>(this.leftPos + 8, this.topPos + 15, this.imageWidth - 70, this.imageHeight - 94 - 17));
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(!(this.getFocused() != null && this.isDragging() && pButton == 0 && this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return true;
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments.clear();
        enchantments.forEach((instance, integer) -> this.enchantments.put(instance, Pair.of(instance, integer)));
        refreshActiveEnchantments();
    }

//    @Override
//    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
//        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
//        if (!this.isQuickCrafting) {
//            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
//        }
//        return true;
//    }

    public void refreshActiveEnchantments() {
        ItemStack stack = this.menu.getSlot(0).getItem();
        this.itemEnchantments = EnchantmentHelper.getEnchantments(stack);

        List<Pair<EnchantmentInstanceMod, Integer>> availableEnchantments;
        if (stack.isEmpty()) {
            availableEnchantments = this.enchantments.values().stream().sorted(Comparator.comparing(o -> o.getKey().getEnchantmentName().getString())).collect(Collectors.toList());
        } else {
            availableEnchantments = this.enchantments.values().stream().filter(pair -> stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK || pair.getKey().getEnchantment().canEnchant(stack)).sorted(Comparator.comparing(o -> o.getKey().getEnchantmentName().getString())).collect(Collectors.toList());
        }
        this.list.updateContent(EnchantmentItem::new, pairContentBuilder -> availableEnchantments.forEach(pairContentBuilder::addWidget));
    }

    private void apply(EnchantmentInstanceMod instance) {
        if (this.menu.getSlot(0).hasItem()) {
            if (ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), instance.getEnchantment()) || hasEqualEnchantments(itemEnchantments, instance)) {
                EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(Collections.singletonList(instance)));
                Pair<EnchantmentInstanceMod, Integer> value = this.enchantments.get(instance);
                if (value.getValue() > 1) {
                    this.enchantments.put(instance, Pair.of(instance, value.getValue() - 1));
                } else {
                    this.enchantments.remove(instance);
                }
            }
        }
        refreshActiveEnchantments();
    }

    private boolean hasEqualEnchantments(Map<Enchantment, Integer> itemEnchantments, EnchantmentInstanceMod enchantment) {
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            if (entry.getKey() == enchantment.getEnchantment()) {
                if (entry.getKey().getMaxLevel() != entry.getValue() && entry.getValue() <= enchantment.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    private class EnchantmentItem extends AbstractContainerWidget {

        private final ItemStack bookStack;
        private final Component name;
        private final Button button;
        private final AbstractWidget text;
        private final int requiredLevels;
        private final Pair<EnchantmentInstanceMod, Integer> item;
        private final BiFunction<Integer, Integer, Boolean> isXYInBounds;

        public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item, int x, int y, int width, BiFunction<Integer, Integer, Boolean> isXYInBounds) {
            super(x, y, width, 18, item.getKey().getEnchantmentName());
            this.isXYInBounds = isXYInBounds;
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            this.item = item;
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = item.getKey().getEnchantmentName();
            Style style = this.name.getStyle();
            //noinspection ConstantConditions
            if(style.getColor() == null || style.getColor().getValue() == ChatFormatting.GRAY.getColor()) {
                ((MutableComponent) this.name).withStyle(style.withColor(ChatFormatting.WHITE));
            }
            this.button = new ImageButton(this.width -12, 2, 11, 17, 1, 208, 18, new ResourceLocation("textures/gui/recipe_book.png"), 256, 256, (button) -> EnchanterScreen.this.apply(item.getKey()), Component.empty());
            MutableComponent text;
            if (isCompatible()) {
                if (hasSufficientLevels()) {
                    text = Component.translatable("text.enchantmentmachine.enchant_for_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.GREEN);
                } else {
                    text = Component.translatable("text.enchantmentmachine.require_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.YELLOW);
                }
            } else {
                text = Component.translatable("text.enchantmentmachine.unavailable").withStyle(ChatFormatting.RED);
            }
            this.button.setTooltip(Tooltip.create(text));
            this.requiredLevels = calculateRequiredLevels();
            this.text = MultiLineTextWidget.create(200, Minecraft.getInstance().font, this.name);
            this.text.setPosition(25,5);
        }

        @Override
        protected List<? extends AbstractWidget> getContainedChildren() {
            return List.of(button, text);
        }

        private boolean isCompatible() {
            EnchantmentInstanceMod s = this.item.getKey();
            for (Map.Entry<Enchantment, Integer> entry : EnchanterScreen.this.itemEnchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                if (enchantment == s.getEnchantment()) { //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                    int newLevel = Math.min(enchantment.getMaxLevel(), s.getLevel() == entry.getValue() ? s.getLevel() + 1 : Math.max(s.getLevel(), entry.getValue()));
                    s = new EnchantmentInstanceMod(enchantment, newLevel); //Override enchInst in loop.
                }
            }
            return s.canEnchant() && ((ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.isEnchantmentCompatible(EnchanterScreen.this.itemEnchantments.keySet(), this.item.getKey().getEnchantment())) || hasEqualEnchantments(EnchanterScreen.this.itemEnchantments, this.item.getKey()));
        }

        @Override
        public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
            this.isHovered = this.isHovered && this.isXYInBounds.apply(pMouseX, pMouseY);
            this.button.visible = EnchanterScreen.this.menu.getSlot(0).hasItem();

            ScreenUtils.blitWithBorder(poseStack, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + 21, this.width, this.height, 200, 18, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(poseStack, Minecraft.getInstance(), pMouseX, pMouseY);

            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(0,-EnchanterScreen.this.list.getScrollAmount(),0);
            RenderSystem.applyModelViewMatrix();
            EnchanterScreen.this.itemRenderer.renderAndDecorateFakeItem(bookStack, this.getX() + 5, this.getY() + 1);
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();


            EnchanterScreen.this.font.drawShadow(poseStack, name, this.getX() + 25, this.getY() +5,-1);

            String count = String.valueOf(bookStack.getCount());
            EnchanterScreen.this.font.drawShadow(poseStack, count, this.getX() + this.width - 20, this.getY() +5, 0xffffff);

            this.button.setPosition(this.getX() + this.width - 12, this.getY() +1);

            this.button.visible = EnchanterScreen.this.menu.getSlot(0).hasItem();

            if (isCompatible()) {
                if (hasSufficientLevels()) {
                    RenderSystem.setShaderColor(0.2f, 1f, 0.4f, 1);
                } else {
                    RenderSystem.setShaderColor(0.5f, 0.4f, 0.2f, 1);
                }
            } else {
                RenderSystem.setShaderColor(1f, 0.2f, 0.4f, 1);
            }
            poseStack.pushPose();

            if (isHovered) {
                setTooltip(new MultilineTooltip(EnchanterScreen.this.getTooltipFromItem(this.bookStack)));
            }

            this.button.render(poseStack, pMouseX, pMouseY, pPartialTick);
            poseStack.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        private int calculateRequiredLevels() {
            Pair<EnchantmentInstanceMod, Integer> result = Utils.tryApplyEnchantment(this.item.getKey(), EnchanterScreen.this.itemEnchantments, true);
            return result == null ? -1 : result.getRight();
        }

        private boolean hasSufficientLevels() {
            return EnchanterScreen.this.menu.getPlayer().experienceLevel >= this.requiredLevels || EnchanterScreen.this.menu.getPlayer().getAbilities().instabuild;
        }
    }
}
