package me.athlaeos.enchantssquared.utils;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.managers.RandomNumberGenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MineUtils {

    private static Map<Material, Material> furnaceRegularRecipes;
    private static Map<Material, Material> furnaceFortuneRecipes;

    public static void reload(){
        furnaceFortuneRecipes = null;
        furnaceRegularRecipes = null;
    }

    public static Collection<ItemStack> cookBlock(ItemStack tool, Block block){
        loadConfig();
        List<ItemStack> items = new ArrayList<>();
        if (furnaceFortuneRecipes.containsKey(block.getType())){
            int fortuneStrength = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            int amountToDrop = 1 + (RandomNumberGenerator.getRandom().nextInt(fortuneStrength + 1));
            items.add(new ItemStack(furnaceFortuneRecipes.get(block.getType()), amountToDrop));
            return items;
        } else if (furnaceRegularRecipes.containsKey(block.getType())){
            items.add(new ItemStack(furnaceRegularRecipes.get(block.getType()), 1));
            return items;
        }
        return block.getDrops(tool);
    }

    private static void loadConfig(){
        if (furnaceRegularRecipes == null){
            YamlConfiguration smeltConfig = ConfigManager.getInstance().getConfig("smeltblocksrecipes.yml").get();
            furnaceRegularRecipes = new HashMap<>();
            for (String s : smeltConfig.getConfigurationSection("fortune_ignored").getKeys(false)){
                try{
                    String recipeResult = smeltConfig.getString("fortune_ignored."+s);
                    if (recipeResult == null) continue;
                    furnaceRegularRecipes.put(Material.matchMaterial(s), Material.matchMaterial(recipeResult));
                } catch (IllegalArgumentException | NullPointerException ignored){
                }
            }
        }
        if (furnaceFortuneRecipes == null){
            YamlConfiguration smeltConfig = ConfigManager.getInstance().getConfig("smeltblocksrecipes.yml").get();
            furnaceFortuneRecipes = new HashMap<>();
            for (String s : smeltConfig.getConfigurationSection("fortune_affected").getKeys(false)){
                try{
                    String recipeResult = smeltConfig.getString("fortune_affected."+s);
                    if (recipeResult == null) continue;
                    furnaceFortuneRecipes.put(Material.matchMaterial(s), Material.matchMaterial(recipeResult));
                } catch (IllegalArgumentException | NullPointerException ignored){
                }
            }
        }
    }
}
