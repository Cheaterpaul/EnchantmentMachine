package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainer;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private final Map<EnchantmentInstance, Pair<EnchantmentInstance, Integer>> enchantments = new HashMap<>();
    private ScrollableListButton<Pair<EnchantmentInstance, Integer>> list;
    private Map<Enchantment, Integer> itemEnchantments = new HashMap<>();


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
        this.addButton(list = new ScrollableListButton<>(this.guiLeft + 8, this.guiTop + 15, this.xSize - 50, this.ySize - 94 - 17, 21, EnchantmentItem::new));
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments) {
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
        this.itemEnchantments = EnchantmentHelper.getEnchantments(stack);
        if (stack.isEmpty()) {
            this.list.setItems(this.enchantments.values());
        } else {
            this.list.setItems(this.enchantments.values().stream().filter(pair -> stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK || stack.canApplyAtEnchantingTable(pair.getKey().getEnchantment())).collect(Collectors.toList()));
        }
    }

    private void apply(EnchantmentInstance instance) {
        if (this.container.getSlot(0).getHasStack()) {
            if (ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.areAllCompatibleWith(itemEnchantments.keySet(), instance.getEnchantment()) || hasEqualEnchantments(itemEnchantments, instance)) {
                EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(Collections.singletonList(instance)));
                Pair<EnchantmentInstance, Integer> value = this.enchantments.get(instance);
                if (value.getValue() > 1) {
                    this.enchantments.put(instance, Pair.of(instance, value.getValue() - 1));
                } else {
                    this.enchantments.remove(instance);
                }
            }
        }
        refreshActiveEnchantments();
    }

    private boolean hasEqualEnchantments(Map<Enchantment, Integer> itemEnchantments, EnchantmentInstance enchantment) {
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            if (entry.getKey() == enchantment.getEnchantment()) {
                if (entry.getKey().getMaxLevel() != entry.getValue() && entry.getValue() <= enchantment.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    private class EnchantmentItem extends ScrollableListButton.ListItem<Pair<EnchantmentInstance, Integer>> {

        private final ItemStack bookStack;
        private final ITextComponent name;
        private final Button button;
        private final int requiredLevels;

        public EnchantmentItem(Pair<EnchantmentInstance, Integer> item) {
            super(item);
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = ((IFormattableTextComponent) item.getKey().getEnchantment().getDisplayName(item.getKey().getLevel())).modifyStyle(style -> style.getColor().getColor() == TextFormatting.GRAY.getColor() ? style.applyFormatting(TextFormatting.WHITE) : style);
            this.button = new ImageButton(0, 0, 11, 17, 1, 208, 18, new ResourceLocation("textures/gui/recipe_book.png"), 256, 256, (button) -> EnchanterScreen.this.apply(item.getKey()), new Button.ITooltip() {
                @Override
                public void onTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
                    if (mouseX > button.x && mouseX < button.x + button.getWidth() && mouseY > button.y && mouseY < button.y + button.getHeightRealms()) {
                        IFormattableTextComponent text;
                        if (isCompatible()) {
                            if (hasSufficientLevels()) {
                                text = new TranslationTextComponent("text.enchantmentmachine.enchant_for_level", EnchantmentItem.this.requiredLevels).mergeStyle(TextFormatting.GREEN);
                            } else {
                                text = new TranslationTextComponent("text.enchantmentmachine.require_level", EnchantmentItem.this.requiredLevels).mergeStyle(TextFormatting.YELLOW);
                            }
                        } else {
                            text = new TranslationTextComponent("text.enchantmentmachine.unavailable").mergeStyle(TextFormatting.RED);
                        }
                        EnchanterScreen.this.renderTooltip(matrixStack, text, mouseX, mouseY);
                    }
                }
            }, StringTextComponent.EMPTY);
            requiredLevels = calculateRequiredLevels();
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (!this.button.visible) return false;
            if (mouseX > this.button.x && mouseX < this.button.x + this.button.getWidth() && mouseY > this.button.y && mouseY < this.button.y + this.button.getHeightRealms()) {
                if (isCompatible() && hasSufficientLevels()) {
                    this.button.onClick(mouseX, mouseY);
                }
                return true;
            }
            return false;
        }

        private boolean isCompatible() {
            EnchantmentInstance s = this.item.getKey();
            for (Map.Entry<Enchantment, Integer> entry : EnchanterScreen.this.itemEnchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                if (enchantment == s.getEnchantment()) { //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                    int newLevel = Math.min(enchantment.getMaxLevel(), s.getLevel() == entry.getValue() ? s.getLevel() + 1 : Math.max(s.getLevel(), entry.getValue()));
                    s = new EnchantmentInstance(enchantment, newLevel); //Override enchInst in loop.
                }
            }
            return s.canEnchant() && ((ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.areAllCompatibleWith(EnchanterScreen.this.itemEnchantments.keySet(), this.item.getKey().getEnchantment())) || hasEqualEnchantments(EnchanterScreen.this.itemEnchantments, this.item.getKey()));
        }

        @Override
        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);

            EnchanterScreen.this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(bookStack, x + 5, y + 2 + yOffset);
            EnchanterScreen.this.font.drawStringWithShadow(matrixStack, name.getString(), x + 25, y + yOffset + 5, name.getStyle().getColor().getColor());

            String count = String.valueOf(bookStack.getCount());
            EnchanterScreen.this.font.drawStringWithShadow(matrixStack, count, x + listWidth - 20, y + yOffset + 5, 0xffffff);

            this.button.x = x + listWidth - 12;
            this.button.y = y + yOffset + 2;

            this.button.visible = EnchanterScreen.this.container.getSlot(0).getHasStack();

            if (isCompatible()) {
                if (hasSufficientLevels()) {
                    RenderSystem.color4f(0.2f, 1f, 0.4f, 1);
                } else {
                    RenderSystem.color4f(0.5f, 0.4f, 0.2f, 1);
                }
            } else {
                RenderSystem.color4f(1f, 0.2f, 0.4f, 1);
            }
            RenderSystem.pushMatrix();
            this.button.render(matrixStack, mouseX, mouseY, partialTicks);
            RenderSystem.popMatrix();
            RenderSystem.color4f(1, 1, 1, 1);

        }

        @Override
        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth - 12 && mouseY > y && mouseY < y + itemHeight) {
                EnchanterScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
            if (button.visible) {
                this.button.renderToolTip(matrixStack, mouseX, mouseY);
            }
        }

        private int calculateRequiredLevels() {
            Pair<EnchantmentInstance, Integer> result = Utils.tryApplyEnchantment(this.item.getKey(), EnchanterScreen.this.itemEnchantments, true);
            return result == null ? -1 : result.getRight();
        }

        private boolean hasSufficientLevels() {
            return EnchanterScreen.this.playerInventory.player.experienceLevel >= this.requiredLevels || EnchanterScreen.this.playerInventory.player.abilities.isCreativeMode;
        }
    }
}
