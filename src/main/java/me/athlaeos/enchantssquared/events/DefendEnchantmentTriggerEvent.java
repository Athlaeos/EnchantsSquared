package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DefendEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Entity damager;
    private final Entity defender;

    public DefendEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Entity damager, Entity defender) {
        super(stack, level, enchantment);
        this.damager = damager;
        this.defender = defender;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getDefender() {
        return defender;
    }
}
