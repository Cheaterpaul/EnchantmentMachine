package de.cheaterpaul.enchantmentmachine.client.screen;

import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends Screen {

    public EnchantmentScreen(Object2IntMap<EnchantmentInstance> enchantments) {
        super( new StringTextComponent("Test"));
        updateEnchantments(enchantments);
    }
    
    public void updateEnchantments(Object2IntMap<EnchantmentInstance> enchantments){

    }

}
