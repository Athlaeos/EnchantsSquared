package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.dom.AnvilRecipeOutcome;
import me.athlaeos.enchantssquared.dom.AnvilRecipeOutcomeState;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

    private EnchantsSquared plugin;
    private final CustomEnchantManager enchantmanager = CustomEnchantManager.getInstance();
    private int spamlimiter = 0;
    private int extra_cost;
    private String message;

    public AnvilListener(){
        plugin = EnchantsSquared.getPlugin();
        extra_cost = ConfigManager.getInstance().getConfig("config.yml").get().getInt("enchantment_extra_cost");
        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_allowed_enchants_exceeded");
        if (extra_cost < 0) extra_cost = 0;
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e) {
        if (e.getInventory().getItem(0) == null) return;
        if (e.getInventory().getItem(1) == null) return;
        ItemStack item1 = e.getInventory().getItem(0);
        ItemStack item2 = e.getInventory().getItem(1);
        ItemStack result = e.getResult();
        spamlimiter++;
        AnvilRecipeOutcome output = enchantmanager.combineItems(item1, item2, result);
        if (spamlimiter >= 3){
            AnvilInventory inventory = e.getInventory();
            if (output.getState() == AnvilRecipeOutcomeState.SUCCESSFUL){
                inventory.setRepairCost(inventory.getRepairCost() + extra_cost);
            } else if (output.getState() == AnvilRecipeOutcomeState.MAX_ENCHANTS_EXCEEDED){
                for (HumanEntity p : e.getViewers()){
                    p.sendMessage(Utils.chat(message));
                }
            }
            e.setResult(output.getOutput());
            spamlimiter = 0;
        }
    }
}
