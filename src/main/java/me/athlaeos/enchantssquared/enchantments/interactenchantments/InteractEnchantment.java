package me.athlaeos.enchantssquared.enchantments.interactenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public abstract class InteractEnchantment extends CustomEnchant {
    public abstract void execute(PlayerInteractEvent e, ItemStack item, int level);

    public abstract void loadConfig();
}
