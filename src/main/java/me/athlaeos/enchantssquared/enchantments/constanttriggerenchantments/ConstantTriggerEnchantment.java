package me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ConstantTriggerEnchantment extends CustomEnchant {
    public abstract void execute(PlayerMoveEvent e, ItemStack stack, int level);

    public abstract void loadConfig();
}
