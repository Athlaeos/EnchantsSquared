package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class PotionEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Entity entity;

    public PotionEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Entity entity) {
        super(stack, level, enchantment);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
