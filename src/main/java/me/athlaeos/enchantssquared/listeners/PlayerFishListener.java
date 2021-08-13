package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.SingleEnchant;
import me.athlaeos.enchantssquared.enchantments.fishenchantments.FishingEnchantment;
import me.athlaeos.enchantssquared.enchantments.mineenchantments.BreakBlockEnchantment;
import me.athlaeos.enchantssquared.events.FishingEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.events.MineEnchantmentTriggerEvent;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerFishListener implements Listener {
    private final double bookCustomEnchantChance;
    private final int bookCustomEnchantRolls;
    public PlayerFishListener(){
        bookCustomEnchantChance = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("custom_enchant_fish_rate");
        bookCustomEnchantRolls = Math.max(1, ConfigManager.getInstance().getConfig("config.yml").get().getInt("custom_enchant_fish_rolls"));
    }

    @EventHandler
    public void onFish(PlayerFishEvent e){
        Entity entity = e.getCaught();
        if (entity != null){
            if (entity instanceof Item){
                CustomEnchantManager manager = CustomEnchantManager.getInstance();
                Map<CustomEnchant, Integer> newCustomEnchantMap = new HashMap<>();
                ItemStack caughtItem = ((Item) entity).getItemStack();
                if (caughtItem.getType() == Material.ENCHANTED_BOOK){
                    if (RandomNumberGenerator.getRandom().nextDouble() * 100 <= bookCustomEnchantChance){
                        for (int i = 0; i < RandomNumberGenerator.getRandom().nextInt(bookCustomEnchantRolls) + 1; i++){
                            SingleEnchant chosenEnchant = manager.pickRandomEnchant(new ArrayList<>(manager.getAllEnchants().values()));
                            newCustomEnchantMap.put(chosenEnchant.getEnchantment(), chosenEnchant.getLevel());
                        }
                        manager.setItemEnchants(caughtItem, newCustomEnchantMap);
                    }
                }
            }
        }


        ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
        if (heldItem.getType() != Material.FISHING_ROD) heldItem = e.getPlayer().getInventory().getItemInOffHand();
        if (heldItem.getType() == Material.FISHING_ROD){
            if (heldItem.hasItemMeta()){
                Map<CustomEnchant, Integer> enchants = CustomEnchantManager.getInstance().getItemsEnchantsFromPDC(heldItem);
                for (CustomEnchant enchant : enchants.keySet()){
                    if (enchant instanceof FishingEnchantment){
                        FishingEnchantmentTriggerEvent event = new FishingEnchantmentTriggerEvent(heldItem, enchants.get(enchant), enchant, e.getPlayer());
                        EnchantsSquared.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()){
                            ((FishingEnchantment) enchant).execute(e, heldItem, event.getLevel());
                        }
                    }
                }
            }
        }
    }
}
