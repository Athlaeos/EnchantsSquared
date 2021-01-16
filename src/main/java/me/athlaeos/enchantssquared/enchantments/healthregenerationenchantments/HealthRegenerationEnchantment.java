package me.athlaeos.enchantssquared.enchantments.healthregenerationenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public abstract class HealthRegenerationEnchantment extends CustomEnchant {
    public abstract void execute(EntityRegainHealthEvent e);

    public abstract void loadConfig();
}
