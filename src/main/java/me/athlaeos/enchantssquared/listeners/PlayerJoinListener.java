package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        CustomEnchantManager manager = CustomEnchantManager.getInstance();
        for (ItemStack i : p.getInventory().getContents()){
            if (i != null){
                manager.updateItem(i);
            }
        }
    }
}
