package me.athlaeos.enchantssquared.events;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EnchantmentTriggerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected boolean isCancelled = false;
    protected ItemStack stack;
    protected int level;
    protected CustomEnchant enchantment;

    public EnchantmentTriggerEvent(ItemStack stack, int level, CustomEnchant enchantment){
        this.stack = stack;
        this.level = level;
        this.enchantment = enchantment;
    }

    public int getLevel() {
        return level;
    }

    public CustomEnchant getEnchantment() {
        return enchantment;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
