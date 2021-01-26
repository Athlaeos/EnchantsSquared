package me.athlaeos.enchantssquared.dom;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomEnchant {
    protected String enchantLore;
    protected String enchantDescription;
    protected String requiredPermission;
    protected int weight;
    protected int max_level = 0;
    protected List<Enchantment> conflictsWith = new ArrayList<>();
    protected List<Material> compatibleItems = new ArrayList<>();
    protected List<String> compatibleItemStrings = new ArrayList<>();
    protected boolean enabled;
    protected boolean book_only;
    protected int max_level_table = 0;
    protected YamlConfiguration config;
    protected CustomEnchantEnum enchantType = CustomEnchantEnum.UNASSIGNED;

    public CustomEnchant() {
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isBook_only() {
        return book_only;
    }

    public int getMax_level_table() {
        return max_level_table;
    }

    public void setMax_level_table(int max_level_table) {
        this.max_level_table = max_level_table;
    }

    public String getEnchantLore() {
        return enchantLore;
    }

    public int getWeight() {
        return weight;
    }

    public int getMax_level() {
        return max_level;
    }

    public List<String> getCompatibleItemStrings() {
        return compatibleItemStrings;
    }

    public List<Enchantment> getConflictsWith() {
        return conflictsWith;
    }

    public List<Material> getCompatibleItems() {
        return compatibleItems;
    }

    public List<Material> addCompatibleItem(String itemName){
        try{
            this.compatibleItems.add(Material.valueOf(itemName));
        } catch (IllegalArgumentException ignored){
        }
        return compatibleItems;
    }

    public List<Material> addCompatibleItem(Material itemMaterial){
        compatibleItems.add(itemMaterial);
        return compatibleItems;
    }

    public CustomEnchantEnum getEnchantType() {
        return enchantType;
    }

    public String getEnchantDescription() {
        return enchantDescription;
    }
}
