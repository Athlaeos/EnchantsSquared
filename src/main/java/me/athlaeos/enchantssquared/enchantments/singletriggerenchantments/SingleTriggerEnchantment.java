package me.athlaeos.enchantssquared.enchantments.singletriggerenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SingleTriggerEnchantment extends CustomEnchant {
    public abstract void execute(ItemStack i, int level);

    public abstract void reverse(ItemStack i, int level);

    public abstract void loadConfig();
}
