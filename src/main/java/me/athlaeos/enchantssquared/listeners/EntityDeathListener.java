package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.CustomEnchantClassification;
import me.athlaeos.enchantssquared.enchantments.killenchantments.KillEnchantment;
import me.athlaeos.enchantssquared.enchantments.killenchantments.Soulbound;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EntityDeathListener implements Listener {
    private CustomEnchantManager manager = null;

    public EntityDeathListener(){

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if (manager == null) manager = CustomEnchantManager.getInstance();
        if (!e.getEntity().hasPermission("es.noregionrestrictions")){
            if (WorldguardHook.getWorldguardHook().isLocationInRegionWithFlag(e.getEntity().getLocation(), "es-deny-all")) return;
        }

        if (e.getEntity() instanceof HumanEntity){
            for (ItemStack i : ((HumanEntity) e.getEntity()).getInventory().getContents()){
                if (i == null) continue;
                if (i.getType() == Material.AIR) continue;
                Map<CustomEnchant, Integer> itemEnchants = manager.getItemsEnchants(i, CustomEnchantClassification.ON_KILL);
                for (CustomEnchant en : itemEnchants.keySet()){
                    if (en instanceof Soulbound){
                        ((Soulbound) en).execute(e, i, 0, null, e.getEntity());
                        break;
                    }
                }
            }
        }

        if (e.getEntity().getKiller() != null){
            Player killer = e.getEntity().getKiller();
            ItemStack heldItem = killer.getInventory().getItemInMainHand();

            if (heldItem.getType() != Material.AIR){
                Map<CustomEnchant, Integer> enchants = manager.getItemsEnchants(heldItem, CustomEnchantClassification.ON_KILL);

                for (CustomEnchant en : enchants.keySet()){
                    if (en instanceof KillEnchantment){
                        ((KillEnchantment) en).execute(e, heldItem, enchants.get(en), killer, e.getEntity());
                    }
                }
            }
        }
    }
}
