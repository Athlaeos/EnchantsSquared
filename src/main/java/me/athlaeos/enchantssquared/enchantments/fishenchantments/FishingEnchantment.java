package me.athlaeos.enchantssquared.enchantments.fishenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public abstract class FishingEnchantment extends CustomEnchant {
    public abstract void execute(PlayerFishEvent e, ItemStack i, int level);

    public abstract void loadConfig();
}
