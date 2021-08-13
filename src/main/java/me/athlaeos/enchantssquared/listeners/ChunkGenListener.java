package me.athlaeos.enchantssquared.listeners;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchant;
import me.athlaeos.enchantssquared.dom.SingleEnchant;
import me.athlaeos.enchantssquared.main.EnchantsSquared;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkGenListener implements Listener {
    private final double bookCustomEnchantChance;
    private final int bookCustomEnchantRolls;
    public ChunkGenListener(){
        bookCustomEnchantChance = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("custom_enchant_dungeon_rate");
        bookCustomEnchantRolls = Math.max(1, ConfigManager.getInstance().getConfig("config.yml").get().getInt("custom_enchant_dungeon_rolls"));
    }

//    @EventHandler
    public void onChunkGen(ChunkPopulateEvent e){
        for (BlockState block : e.getChunk().getTileEntities()){
            if (block.getBlock().getType() == Material.CHEST || block.getBlock().getType() == Material.CHEST_MINECART){
                Chest chest = (Chest) block;
                CustomEnchantManager manager = CustomEnchantManager.getInstance();
                for (ItemStack item : chest.getBlockInventory().getContents()){
                    if (item != null){
                        System.out.println("chest contains " + item.getType());
                        if (item.getType() == Material.ENCHANTED_BOOK){
                            if (RandomNumberGenerator.getRandom().nextDouble() * 100 <= bookCustomEnchantChance){
                                Map<CustomEnchant, Integer> newCustomEnchantMap = new HashMap<>();
                                for (int i = 0; i < RandomNumberGenerator.getRandom().nextInt(bookCustomEnchantRolls) + 1; i++){
                                    SingleEnchant chosenEnchant = manager.pickRandomEnchant(new ArrayList<>(manager.getAllEnchants().values()));
                                    newCustomEnchantMap.put(chosenEnchant.getEnchantment(), chosenEnchant.getLevel());
                                }
                                manager.setItemEnchants(item, newCustomEnchantMap);
                            }
                        }
                    }
                }
            }
        }
    }
}
