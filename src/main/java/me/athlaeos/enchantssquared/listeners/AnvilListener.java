package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private Main plugin;
    private final CustomEnchantManager enchantmanager = CustomEnchantManager.getInstance();
    private int spamlimiter = 0;
    private String message;

    public AnvilListener(){
        plugin = Main.getPlugin();
        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_allowed_enchants_exceeded");
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e) {
        if (e.getInventory().getItem(0) == null) return;
        if (e.getInventory().getItem(1) == null) return;
        if (e.getResult() == null) return;
        ItemStack item1 = e.getInventory().getItem(0);
        ItemStack item2 = e.getInventory().getItem(1);
        ItemStack result = e.getResult();
        spamlimiter++;
        if (!enchantmanager.combineItems(item1, item2, result)){
            e.setResult(null);
            if (spamlimiter >= 3){
                for (HumanEntity p : e.getViewers()){
                    p.sendMessage(Utils.chat(message));
                }
                spamlimiter = 0;
            }
        }
    }
}
