package me.athlaeos.enchantssquared.enchantments.potionenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;

public abstract class PotionEffectEnchantment extends CustomEnchant {
    public abstract void execute(EntityPotionEffectEvent e, ItemStack i, int level);

    public abstract void loadConfig();
}
