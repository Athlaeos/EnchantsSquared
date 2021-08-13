package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.potionenchantments.PotionEffectEnchantment;
import me.athlaeos.enchantssquared.events.MineEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.events.PotionEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PotionEffectListener implements Listener {

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e){
        if (!e.isCancelled()){
            CustomEnchantManager manager = CustomEnchantManager.getInstance();
            for (ItemStack i : Utils.getEntityEquipment(e.getEntity(), true)){
                if (i.getType() == Material.ENCHANTED_BOOK) continue;
                Map<CustomEnchant, Integer> enchants = manager.getItemsEnchantsFromPDC(i);
                for (CustomEnchant enchant : enchants.keySet()){
                    if (enchant instanceof PotionEffectEnchantment){
                        PotionEnchantmentTriggerEvent event = new PotionEnchantmentTriggerEvent(i, enchants.get(enchant), enchant, e.getEntity());
                        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()){
                            ((PotionEffectEnchantment) enchant).execute(e, i, enchants.get(enchant));
                        }
                    }
                }
            }
        }
    }
}
