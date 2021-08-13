package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantingEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    public EnchantingEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment) {
        super(stack, level, enchantment);
    }
}
