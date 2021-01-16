package me.athlaeos.enchantssquared.enchantments.mineenchantments;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BreakBlockEnchantment extends CustomEnchant {
    public abstract void execute(BlockBreakEvent e, ItemStack i, int level);

    public abstract void loadConfig();
}
