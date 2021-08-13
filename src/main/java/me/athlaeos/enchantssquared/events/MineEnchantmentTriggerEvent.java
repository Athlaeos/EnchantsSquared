package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MineEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Player player;
    private final Block minedBlock;

    public MineEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Player player, Block minedBlock) {
        super(stack, level, enchantment);
        this.player = player;
        this.minedBlock = minedBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getMinedBlock() {
        return minedBlock;
    }
}
