package me.athlaeos.enchantssquared.enchantments.singletriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.managers.ItemAttributesManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class Steady extends SingleTriggerEnchantment {
    private double knockback_reduction_lv;

    public Steady(){
        this.enchantType = CustomEnchantType.KNOCKBACK_PROTECTION;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.knockback_protection";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(ItemStack i, int level) {
        double knockback_protection_lv = (knockback_reduction_lv * level) / 10;
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getAttributeModifiers() == null){
            ItemAttributesManager.getInstance().applyVanillaStats(i);
        }
        double defaultArmor = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        ItemAttributesManager.getInstance().addDefaultStat(i, Attribute.GENERIC_KNOCKBACK_RESISTANCE, defaultArmor + knockback_protection_lv);
    }

    @Override
    public void reverse(ItemStack i, int level) {
        double vanillaStrength = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (vanillaStrength == 0){
            ItemAttributesManager.getInstance().removeDefaultStat(i, Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        } else {
            ItemAttributesManager.getInstance().setAttributeStrength(i, Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                    vanillaStrength);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.steady.enchant_name");
        this.knockback_reduction_lv = config.getDouble("enchantment_configuration.steady.knockback_reduction_lv");
        this.enabled = config.getBoolean("enchantment_configuration.steady.enabled");
        this.weight = config.getInt("enchantment_configuration.steady.weight");
        this.book_only = config.getBoolean("enchantment_configuration.steady.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.steady.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.steady.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.steady.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.steady.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.steady.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.steady.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.steady.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.steady.trade_enabled");
        setIcon(config.getString("enchantment_configuration.steady.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.steady.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:steady is not valid, please correct it");
            }
        }
    }
}
