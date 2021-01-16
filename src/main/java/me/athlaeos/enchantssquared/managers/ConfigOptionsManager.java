package me.athlaeos.enchantssquared.managers;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigOptionsManager {
    private static ConfigOptionsManager manager = null;

    private final int maxEnchants;
    private final int maxEnchantsFromTable;
    private final int levelMinimum;

    public ConfigOptionsManager(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();

        maxEnchantsFromTable = config.getInt("enchantment_table_rolls");
        maxEnchants = config.getInt("max_enchants");
        levelMinimum = config.getInt("level_minimum");
    }

    public static ConfigOptionsManager getInstance(){
        if (manager == null){
            manager = new ConfigOptionsManager();
        }
        return manager;
    }

    public int getMaxEnchants() {
        return maxEnchants;
    }

    public int getLevelMinimum() {
        return levelMinimum;
    }

    public int getMaxEnchantsFromTable() {
        return maxEnchantsFromTable;
    }
}
