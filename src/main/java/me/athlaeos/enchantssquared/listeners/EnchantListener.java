package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.main.Main;
import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.PlayerOptionsManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantListener implements Listener {

    private Main plugin;
    private int min_enchant_level_needed;
    private PlayerOptionsManager manager = PlayerOptionsManager.getManager();
    private CustomEnchantManager enchantmanager = CustomEnchantManager.getInstance();

    public EnchantListener(){
        plugin = Main.getPlugin();
        min_enchant_level_needed = ConfigManager.getInstance().getConfig("config.yml").get().getInt("custom_enchant_rate");
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e){
        if (e.isCancelled()) return;
        Player p = e.getEnchanter();
        if (!manager.doesPlayerWantEnchants(p)) return;
        if (e.getExpLevelCost() >= plugin.getConfig().getInt("level_minimum")){
            int randomEnchantNumber = RandomNumberGenerator.getRandom().nextInt(100) + 1;
            if (randomEnchantNumber <= min_enchant_level_needed){
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ItemStack item = e.getInventory().getItem(0);
                    enchantmanager.enchantForPlayer(item, e.getEnchanter());
                }, 1L);
            }
        }
    }
}
