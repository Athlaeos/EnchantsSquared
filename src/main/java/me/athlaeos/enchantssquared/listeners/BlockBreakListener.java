package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.BreakBlockEnchantment;
import me.athlaeos.enchantssquared.events.DefendEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.events.MineEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.enchantmanagers.ExcavationBlockFaceManager;
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
            if (ExcavationBlockFaceManager.getInstance().getBlockFaceMap().get(e.getPlayer().getUniqueId()) == null){
                return;
            }
            if (!e.getPlayer().hasPermission("es.noregionrestrictions")){
                if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getBlock().getLocation(), "es-deny-all")) return;
            }
            ItemStack mainHandItem = e.getPlayer().getInventory().getItemInMainHand();
            if (mainHandItem.getType() != Material.AIR || mainHandItem.getType() != Material.ENCHANTED_BOOK){
                if (mainHandItem.hasItemMeta()){
                    Map<CustomEnchant, Integer> enchants = enchantManager.getItemsEnchantsFromPDC(mainHandItem);
                    for (CustomEnchant enchant : enchants.keySet()){
                        if (enchant instanceof BreakBlockEnchantment){
                            MineEnchantmentTriggerEvent event = new MineEnchantmentTriggerEvent(mainHandItem, enchants.get(enchant), enchant, e.getPlayer(), e.getBlock());
                            EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                ((BreakBlockEnchantment) enchant).execute(e, mainHandItem, event.getLevel());
                            }
                        }
                    }
                }
            }
        }
    }
}
