package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.Debug;
import me.athlaeos.enchantssquared.dom.AnvilRecipeOutcome;
import me.athlaeos.enchantssquared.dom.AnvilRecipeOutcomeState;
import me.athlaeos.enchantssquared.enchantments.StandardGlintEnchantment;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class AnvilListener implements Listener {

    private EnchantsSquared plugin;
    private final CustomEnchantManager enchantmanager = CustomEnchantManager.getInstance();
    private int extra_cost;
    private String message;

    public AnvilListener(){
        plugin = EnchantsSquared.getPlugin();
        extra_cost = ConfigManager.getInstance().getConfig("config.yml").get().getInt("enchantment_extra_cost");
        message = ConfigManager.getInstance().getConfig("translations.yml").get().getString("warning_allowed_enchants_exceeded");
        if (extra_cost < 0) extra_cost = 0;
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onAnvilUse(PrepareAnvilEvent e) {
        if (e.getInventory().getItem(0) == null) return;
        if (e.getInventory().getItem(1) == null) return;
        ItemStack item1 = e.getInventory().getItem(0);
        ItemStack item2 = e.getInventory().getItem(1);
        ItemStack result = e.getResult();
        AnvilRecipeOutcome output = enchantmanager.combineItems(item1, item2, result);
        AnvilInventory inventory = e.getInventory();
        if (output.getState() == AnvilRecipeOutcomeState.SUCCESSFUL){
            if (e.getInventory().getViewers().size() > 0){
                HumanEntity human = e.getInventory().getViewers().get(0);
                Debug.log(human, "&7onAnvilUse() PrepareAnvilEvent listener &fcombinement of items successful");
            }
            e.setResult(output.getOutput());
        } else if (output.getState() == AnvilRecipeOutcomeState.MAX_ENCHANTS_EXCEEDED){
            if (e.getInventory().getViewers().size() > 0){
                HumanEntity human = e.getInventory().getViewers().get(0);
                Debug.log(human, "&7onAnvilUse() PrepareAnvilEvent listener &fcustom max enchantments exceeded");
            }
            e.setResult(null);
        }
        if (output.getOutput() != null){
            if (output.getOutput().getEnchantments().containsKey(StandardGlintEnchantment.getEnsquaredGlint())){
                inventory.setRepairCost(inventory.getRepairCost() + extra_cost);
            }
        } else {
            if (e.getInventory().getViewers().size() > 0){
                HumanEntity human = e.getInventory().getViewers().get(0);
                Debug.log(human, "&7onAnvilUse() PrepareAnvilEvent &fanvil result is null");
            }
        }
    }

//    @EventHandler
//    public void onAnvilClick(InventoryClickEvent e){
//        if (e.getClickedInventory() instanceof AnvilInventory){
//            AnvilInventory inv = (AnvilInventory) e.getClickedInventory();
//            System.out.println("output costs: " + inv.getRepairCost());
//            ItemStack output = inv.getItem(2);
//
//        }
//    }
}
