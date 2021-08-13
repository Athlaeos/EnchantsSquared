package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HealthRegenEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final LivingEntity entity;

    public HealthRegenEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, LivingEntity entity) {
        super(stack, level, enchantment);
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
