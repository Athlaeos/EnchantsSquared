package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class AttackEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Entity damager;
    private final Entity victim;

    public AttackEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Entity damager, Entity victim) {
        super(stack, level, enchantment);
        this.damager = damager;
        this.victim = victim;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getVictim() {
        return victim;
    }
}
