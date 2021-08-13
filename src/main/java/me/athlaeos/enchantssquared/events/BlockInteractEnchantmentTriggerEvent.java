package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockInteractEnchantmentTriggerEvent extends EnchantmentTriggerEvent{
    private final Player player;
    private final Block clickedBlock;

    public BlockInteractEnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment, Player player, Block clickedBlock) {
        super(stack, level, enchantment);
        this.player = player;
        this.clickedBlock = clickedBlock;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }
}
