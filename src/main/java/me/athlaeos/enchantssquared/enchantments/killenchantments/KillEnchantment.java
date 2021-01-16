package me.athlaeos.enchantssquared.enchantments.killenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public abstract class KillEnchantment extends CustomEnchant {
    public abstract void execute(EntityDeathEvent e, ItemStack stack, int level, LivingEntity killer, LivingEntity killed);

    public abstract void loadConfig();
}
