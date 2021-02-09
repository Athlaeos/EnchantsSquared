package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.interactenchantments.InteractEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.enchantmanagers.ExcavationBlockFaceManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BlockInteractListener implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent e){
        if (e.getAction() == Action.LEFT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                ExcavationBlockFaceManager.getInstance().getBlockFaceMap().put(e.getPlayer().getUniqueId(), e.getBlockFace());
            }
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                    if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getClickedBlock().getLocation(), "es-deny-all")) return;
                }
                ItemStack clickedItem;
                if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR){
                    clickedItem = e.getPlayer().getInventory().getItemInMainHand();
                } else if (e.getPlayer().getInventory().getItemInOffHand().getType() != Material.AIR){
                    clickedItem = e.getPlayer().getInventory().getItemInOffHand();
                } else {
                    return;
                }

                Map<CustomEnchant, Integer> enchants = CustomEnchantManager.getInstance().getItemsEnchantsFromPDC(clickedItem);
                for (CustomEnchant en : enchants.keySet()){
                    if (en instanceof InteractEnchantment){
                        ((InteractEnchantment) en).execute(e, clickedItem, enchants.get(en));
                    }
                }
            }
        }
    }
}
