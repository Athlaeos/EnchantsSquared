package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.managers.enchantmanagers.SoulboundItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        for (ItemStack i : SoulboundItemManager.getInstance().executeOnRespawn(e.getPlayer().getUniqueId())){
            if (e.getPlayer().getInventory().firstEmpty() < 0){
                e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), i);
            } else {
                e.getPlayer().getInventory().addItem(i);
            }
        }
    }
}
