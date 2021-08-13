package me.athlaeos.enchantssquared.enchantments.singletriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.managers.ItemAttributesManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ReinforcedPlating extends SingleTriggerEnchantment {
    private double armor_lv;

    public ReinforcedPlating(){
        this.enchantType = CustomEnchantType.PLATING;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.plating";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(ItemStack i, int level) {
        double armor = (this.armor_lv * level);
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getAttributeModifiers() == null){
            ItemAttributesManager.getInstance().applyVanillaStats(i);
        }
        double defaultHealth = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_ARMOR);
        ItemAttributesManager.getInstance().addDefaultStat(i, Attribute.GENERIC_ARMOR, defaultHealth + armor);
    }

    @Override
    public void reverse(ItemStack i, int level) {
        double vanillaStrength = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_ARMOR);
        if (vanillaStrength == 0){
            ItemAttributesManager.getInstance().removeDefaultStat(i, Attribute.GENERIC_ARMOR);
        } else {
            ItemAttributesManager.getInstance().setAttributeStrength(i, Attribute.GENERIC_ARMOR,
                    vanillaStrength);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.reinforced_plating.enchant_name");
        this.armor_lv = config.getDouble("enchantment_configuration.reinforced_plating.armor_lv");
        this.enabled = config.getBoolean("enchantment_configuration.reinforced_plating.enabled");
        this.weight = config.getInt("enchantment_configuration.reinforced_plating.weight");
        this.book_only = config.getBoolean("enchantment_configuration.reinforced_plating.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.reinforced_plating.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.reinforced_plating.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.reinforced_plating.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.reinforced_plating.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.reinforced_plating.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.reinforced_plating.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.reinforced_plating.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.reinforced_plating.trade_enabled");

        setIcon(config.getString("enchantment_configuration.reinforced_plating.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.reinforced_plating.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:reinforced_plating is not valid, please correct it");
            }
        }
    }
}
