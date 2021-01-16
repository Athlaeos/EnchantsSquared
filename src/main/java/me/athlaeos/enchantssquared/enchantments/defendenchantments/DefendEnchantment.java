package me.athlaeos.enchantssquared.enchantments.defendenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public abstract class DefendEnchantment extends CustomEnchant {
    public abstract void execute(EntityDamageByEntityEvent e, ItemStack i, int level, LivingEntity damager, LivingEntity victim);

    public abstract void loadConfig();
}
