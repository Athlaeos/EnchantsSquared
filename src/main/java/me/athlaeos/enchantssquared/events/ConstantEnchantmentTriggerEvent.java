package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConstantEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Player player;

    public ConstantEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Player player) {
        super(stack, level, enchantment);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
