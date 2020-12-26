package de.cheaterpaul.enchantmentmachine.client.screen;

import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends Screen {

    private Object2IntMap<EnchantmentInstance> enchantments = new Object2IntArrayMap<>();
    private ScrollableListButton list;

    public EnchantmentScreen() {
        super(new StringTextComponent("Test"));
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(list = new ScrollableListButton(0,0,130,10,enchantments.size(),null, new TranslationTextComponent("testname"), this::onPress));
    }


    private void onPress(Integer pressed) {

    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){
        this.enchantments = enchantments;
        this.list.updateList(enchantments);
    }

}
