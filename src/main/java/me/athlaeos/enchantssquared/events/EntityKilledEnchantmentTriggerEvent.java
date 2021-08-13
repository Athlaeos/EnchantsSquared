package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityKilledEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Player killer;
    private final LivingEntity killed;

    public EntityKilledEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Player killer, LivingEntity killed) {
        super(stack, level, enchantment);
        this.killer = killer;
        this.killed = killed;
    }

    public Player getKiller() {
        return killer;
    }

    public LivingEntity getKilled() {
        return killed;
    }
}
