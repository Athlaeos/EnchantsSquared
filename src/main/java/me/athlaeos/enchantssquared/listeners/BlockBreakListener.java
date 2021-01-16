package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.dom.Version;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.BreakBlockEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.MinecraftVersionManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class BlockBreakListener implements Listener {
    private final CustomEnchantManager enchantManager;

    public BlockBreakListener(){
        enchantManager = CustomEnchantManager.getInstance();
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e){
        if (!e.isCancelled()){
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getBlock().getLocation(), "es-deny-all")) return;
            }
            ItemStack mainHandItem = e.getPlayer().getInventory().getItemInMainHand();
            if (mainHandItem.getType() != Material.AIR){
                if (mainHandItem.hasItemMeta()){
                    Map<CustomEnchant, Integer> enchants = enchantManager.getItemsEnchants(mainHandItem, CustomEnchantClassification.ON_BLOCK_BREAK);
                    for (CustomEnchant enchant : enchants.keySet()){
                        if (enchant instanceof BreakBlockEnchantment){
                            ((BreakBlockEnchantment) enchant).execute(e, mainHandItem, enchants.get(enchant));
                        }
                    }
                }
            }
        }
    }
}
