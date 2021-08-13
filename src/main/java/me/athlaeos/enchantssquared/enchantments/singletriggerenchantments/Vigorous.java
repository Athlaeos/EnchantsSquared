package me.athlaeos.enchantssquared.enchantments.singletriggerenchantments;

import me.athlaeos.enchantssquared.configs.ConfigManager;
import me.athlaeos.enchantssquared.dom.CustomEnchantType;
import me.athlaeos.enchantssquared.dom.MaterialClassType;
import me.athlaeos.enchantssquared.enchantments.constanttriggerenchantments.ConstantTriggerEnchantment;
import me.athlaeos.enchantssquared.hooks.WorldguardHook;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.managers.ItemAttributesManager;
import me.athlaeos.enchantssquared.managers.ItemMaterialManager;
import me.athlaeos.enchantssquared.utils.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

public class Vigorous extends SingleTriggerEnchantment {
    private double health_lv;
    private boolean reset_health;

    public Vigorous(){
        this.enchantType = CustomEnchantType.VIGOROUS;
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.requiredPermission = "es.enchant.vigorous";
        loadFunctionalItemStrings(Collections.singletonList("ALL"));
        loadConfig();
    }

    @Override
    public void execute(ItemStack i, int level) {
        double health_extra = (this.health_lv * level);
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getAttributeModifiers() == null){
            ItemAttributesManager.getInstance().applyVanillaStats(i);
        }
        double defaultHealth = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_MAX_HEALTH);
        ItemAttributesManager.getInstance().addDefaultStat(i, Attribute.GENERIC_MAX_HEALTH, defaultHealth + health_extra);
    }

    @Override
    public void reverse(ItemStack i, int level) {
        double vanillaStrength = ItemAttributesManager.getInstance().getVanillaAttributeStrength(i, Attribute.GENERIC_MAX_HEALTH);
        if (vanillaStrength == 0){
            ItemAttributesManager.getInstance().removeDefaultStat(i, Attribute.GENERIC_MAX_HEALTH);
        } else {
            ItemAttributesManager.getInstance().setAttributeStrength(i, Attribute.GENERIC_MAX_HEALTH,
                    vanillaStrength);
        }
    }

    @Override
    public void loadConfig() {
        this.enchantLore = config.getString("enchantment_configuration.vigorous.enchant_name");
        this.health_lv = config.getDouble("enchantment_configuration.vigorous.health_lv");
        this.enabled = config.getBoolean("enchantment_configuration.vigorous.enabled");
        this.weight = config.getInt("enchantment_configuration.vigorous.weight");
        this.book_only = config.getBoolean("enchantment_configuration.vigorous.book_only");
        this.max_level_table = config.getInt("enchantment_configuration.vigorous.max_level_table");
        this.max_level = config.getInt("enchantment_configuration.vigorous.max_level");
        this.enchantDescription = config.getString("enchantment_configuration.vigorous.description");
        this.tradeMinCostBase = config.getInt("enchantment_configuration.vigorous.trade_cost_base_lower");
        this.tradeMaxCostBase = config.getInt("enchantment_configuration.vigorous.trade_cost_base_upper");
        this.tradeMinCostLv = config.getInt("enchantment_configuration.vigorous.trade_cost_lv_lower");
        this.tradeMaxCostLv = config.getInt("enchantment_configuration.vigorous.trade_cost_base_upper");
        this.availableForTrade = config.getBoolean("enchantment_configuration.vigorous.trade_enabled");

        this.reset_health = config.getBoolean("enchantment_configuration.vigorous.reset_health");

        setIcon(config.getString("enchantment_configuration.vigorous.icon"));

        this.compatibleItemStrings = config.getStringList("enchantment_configuration.vigorous.compatible_with");
        for (String s : compatibleItemStrings){
            try {
                MaterialClassType type = MaterialClassType.valueOf(s);
                this.compatibleItems.addAll(ItemMaterialManager.getInstance().getMaterialsFromType(type));
            } catch (IllegalArgumentException e){
                System.out.println("Material category " + s + " in the config:vigorous is not valid, please correct it");
            }
        }
    }

    public boolean reset_health() {
        return reset_health;
    }
}
